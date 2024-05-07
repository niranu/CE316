package com.example.ce316;

import java.io.*;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Compiler {
    private static final String COMPILER_PATH = "src/main/resources/CompilerPath/";

    public static void compileAndRun(String language, String sourceCodePath) {
        try {
            JSONObject config = getConfigurationByLanguage(language);
            if (config == null) {
                System.out.println("No configuration found for the specified language.");
                return;
            }

            // Compiles the source code
            if (config.getBoolean("needs_compilation")) {
                String compileCommand = config.getString("command").replace("{source}", sourceCodePath);
                System.out.println("Compiling with command: " + compileCommand);
                Process compileProcess = Runtime.getRuntime().exec(compileCommand);
                compileProcess.waitFor();
                printProcessOutput(compileProcess);
            }

            // Runs the compiled code
            String runCommand = config.getString("run_command").replace("{classpath}", "src/main/resources/Projects");
            System.out.println("Running code from: " + runCommand);
            Process runProcess = Runtime.getRuntime().exec(runCommand);
            printProcessOutput(runProcess);

            // Delete the .class file after execution
            deleteClassFiles(sourceCodePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject getConfigurationByLanguage(String language) {
        File configFile = new File(COMPILER_PATH + language + ".json");
        if (configFile.exists()) {
            try (InputStream is = new FileInputStream(configFile)) {
                JSONTokener tokener = new JSONTokener(is);
                return new JSONObject(tokener);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void printProcessOutput(Process process) throws IOException {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        System.out.println("Standard output:");
        while ((line = stdInput.readLine()) != null) {
            System.out.println(line);
        }

        System.out.println("Standard error:");
        while ((line = stdError.readLine()) != null) {
            System.out.println(line);
        }
    }

    // Method to delete .class files
    private static void deleteClassFiles(String sourceCodePath) {
        String classFilePath = sourceCodePath.replace(".java", ".class");
        File classFile = new File(classFilePath);
        if (classFile.delete()) {
            System.out.println("Deleted class file: " + classFilePath);
        } else {
            System.out.println("Failed to delete class file: " + classFilePath);
        }
    }

    public static void main(String[] args) {
        compileAndRun("java", "src/main/resources/Projects/HelloWorld.java");
    }
}