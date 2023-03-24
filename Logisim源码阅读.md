# Logisim源码

## O、logisim-evolution简介

​		Logisim本质上是一个单线程Java工程，主方法为Github的logisim-evolution源码中的`logisim-evolution-master/src/main/java/com/cburch/logisim/Main.java`，采用了典型的面向对象开发。整个软件大概有20万行代码，开源且功能强大。

## 一、数学部件

​		每一个数学部件都依赖于两个类，一个类以其自身命名，另一个类为HdlGeneratorFactory类。我们从一个数学部件Adder来切入Logisim的画布系统。

### Adder

​		Adder为封装好的全加器，源码在std目录下的arith文件夹中。

​		`Adder()`是Adder的构造方法，包括了端口定义、属性等重要的信息。

```java
public Adder() {
  super(_ID, S.getter("adderComponent"), new AdderHdlGeneratorFactory());
  setAttributes(new Attribute[] {StdAttr.WIDTH}, new Object[] {BitWidth.create(8)});			// 标准宽度，初始宽度为8
  setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
  setOffsetBounds(Bounds.create(-40, -20, 40, 40));
  setIcon(new ArithmeticIcon("+"));			// Icon是与左侧工具栏中相关的图标

  final var ps = new Port[5];
  // 表示端口相对于元件位置的坐标，端口的类型以及端口的宽度；这也是端口需要具备的几个属性
  ps[IN0] = new Port(-40, -10, Port.INPUT, StdAttr.WIDTH);
  ps[IN1] = new Port(-40, 10, Port.INPUT, StdAttr.WIDTH);
  ps[OUT] = new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH);
  ps[C_IN] = new Port(-20, -20, Port.INPUT, 1);
  ps[C_OUT] = new Port(-20, 20, Port.OUTPUT, 1);
  ps[IN0].setToolTip(S.getter("adderInputTip"));
  ps[IN1].setToolTip(S.getter("adderInputTip"));
  ps[OUT].setToolTip(S.getter("adderOutputTip"));
  ps[C_IN].setToolTip(S.getter("adderCarryInTip"));
  ps[C_OUT].setToolTip(S.getter("adderCarryOutTip"));
  setPorts(ps);
}
```

​		`paintInstance`是用于绘制Adder的主要方法，该方法在画布上绘制了Adder类的形状、端口、注释等信息。

```java
public void paintInstance(InstancePainter painter) {
    Graphics g = painter.getGraphics();
    painter.drawBounds();		// 绘制边框，为一个正方形

    g.setColor(Color.GRAY);		// 设置主题颜色，设置后貌似字体为灰色
    painter.drawPort(IN0);		// IN0~C_OUT实际上为1~5的编号，象征了端口
    painter.drawPort(IN1);
    painter.drawPort(OUT);
    painter.drawPort(C_IN, "c in", Direction.NORTH);
    painter.drawPort(C_OUT, "c out", Direction.SOUTH);

  	// loc为表示模块位置的实例，每个模块都以某个点作为位置
    Location loc = painter.getLocation();	
    int x = loc.getX();
    int y = loc.getY();
    GraphicsUtil.switchToWidth(g, 2);
    g.setColor(Color.BLACK);
    g.drawLine(x - 15, y, x - 5, y);		// 两个drawLine绘制一个加号
    g.drawLine(x - 10, y - 5, x - 10, y + 5);
    GraphicsUtil.switchToWidth(g, 1);
}
```

​		`propagate`方法用于给输出端口赋值，值被存放在Value对象当中，并且可以提供端口号，通过`InstanceState`的`getPortValue`方法来取值。除此之外，Adder还配置了一个静态的`computeSum`方法用于计算输出。

​		可以大概了解到数学部件的编写方法，依据此可以编写出自己的数学部件。

### 边框绘制

​		以Adder为例，绘制边框的代码如下：

```java
painter.drawBounds();
```

​		`painter`是InstancePainter对象的实例，其`drawBounds`方法如下：

```java
public void drawBounds() {
		context.drawBounds(comp);
}
```

​		`context`又是ComponentDrawContext对象的实例，继续考察其`drawBounds`方法：

```java
public void drawBounds(Component comp) {
  	/* g是一个用于绘制的工具类的实例 */
    GraphicsUtil.switchToWidth(g, 2);		// 和设置线条宽度有关的方法
    g.setColor(Color.BLACK);		// 设置边框颜色
    final var bds = comp.getBounds();		// 得到边框有关的信息
    g.drawRect(bds.getX(), bds.getY(), ...);	// 绘制矩形
    GraphicsUtil.switchToWidth(g, 1);
}
```

​		通过这三个蹭蹭掉用的方法，成功绘制出了边框。当然，这其中涉及到了一些新的类，包括Component类、GraphicsUtil类、Graphics类、InstancePainter类等。有了其public方法的使用示例，可以更深一步挖掘其绘制原理。

## 二、元件绘制体系

### 1.InstancePainter

​		这个类在`/Users/enqurance/Desktop/BUAA/logisim-evolution-master/src/main/java/com/cburch/logisim/instance`文件夹下，是Adder绘制时第一阶段调用的类。

​		这个类的名称直译一下就是实例绘制器。如上所述，Adder为一个类，那么每一个在画布上的Adder部件可以看作一个实例，这个类的对象可以用于绘制这些实例。

​		InstancePainter提供很多绘制方法，包括：

- `drawBounds`方法，用于绘制边框
- `drawLabel`方法，用于绘制标签
- `drawPort`方法，用于绘制端口，包括两个重载方法

​		等等。

​		这些方法的使用还和ComponentDrawContext和InstanceComponent类有很大的关联，在这些方法内部使用了该类的实例，可以继续进行考察。

### 2.InstanceComponent

​		元件实例类，和InstancePainter在同一路径下。这一个类包括了原件的很多重要的属性：

|            属性             |     描述     |
| :-------------------------: | :----------: |
|        Location loc;        | 描述元件位置 |
|       Bounds bounds;        | 描述元件边框 |
|    List<Port> portList;     | 描述元件端口 |
| InstanceTextField textField | 描述元件文本 |
|           ......            |    ......    |

​		除此之外，该类还有一个draw方法，用于绘制元件：

```java
  @Override
  public void draw(ComponentDrawContext context) {
    final var painter = context.getInstancePainter();
    painter.setInstance(this);
    factory.paintInstance(painter);
    if (doMarkInstance) {
      final var g = painter.getGraphics();
      final var bds = painter.getBounds();
      final var current = g.getColor();
      g.setColor(Netlist.DRC_INSTANCE_MARK_COLOR);
      GraphicsUtil.switchToWidth(g, 2);
      g.drawRoundRect(
          bds.getX() - 10, bds.getY() - 10, bds.getWidth() + 20, bds.getHeight() + 20, 40, 40);
      GraphicsUtil.switchToWidth(g, 1);
      g.setColor(current);
    }
  }
```

​		此方法首先声明了一个painter实例，并传入自身实例设置painter实例，随后通过factory的paintInstance方法来绘制。**if条件语句内的代码未必会执行**，所以绘制的重点在painter与factory中。painter来自ComponentDrawContext的一个实例，而factory则是InstanceFactory的实例。

​		接下来，需要考察ComponentDrawContext和InstanceFactory两个类。

### 3.ComponentDrawContext

​		这一个类是用于绘制组件内容的类，对外提供了很多关于组件绘制的方法。其中比较重要的是DrawBounds方法。

```java
  public void drawBounds(Component comp) {
    GraphicsUtil.switchToWidth(g, 2);
    g.setColor(Color.BLACK);		// 设置颜色
    final var bds = comp.getBounds();			// 获取边框信息
    System.out.println(bds.getWidth() +  " " + bds.getHeight());
    g.drawRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());		// 绘制矩形
  //    g.drawOval(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
    GraphicsUtil.switchToWidth(g, 1);
  }
```

​		绘制边框所需要的信息从Component类的实例中获得，并且调用Graphics方法的实例来drawRect方法来绘制边框。

### 4.InstanceFactory

​		这一个类下有第一个比较重要的方法，paintGhost，用于绘制鼠标拖动时组件的样式。

​		还有一个重要的接口，名称为paintInstance，在多个类中这个接口得到了实现，例如Adder类。这和我们上面介绍过的`paintInstance()`方法实现了闭环，即最终通过实现InstancePainter的接口，调用其实例的方法实现了组件的绘制。

```java
public void paintInstance(InstancePainter painter) {
    Graphics g = painter.getGraphics();
    painter.drawBounds();		// 绘制边框，为一个正方形

    g.setColor(Color.GRAY);		// 设置主题颜色，设置后貌似字体为灰色
    painter.drawPort(IN0);		// IN0~C_OUT实际上为1~5的编号，象征了端口
    painter.drawPort(IN1);
    painter.drawPort(OUT);
    painter.drawPort(C_IN, "c in", Direction.NORTH);
    painter.drawPort(C_OUT, "c out", Direction.SOUTH);

  	// loc为表示模块位置的实例，每个模块都以某个点作为位置
    Location loc = painter.getLocation();	
    int x = loc.getX();
    int y = loc.getY();
    GraphicsUtil.switchToWidth(g, 2);
    g.setColor(Color.BLACK);
    g.drawLine(x - 15, y, x - 5, y);		// 两个drawLine绘制一个加号
    g.drawLine(x - 10, y - 5, x - 10, y + 5);
    GraphicsUtil.switchToWidth(g, 1);
}
```

### 5.总结

​		上面介绍的几个类基本提供了绘制元件所有的接口和方法，通过一系列的调用即可实现绘制。不过这些方法能够实现绘制，都是基于已有的组件之上的。如何在进程创建组件并赋初始值，则是另一个关键的步骤。

## 三、主线程与组件的初始化

### 1.启动画布的StartUp类

​		经过解析StartUp类下的run方法应当是软件的线程对应的run方法。软件可以有两种打开方式：通过指定格式的文件打开，或者是通过应用程序打开。monitor对应的SplashScreen类应当是用于显示启动界面的，而通过检查下一段代码，发现每一个工程都对应了Project类：

```java
    if (filesToOpen.isEmpty()) {
      final var proj = ProjectActions.doNew(monitor);
      proj.setStartupScreen(true);
      if (showSplash) {
        monitor.close();
      }
    }
```

​		经过测试，发现每新建一个Project对象，都可以打开一个画布，我们的后续工作可以从Project对象的初始化入手；而Project类的对象似乎又和LogisimFile类有关。

### 2.生产Project的ProjectAction类

​		ProjectAction类中的两个重载的doNew方法用于生产Project对象，其中参数更多的doNew对象提供了主要的功能：

```java
  public static Project doNew(SplashScreen monitor, boolean isStartupScreen) {
    if (monitor != null) monitor.setProgress(SplashScreen.FILE_CREATE);
    final var loader = new Loader(monitor);
    final var templReader = AppPreferences.getTemplate().createStream();
    LogisimFile file = null;
    try {
      file = loader.openLogisimFile(templReader);		// 与工具有关
    } catch (IOException ex) {
      displayException(monitor, ex);
    } finally {
      try {
        templReader.close();
      } catch (IOException ignored) {
      }
    }
    if (file == null) file = createEmptyFile(loader, null);
    return completeProject(monitor, loader, file, isStartupScreen);
  }
```

​		`try...catch`代码块的内容貌似与加载工具栏的各种工具有关，将其注释后无法再看到工具栏的各种逻辑部件，详见加注释的代码。

​		该方法调用的completeProject方法可以用于构造画布框架：

```java
  private static Project completeProject(
      SplashScreen monitor, Loader loader, LogisimFile file, boolean isStartup) {
    if (monitor != null) monitor.setProgress(SplashScreen.PROJECT_CREATE);
    final var ret = new Project(file);	
    if (monitor != null) monitor.setProgress(SplashScreen.FRAME_CREATE);	// 构造画布框架
    // 执行Runnable对象的run方法
    SwingUtilities.invokeLater(new CreateFrame(loader, ret, isStartup));	
    updatecircs(file, ret);
    return ret;
  }
```

​		通过这个类可以唤醒画布框架。

### 3.Circuit类

​		Circuit类相当于Logisim中的每一个元件，其内部可以包含很多Component，以及Wire。在工程初始化的时候添加Circuit的方法见ZYL的文档。

#### A.mutatorAdd()

​		每添加一个元件，或是一根线，都会调用此方法。



# 对新建电路的跟踪

新发现：

如下注释，运行时只有一个main电路，说明其他组件的加载在openLogisimFile函数中

```java
public static Project doNew(SplashScreen monitor, boolean isStartupScreen) {
    if (monitor != null) monitor.setProgress(SplashScreen.FILE_CREATE);
    final var loader = new Loader(monitor);
    final var templReader = AppPreferences.getTemplate().createStream();
    LogisimFile file = null;
//    try {
//      file = loader.openLogisimFile(templReader);
//    } catch (IOException ex) {
//      displayException(monitor, ex);
//    } finally {
//      try {
//        templReader.close();
//      } catch (IOException ignored) {
//      }
//    }
    if (file == null) file = createEmptyFile(loader, null);
    return completeProject(monitor, loader, file, isStartupScreen);
  }
```

openLogisimFile函数如下，当注释掉 try 的内容，尝试运行logisim发现打不开，说明关键在load函数：

```java
public LogisimFile openLogisimFile(InputStream reader) throws IOException {
    LogisimFile ret = null;
    try {
      ret = LogisimFile.load(reader, this);
    } catch (LoaderException e) {
      return null;
    }
    showMessages(ret);
    return ret;
  }
```

load函数，可以看到catch的内容为返回null，结合上一段分析，关键在于loadSub函数：

```java
public static LogisimFile load(InputStream in, Loader loader) throws IOException {
    try {
      return loadSub(in, loader);
    } catch (SAXException e) {
      e.printStackTrace();
      loader.showError(S.get("xmlFormatError", e.toString()));
      return null;
    }
  }
```

loadSub函数，关键应该在于 xmlReader 类的readLibrary函数：

```java
public static LogisimFile loadSub(InputStream in, Loader loader) throws IOException, SAXException {
    return (loadSub(in, loader, null));
  }

  public static LogisimFile loadSub(InputStream in, Loader loader, File file) throws IOException, SAXException {
    // fetch first line and then reset
    final var inBuffered = new BufferedInputStream(in);
    final var firstLine = getFirstLine(inBuffered);

    //...错误处理

    final var xmlReader = new XmlReader(loader, file);
    /* Can set the project pointer to zero as it is fixed later */
    final var ret = xmlReader.readLibrary(inBuffered, null);
    ret.loader = loader;
    return ret;
  }
```

readLibrary函数，保留了关键的部分：

```java
LogisimFile readLibrary(InputStream is, Project proj) throws IOException, SAXException {
    final var doc = loadXmlFrom(is);
    var elt = doc.getDocumentElement();
    // 检验兼容性（吧？
    elt = ensureLogisimCompatibility(elt);

    considerRepairs(doc, elt);
    final var file = new LogisimFile((Loader) loader);
    final var context = new ReadContext(file);

    context.toLogisimFile(elt, proj);

    if (file.getCircuitCount() == 0) {
        file.addCircuit(new Circuit("main", file, proj));
    }
    //...报错处理
    if (!context.messages.isEmpty()) {
        final var all = new StringBuilder();
        for (final var msg : context.messages) {
          all.append(msg).append("\n");
        }
        loader.showError(all.substring(0, all.length() - 1));
    }
    return file;
  }

```

```java
public void addCircuit(Circuit circuit, int index) {
    circuit.addCircuitListener(this);
    final var tool = new AddTool(circuit.getSubcircuitFactory());
    tools.add(index, tool);
    if (tools.size() == 1) setMainCircuit(circuit);
    fireEvent(LibraryEvent.ADD_TOOL, tool);
  }
```

SubcircuitFactory方法：

```java
public SubcircuitFactory(Circuit source) {
    super("", null, new CircuitHdlGeneratorFactory(source), true);
    this.source = source;
    setFacingAttribute(StdAttr.FACING);
    setDefaultToolTip(new CircuitFeature(null));
    setInstancePoker(SubcircuitPoker.class);
  }
```

通过启动时打断点跟踪，发现执行到SubcircuitFactory方法时main电路参数如下![image-20230302205301499](C:\Users\Yoga\AppData\Roaming\Typora\typora-user-images\image-20230302205301499.png)

位于XmlCircuitReader类的方法似乎调用了mutatorAdd方法

```java
@Override
  protected void run(CircuitMutator mutator) {
    for (final var circuitData : circuitsData) {
      buildCircuit(circuitData, mutator);
    }
    for (final var circuitData : circuitsData) {
      buildDynamicAppearance(circuitData);
    }
  }
```

尝试通过打开一个保存好的circuit文件（已有部分线路）观察内部的部件是如何加载的：

- 执行了MenuFile类的actionPerformed方法（该方法有非常多的重载，目前还没定位到怎么被调用到的），调用了ProjectActions类的doOpen方法

- 执行了ProjectActions类的doOpen方法，调用了Loader类的openLogisimFile方法

- 执行了Loader类的openLogisimFile方法，调用了本类的loadLogisimFile方法

- 执行Loader类的loadLogisimFile方法，调用了Logisim类的load方法

- 执行Logisim类的load方法，调用本类的readSub方法

- 执行Logisim类的loadSub方法，调用XmlReader类的readLibrary方法

- 执行XmlReader类的readLibrary方法，调用了本类的toLogisimFile方法

- 进入XmlReader类的toLogisimFile方法，执行到

  - ```java
    case "circuit" -> {
        name = circElt.getAttribute("name");
        if (name == null || "".equals(name)) {
          addError(S.get("circNameMissingError"), "C??");
        }
        //new了一个新电路，调用了前面提到的SubcircuitFactory方法
        final var circData = new CircuitData(circElt, new Circuit(name, file, proj));
        file.addCircuit(circData.circuit);
        circData.knownComponents = loadKnownComponents(circElt, isHolyCrossFile,
                    isEvolutionFile);
        //这一段推测可能是嵌套，电路套电路
        for (Element appearElt : XmlIterator.forChildElements(circElt, "appear")) {
          loadAppearance(appearElt, circData, name + ".appear");
        }
        for (final var boardMap : XmlIterator.forChildElements(circElt, "boardmap")) {
          final var boardName = boardMap.getAttribute("boardname");
          if (StringUtil.isNullOrEmpty(boardName))
            continue;
          loadMap(boardMap, boardName, circData.circuit);
        }
        circuitsData.add(circData);
      }
    ```

- 运行到XmlCircuitReader类的run方法，执行了XmlCircuitReader类的buildCircuit方法

- buildCircuit方法执行了mutator.add(dest, comp);语句

- 运行到CircuitMutatorImpl类，调用了add方法，其中调用了mutatorAdd方法

- 回到XmlCircuitReader类的run方法，继续执行buildDynamicAppearance方法（好像不是用来画具体的组件

- 回到前面的toLogisimFile方法

- ……后续没有断点跟踪



截至目前，执行到ProjectActions类的doOpen方法中的：

```java
try {
      final var lib = loader.openLogisimFile(f);
    // .......
}
```

还没有进行绘画过程，推测上述过程应该是将电路的基本信息加载好，logisimFile实例“lib”中，后续可能对这个file文件进行绘图
