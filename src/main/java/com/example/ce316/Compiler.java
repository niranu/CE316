package com.example.ce316;

import java.io.*;
import java.util.Locale;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Compiler {
    private static final String PROJECT_PATH = "src/main/resources/Projects/";

    public static void compileAndRun(String project, String studentDirectory) {
        String sourceCodePath = PROJECT_PATH + project + "/StudentProjects/" + studentDirectory;
        String mainSourceFile = sourceCodePath + "/Main";
        String outputFilePath = sourceCodePath + "/output.txt"; // Output file path

        try (PrintWriter outputWriter = new PrintWriter(new FileWriter(outputFilePath))) {
            JSONObject config = getConfigurationByProject(project);
            if (config == null) {
                outputWriter.println("No configuration found for the specified project.");
                return;
            }

            // Compile the source code if needed
            boolean compilationSuccess = true;
            if (config.getBoolean("needs_compilation")) {
                String compileCommand = config.getString("command")
                        .replace("{source}", mainSourceFile);
                Process compileProcess = Runtime.getRuntime().exec(compileCommand);
                compileProcess.waitFor();
                compilationSuccess = printCompileOutput(compileProcess, outputWriter);
            }

            // Run the compiled code if compilation was successful
            if (compilationSuccess) {
                String runCommand = config.getString("run_command")
                        .replace("{classpath}", sourceCodePath)
                        .replace("{main}", "Main");
                Process runProcess = Runtime.getRuntime().exec(runCommand);
                printRunOutput(runProcess, outputWriter);
            }

            // Delete the compiled files after execution
            deleteClassFiles(sourceCodePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject getConfigurationByProject(String project) {
        File configFile = new File(PROJECT_PATH + project + "/" + project.toLowerCase(Locale.ROOT) + ".json");
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

    private static boolean printCompileOutput(Process process, PrintWriter outputWriter) throws IOException {
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String errorLine;
        boolean hasErrors = false;
        StringBuilder errors = new StringBuilder();
        while ((errorLine = stdError.readLine()) != null) {
            errors.append(errorLine).append("\n");
            hasErrors = true;
        }

        if (hasErrors) {
            outputWriter.println(errors.toString().trim());
        }

        return !hasErrors;
    }

    private static void printRunOutput(Process process, PrintWriter outputWriter) throws IOException {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder errorBuilder = new StringBuilder();
        boolean hasErrors = false;

        // Capture standard error output
        while ((line = stdError.readLine()) != null) {
            errorBuilder.append(line).append("\n");
            hasErrors = true;
        }

        if (hasErrors) {
            outputWriter.println(errorBuilder.toString().trim());
        } else {
            // Capture standard output
            while ((line = stdInput.readLine()) != null) {
                outputWriter.println(line);
            }
        }
    }

    // Method to delete compiled .class files after execution
    private static void deleteClassFiles(String sourceCodePath) {
        File directory = new File(sourceCodePath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".class"));

        if (files != null) {
            for (File file : files) {
                if (file.delete()) {
                    System.out.println("Deleted compiled file: " + file.getPath());
                } else {
                    System.out.println("Failed to delete compiled file: " + file.getPath());
                }
            }
        }
    }

    private static void runAll(String project) {
        String studentProjectPath = PROJECT_PATH + project + "/StudentProjects";
        File studentProjectsDirectory = new File(studentProjectPath);

        if (!studentProjectsDirectory.exists() || !studentProjectsDirectory.isDirectory()) {
            System.out.println("Student projects directory not found.");
            return;
        }

        File[] studentDirectories = studentProjectsDirectory.listFiles(File::isDirectory);

        assert studentDirectories != null;
        for (File studentDirectory : studentDirectories) {
            System.out.println("Running code for student: " + studentDirectory.getName());
            compileAndRun(project, studentDirectory.getName());
            System.out.println("Finished running code for student: " + studentDirectory.getName());
        }
    }

    public static void main(String[] args) {
        compileAndRun("Library", "456");
        runAll("Library");
    }
}
