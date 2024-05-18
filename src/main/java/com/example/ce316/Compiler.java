package com.example.ce316;

import java.io.*;
import java.util.Locale;

import javafx.scene.control.Alert;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Compiler {
    public Compiler() {}

    private static final String PROJECT_PATH = "src/main/resources/Projects/";


    public static void compileAndRun(String project ,String fileName) {
        String sourceCodePath = PROJECT_PATH + project+"/StudentProjects/"+fileName+"/";
        //src/main/resources/Projects/Library/StudentProjects/456/Main
        try {
            JSONObject config = getConfigurationByProject(project);
            if (config == null) {
                System.out.println("No configuration found for the specified language.");
                return;
            }

            if (config.getBoolean("needs_compilation")) {


                String language = config.getString("language");

                // Runs the compiled code
                //src/main/resources/Projects/Library/StudentProjects/Main

                    String compileCommand = config.getString("command").replace("{source}", sourceCodePath);
                    System.out.println("Compiling with command: " + compileCommand);
                    Process compileProcess = Runtime.getRuntime().exec(compileCommand);
                    compileProcess.waitFor();
                    String runCommand = config.getString("run_command").replace("{source}", sourceCodePath);
                    System.out.println("Running code from: " + runCommand);
                    Process runProcess = Runtime.getRuntime().exec(runCommand);
                    saveProcessOutput(runProcess, sourceCodePath);
                    boolean match = Comparator(project, fileName);
                    writeCSV(project, fileName, match);


                    /*String compileCommand = config.getString("command").replace("{source}", sourceCodePath);
                    System.out.println("Compiling with command: " + compileCommand);
                    Process compileProcess = Runtime.getRuntime().exec(compileCommand);
                    compileProcess.waitFor();
                    String runCommand = config.getString("run_command").replace("{source}", sourceCodePath);
                    System.out.println("Running C code with command: " + runCommand);
                    Process runProcess = Runtime.getRuntime().exec(runCommand);
                    saveProcessOutput(runProcess, sourceCodePath);
                    boolean match = Comparator(project, fileName);
                    writeCSV(project, fileName, match);*/


            }else{
                // Execute Python script

                String command = config.getString("command").replace("{source}", sourceCodePath);
                System.out.println("Running Python script with command: " + command);
                Process process = Runtime.getRuntime().exec(command);
                saveProcessOutput(process, sourceCodePath);
                boolean match = Comparator(project, fileName);
                writeCSV(project, fileName, match);

            }


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

    private static void saveProcessOutput(Process process, String studentProjectPath) throws IOException {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();

        String line;

        while ((line = stdInput.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }


        while ((line = stdError.readLine()) != null) {
            errors.append(line).append(System.lineSeparator());
        }

        // Write the output to the output.txt file
        File outputFile = new File(studentProjectPath, "output.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(output.toString());
            writer.write(errors.toString());
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
    // compares the expected output to student output
    private static boolean Comparator (String project,String studentFile) throws IOException {
        String sourceCodePath = PROJECT_PATH + project+"/"+project+"_output.txt";
        String studentOutputPath = PROJECT_PATH + project+"/StudentProjects/"+studentFile+"/output.txt";

        BufferedReader sourceReader = new BufferedReader(new FileReader(sourceCodePath));
        BufferedReader outputReader = new BufferedReader(new FileReader(studentOutputPath));
        String sourceLine;
        String outputLine;


        // Compare each line from both files
        while ((sourceLine = sourceReader.readLine()) != null && (outputLine = outputReader.readLine()) != null) {

            if (!sourceLine.equals(outputLine)) {
                return false;
            }

        }
        return true;
    }
    public static void writeCSV(String project, String studentID, boolean match) {

        String CSVPath = PROJECT_PATH + project;
        String csvFile = CSVPath +"/comparison_report.csv";

        try (FileWriter writer = new FileWriter(csvFile, true)) {
            // Append project, student ID, and match status to CSV file
            writer.append(project)
                    .append(",")
                    .append(studentID)
                    .append(",")
                    .append(match ? "Match with output" : "Does not match with output")
                    .append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //For running all the code in the Students project File which are divided by the students ID
    // and all are separated in different directories
    public void RunAll (String project){
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
            compileAndRun(project, studentDirectory.getName()); // Pass the project name and student directory name
            System.out.println("Finished running code for student: " + studentDirectory.getName());


        }/*
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Running successful! Check report screen from assignment report parts!");
        alert.showAndWait();*/

    }

    public static void main (String[] args) throws IOException {
        Compiler cm = new Compiler();
        //compileAndRun("python_deneme","23");
        //Configuration config = new Configuration("rUBY" , false, "asd","dsa");
        //config.saveToJsonFile(config.getLanguage());
        cm.RunAll("HelloWorld");
        cm.RunAll("deneme_c++");
        cm.RunAll("Library");
        cm.RunAll("python_deneme");

        //cm.RunAll("HelloWorld");
        //System.out.println(Comparator("Library","456"));
        //writeCSV("Library","456",true);
    }


}