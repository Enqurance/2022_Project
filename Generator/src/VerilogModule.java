import java.util.HashMap;

public class VerilogModule {
    private final HashMap<String, VerilogModule> subModules = new HashMap<>();
    private final HashMap<String, VerilogModule> connectedModules = new HashMap<>();
    private final String moduleClass;
    private final String moduleName;

    public VerilogModule(String moduleClass, String moduleName) {
        this.moduleClass = moduleClass;
        this.moduleName = moduleName;
    }

    public HashMap<String, VerilogModule> getSubModules() {
        return subModules;
    }

    public HashMap<String, VerilogModule> getConnectedModules() {
        return connectedModules;
    }

    public void addSubVerilogModule(VerilogModule m) {
        subModules.put(m.getModuleName(), m);
    }

    public void addConnectedVerilogModule(VerilogModule m) {
        connectedModules.put(m.getModuleName(), m);
    }

    public String getModuleClass() {
        return moduleClass;
    }

    public String getModuleName() {
        return moduleName;
    }
}
