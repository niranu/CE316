package com.example.ce316;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CreateNewProject {

    public void createProjectFile(String projectName, String description, String expectedOutput) {
        // Sanitize the project name by replacing spaces with underscores
        String sanitizedProjectName = projectName.replace(" ", "_");

        String projectDirectoryPath = "src/main/resources/Projects/" + sanitizedProjectName;
        File projectDirectory = new File(projectDirectoryPath);


        if (!projectDirectory.exists()) {
            if (projectDirectory.mkdirs()) {
                System.out.println("Project directory created successfully.");

                // Create StudentProjects directory inside the project directory

            } else {
                System.out.println("Failed to create project directory.");
                return;
            }
        }

        try {
            File studentProjectsDirectory = new File(projectDirectoryPath + "/StudentProjects");
            if (studentProjectsDirectory.mkdirs()) {
                System.out.println("StudentProjects directory created successfully.");
            } else {
                System.out.println("Failed to create StudentProjects directory.");
                return;
            }

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
