package com.example.ce316;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipUnpacker {

    private static final String STUDENT_PROJECTS_FOLDER = "StudentProjects";

    public static void main(String[] args) {
        String zipFilesDirectory = "path/to/student/zip/files";
        unpackStudentProjects(zipFilesDirectory);
    }

    /**
     * Ensure the StudentProjects directory exists.
     */
    private static void ensureStudentProjectsFolderExists() {
        File studentProjectsDir = new File(STUDENT_PROJECTS_FOLDER);
        if (!studentProjectsDir.exists()) {
            studentProjectsDir.mkdirs();
        }
    }

    /**
     * Unpack all student ZIP files into their respective folders under the StudentProjects folder.
     * @param zipFilesDirectory Path to the directory containing student ZIP files.
     */
    public static void unpackStudentProjects(String zipFilesDirectory) {
        ensureStudentProjectsFolderExists();

        File dir = new File(zipFilesDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Invalid ZIP files directory: " + zipFilesDirectory);
            return;
        }

        File[] zipFiles = dir.listFiles((d, name) -> name.endsWith(".zip"));
        if (zipFiles == null || zipFiles.length == 0) {
            System.out.println("No ZIP files found in the directory.");
            return;
        }

        for (File zipFile : zipFiles) {
            try {
                String studentID = getStudentIDFromZipFile(zipFile.getName());
                if (studentID == null) {
                    System.out.println("Skipping invalid ZIP file: " + zipFile.getName());
                    continue;
                }

                File studentFolder = new File(STUDENT_PROJECTS_FOLDER, studentID);
                if (!studentFolder.exists()) {
                    studentFolder.mkdir();
                }

                unpackZipFile(zipFile, studentFolder);
                System.out.println("Successfully unpacked: " + zipFile.getName());

            } catch (IOException e) {
                System.err.println("Error unpacking ZIP file: " + zipFile.getName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Extract the student ID from the ZIP file name.
     * Assumes the ZIP file name is in the format 'studentID.zip'.
     * @param zipFileName ZIP file name.
     * @return Student ID or null if invalid format.
     */
    private static String getStudentIDFromZipFile(String zipFileName) {
        int dotIndex = zipFileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return null;
        }
        return zipFileName.substring(0, dotIndex);
    }

    /**
     * Unpack a ZIP file into the specified output folder.
     * @param zipFile ZIP file to unpack.
     * @param outputFolder Output folder.
     * @throws IOException If an I/O error occurs.
     */
    private static void unpackZipFile(File zipFile, File outputFolder) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = newFile(outputFolder, entry);
                if (entry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // Create parent directories if necessary
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // Write file
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    /**
     * Resolve the path of a ZIP entry to prevent directory traversal attacks.
     * @param destinationDir Destination directory.
     * @param zipEntry ZIP entry.
     * @return Safe resolved file path.
     * @throws IOException If the resolved file is outside the destination directory.
     */
    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target directory: " + zipEntry.getName());
        }

        return destFile;
    }
}

