package com.example.ce316;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CreateNewProject {

    public void createProjectFile(String projectName, String description, String expectedOutput) {
        String projectDirectoryPath = "src/main/resources/Projects/" + projectName;
        File projectDirectory = new File(projectDirectoryPath);

        if (!projectDirectory.exists()) {
            if (projectDirectory.mkdirs()) {
                System.out.println("Project directory created successfully.");

                // Create Student_Files directory inside the project directory
                File studentFilesDirectory = new File(projectDirectoryPath + "/Student_Files");
                if (studentFilesDirectory.mkdirs()) {
                    System.out.println("Student_Files directory created successfully.");
                } else {
                    System.out.println("Failed to create Student_Files directory.");
                    return;
                }
            } else {
                System.out.println("Failed to create project directory.");
                return;
            }
        }

        try {
            FileWriter descriptionWriter = new FileWriter(projectDirectoryPath + "/" + projectName + "_description.txt");
            descriptionWriter.write(description);
            descriptionWriter.close();
            System.out.println("Description file created successfully.");

            FileWriter outputWriter = new FileWriter(projectDirectoryPath + "/" + projectName + "_output.txt");
            outputWriter.write(expectedOutput);
            outputWriter.close();
            System.out.println("Expected output file created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating project files: " + e.getMessage());
        }
    }





}
