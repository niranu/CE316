package com.example.ce316;

import java.io.*;
import java.util.Locale;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Compiler {
    private static final String PROJECT_PATH = "src/main/resources/Projects/";

    /*public static void compileAndRun(String project, String studentDirectory) {
        String sourceCodePath = PROJECT_PATH + project + "/StudentProjects/" + studentDirectory;
        //src/main/resources/Projects/Library/StudentProjects                     /123

        try {
            JSONObject config = getConfigurationByProject(project);
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
                System.out.println("******");
            }

            // Runs the compiled code
            //src/main/resources/Projects/Library/StudentProjects/123/Main
            String runCommand = config.getString("run_command").replace("{classpath}", sourceCodePath+"/");
            System.out.println("Running code from: " + runCommand);
            Process runProcess = Runtime.getRuntime().exec(runCommand);
            printProcessOutput(runProcess);

            // Delete the .class file after execution
            deleteClassFiles(sourceCodePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    public static void compileAndRun(String project/*String fileName*/) {
        String sourceCodePath = PROJECT_PATH + project+"/StudentProjects";
        //src/main/resources/Projects/Library/StudentProjects/456/Main
        try {
            JSONObject config = getConfigurationByProject(project);
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
                System.out.println("******");
            }

            // Runs the compiled code
            //src/main/resources/Projects/Library/StudentProjects/Main
            String runCommand = config.getString("run_command").replace("{classpath}",sourceCodePath+"/");
            System.out.println("Running code from: " + runCommand);
            Process runProcess = Runtime.getRuntime().exec(runCommand);
            printProcessOutput(runProcess);

            // Delete the .class file after execution
            deleteClassFiles(sourceCodePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        private static JSONObject getConfigurationByProject (String project){
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

        private static void printProcessOutput (Process process) throws IOException {
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
        // This method will be used when it is necessary to delete .class files
        private static void deleteClassFiles (String sourceCodePath){
            // Construct the class file path using File.separator
            String classFilePath = sourceCodePath.replace(".java", ".class");
            File classFile = new File(classFilePath);

            // Check if the class file exists and delete it
            if (classFile.exists()) {
                if (classFile.delete()) {
                    System.out.println("Deleted class file: " + classFilePath);
                } else {
                    System.out.println("Failed to delete class file: " + classFilePath);
                }
            } else {
                System.out.println("Class file does not exist: " + classFilePath);
            }
        }
        private static String Comparator () {


            return null;
        }

        //For running all the code in the Students project File which are divided by the students ID
        // and all are separated in different directories
        private static void RunAll (String project){
            String studentProjectPath = PROJECT_PATH + "/" + project + "/StudentProjects";
            File studentProjectsDirectory = new File(studentProjectPath);

            // Check if the student projects directory exists
            if (!studentProjectsDirectory.exists() || !studentProjectsDirectory.isDirectory()) {
                System.out.println("Student projects directory not found.");
                return;
            }

            // Get list of student directories
            File[] studentDirectories = studentProjectsDirectory.listFiles(File::isDirectory);

            // Iterate over each student directory
            assert studentDirectories != null;
            for (File studentDirectory : studentDirectories) {
                System.out.println("Running code for student: " + studentDirectory.getName());
                //compileAndRun(project, studentDirectory.getName()); // Pass the project name and student directory name
                System.out.println("Finished running code for student: " + studentDirectory.getName());
            }

        }

        public static void main (String[] args){
            compileAndRun("Library");
            //RunAll("Library");
        }


}