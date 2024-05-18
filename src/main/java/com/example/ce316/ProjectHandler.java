package com.example.ce316;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectHandler {

    private static final String BASE_PROJECTS_DIR = "src/main/resources/Projects";

    public static void handleStudentProjects(String zipFilesDirectory, String projectName) throws IOException {
        File dir = new File(zipFilesDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("The specified directory does not exist or is not a directory.");
        }

        File[] zipFiles = dir.listFiles((d, name) -> name.endsWith(".zip"));
        if (zipFiles == null || zipFiles.length == 0) {
            throw new IOException("No ZIP files found in the specified directory.");
        }

        // Use the existing Projects directory
        Path basePath = Paths.get(BASE_PROJECTS_DIR);
        File baseDir = basePath.toFile();
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            throw new IOException("The base Projects directory does not exist or is not a directory.");
        }

        // Create a new project directory within the existing Projects directory
        Path projectPath = basePath.resolve(projectName);
        File projectDir = projectPath.toFile();
        if (!projectDir.exists()) {
            projectDir.mkdirs();
        }

        // Create StudentProjects directory within the new project directory
        Path studentProjectsPath = projectPath.resolve("StudentProjects");
        File studentProjectsDir = studentProjectsPath.toFile();
        if (!studentProjectsDir.exists()) {
            studentProjectsDir.mkdirs();
        }

        Unpacker unpacker = new Unpacker();
        for (File zipFile : zipFiles) {
            System.out.println("Processing ZIP File: " + zipFile.getName());
            String studentId = extractStudentId(zipFile.getName());
            Path studentProjectPath = studentProjectsPath.resolve(studentId);
            if (!studentProjectPath.toFile().exists()) {
                studentProjectPath.toFile().mkdirs();
            }
            unpacker.unpack(zipFile.getAbsolutePath(), studentProjectPath.toString());
        }
    }

    private static String extractStudentId(String fileName) {
        // Extract the student ID from the file name (e.g., 100.zip -> 100)
        return fileName.replace(".zip", "");
    }
}
