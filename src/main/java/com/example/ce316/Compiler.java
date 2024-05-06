package com.example.ce316;


//COMPILE RUN JAVA İÇİN ÇALIŞIYOR GİBİ AMA OUTPUT ALAMIYORUZ BELKİ DE ÇALIŞMIYORDUR
import java.io.*;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Compiler {
    private static final String COMPILER_PATH = ".//src//main//resources//CompilerPath";

    // Compiles and runs the source code based on the provided configuration
    public static void compileAndRun(String language, String sourceCodePath) {
        try {
            JSONObject config = getConfigurationByLanguage(language);
            if (config == null) {
                System.out.println("No configuration found for the specified language.");
                return;
            }

            String compilerCommand = config.getString("command");

            // Compiling the source code (if necessary)
            if (config.getBoolean("needs_compilation")) {
                String compileCommand = compilerCommand.replace("{source}", sourceCodePath);
                System.out.println("Compiling with command: " + compileCommand);
                Process compileProcess = Runtime.getRuntime().exec(compileCommand);
                compileProcess.waitFor();
            }

            // Running the compiled code or script
            String runCommand = config.has("run_command") ? config.getString("run_command") : compilerCommand;
            runCommand = runCommand.replace("{classpath}", ".//src//main//resources//Projects")
                    .replace("{main}", "Main"); // Changed from "main" to "Main"
            System.out.println("Running code from: " + runCommand);
            Process runProcess = Runtime.getRuntime().exec(runCommand);
            captureOutput(runProcess);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Retrieves the configuration JSON object based on language
    private static JSONObject getConfigurationByLanguage(String language) {
        File configFile = new File(COMPILER_PATH, language + ".json");
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

    // Captures and prints the output of the process (ÇALIŞMIYOR DÜZELTİLECEK)
    private static void captureOutput(Process process) throws IOException {
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println("Standard error:");
        while ((line = stdError.readLine()) != null) {
            System.out.println(line);
        }
    }

    public static void main(String[] args) {
        // Example usage: run a Python script
        compileAndRun("java", "src/main/resources/Projects/test.java");

    }
}