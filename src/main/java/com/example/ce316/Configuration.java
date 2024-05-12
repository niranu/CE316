package com.example.ce316;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration {
    private String language;
    private boolean needsCompilation;
    private String compileCommand;
    private String runCommand;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    // Constructor
    public Configuration(String language, boolean needsCompilation, String compileCommand, String runCommand) {
        this.language = language;
        this.needsCompilation = needsCompilation;
        this.compileCommand = compileCommand;
        this.runCommand = runCommand;
    }

    // Method to serialize Configuration object to JSON file
    public void saveToJsonFile(String fileName) throws IOException {
        File file = new File("src/main/resources/Jsons/"+ fileName + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("{\n");
            writer.write("  \"language\": \"" + language + "\",\n");
            writer.write("  \"needs_compilation\": " + needsCompilation + ",\n");
            writer.write("  \"command\": \"" + compileCommand + "\"");

            if (needsCompilation) {
                writer.write(",\n");
                writer.write("  \"run_command\": \"" + runCommand + "\"");
            }
            writer.write("\n}\n");
        }
    }


}

