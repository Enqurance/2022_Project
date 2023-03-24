import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HierarchyAnalyzer {
    private final HashSet<String> moduleClasses = new HashSet<>();
    private final ArrayList<String> verilogFiles = new ArrayList<>();

    public HashMap<String, VerilogModule> buildUsageMap() {
        HashMap<String, VerilogModule> modules = new HashMap<>();
        String projectPath = "VerilogProject";
        verilogFiles.addAll(getFileNames(projectPath));
        addModuleClasses();
        printModuleClass();
        return modules;
    }

    private ArrayList<String> getFileNames(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        ArrayList<String> fileNames = new ArrayList<>();
        return getFileNames(file, fileNames);
    }

    private ArrayList<String> getFileNames(File file, ArrayList<String> fileNames) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory()) {
                    if (isVerilogFile(f.getName())) {
                        fileNames.add(f.getName());
                    }
                }
            }
        }
        return fileNames;
    }

    private void addModuleClasses() {
        for (String file : verilogFiles) {
            String regex = ".v";
            String[] arr = file.split(regex);
            moduleClasses.add(arr[0]);
        }
    }

    private void connectModule(VerilogModule m1, VerilogModule m2) {
        m1.addSubVerilogModule(m2);
    }

    private boolean isVerilogFile(String filename) {
        String verilogFile = ".+(\\.v)";
        Pattern p = Pattern.compile(verilogFile);
        Matcher m = p.matcher(filename);
        return m.find();
    }

    public ArrayList<String> getVerilogFiles() {
        return verilogFiles;
    }

    public void printModuleClass() {
        for (String s : moduleClasses) {
            System.out.println(s);
        }
    }

    public void createModuleMap() {
        for (String filename : verilogFiles) {
            analyzeSingleFile(filename);
        }
    }

    public void analyzeSingleFile(String filename) {

    }
}

