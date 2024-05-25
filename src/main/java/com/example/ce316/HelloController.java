package com.example.ce316;


import com.opencsv.exceptions.CsvException;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import com.opencsv.CSVReader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import javax.json.JsonObject;
import javax.json.stream.JsonParser;



public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private ResourceBundle resources;
    @FXML
    private Button createNewProjectButton;
    @FXML
    private Button openExistingProjectButton;
    @FXML
    private Button assignmentReportsButton;
    @FXML
    private Button BackButton;
    @FXML
    private Button RunButton;

    @FXML
    private TextArea createNewProject_ProjectName;
    @FXML
    private TextArea createNewProject_ProjectDescription;
    @FXML
    private TextArea createNewProject_ExpectedOutput;
    @FXML
    private Button createNewProject_CreateProject;
    @FXML
    private Button refreshButton;
    @FXML
    private ComboBox<String> createNewProject_ConfigurationComboBox= new ComboBox<>();
    @FXML
    private ComboBox<String> project_comboBox = new ComboBox<>();
    @FXML
    private ComboBox<String> config_comboBox = new ComboBox<>();

    CreateNewProject projectCreator;

    @FXML
    private TableView<String[]> reportTable;

    @FXML
    private URL location;
    @FXML
    private StackPane mainStackPane;
    @FXML
    private TableColumn<String[], String> projectColumn;

    @FXML
    private TableColumn<String[], String> studentIdColumn;

    @FXML
    private TableColumn<String[], String> resultColumn;

    @FXML
    private TextArea mainClassNameTextArea;


    @FXML
    private TextArea languageTextArea;
    @FXML
    private ComboBox<String> needsCompilationComboBox;
    @FXML
    private TextArea compileCommandTextArea;
    @FXML
    private TextArea runCommandTextArea;
    @FXML
    private Button saveConfigurationButton;

    private Stage stage;
    private Scene scene;
    @FXML
    private Parent root;

    private String temporaryFilePath;

    //edit
    @FXML
    private ComboBox<String> editProject_ProjectNameComboBox = new ComboBox<>();
    @FXML
    private ChoiceBox<String> editProject_ChoiceBox = new ChoiceBox<>();
    @FXML
    private TextArea editProject_AssignmentDescription;
    @FXML
    private TextArea editProject_Output;
    @FXML
    private TextArea editProject_MainClass;
    @FXML
    private Button editPtoject_DeleteCode;
    @FXML
    private Button editProject_UploadCode;




    Compiler compiler = new Compiler();


    @FXML
    private void createProject(ActionEvent event) {
        System.out.println("Project creation initiated.");
        projectCreator = new CreateNewProject();
        String projectName = createNewProject_ProjectName.getText();
        String description = createNewProject_ProjectDescription.getText();
        String expectedOutput = createNewProject_ExpectedOutput.getText();
        String mainClass = mainClassNameTextArea.getText();

        projectName = projectName.replace(" ", "");

        if (projectName == null || projectName.trim().isEmpty()) {
            // Display error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Project name cannot be empty!");
            alert.showAndWait();
        } else {
            if (mainClass == null || mainClass.trim().isEmpty()) {
                // Display error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Main class name cannot be empty!");
                alert.showAndWait();
            } else {
                String selectedLanguage = createNewProject_ConfigurationComboBox.getSelectionModel().getSelectedItem();
                if (selectedLanguage != null){
                    System.out.println("Selected item: " + selectedLanguage);

                    // Convert the selected item to lowercase for case-insensitive comparison
                    String selectedLanguageLowerCase = selectedLanguage.toLowerCase();

                    // Get the JSON file path based on the selected item
                    String jsonFilePath = "src/main/resources/Jsons/" + selectedLanguageLowerCase + ".json";
                    File jsonFile = new File(jsonFilePath);

                    // Check if the JSON file exists
                    if (jsonFile.exists()) {
                        System.out.println("JSON file found for selected language.");


                        try {
                            // Read the JSON file content
                            String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()));

                            // Check if compilation is needed based on the JSON content
                            // Customize JSON content for languages that require compilation

                            if (jsonContent.contains("{main}")) {
                                jsonContent = jsonContent.replace("{main}", mainClass);
                            }

                            // Save the edited JSON content to the project directory
                            String projectDirectoryPath = "src/main/resources/Projects/" + projectName;
                            File projectDirectory = new File(projectDirectoryPath);

                            if (!projectDirectory.exists()) {
                                if (projectDirectory.mkdirs()) {
                                    System.out.println("Project directory created successfully.");
                                } else {
                                    System.out.println("Failed to create project directory.");
                                    return;
                                }
                            }


                            // Copy the edited JSON file to the project directory with the project name as the filename
                            String newJsonFileName = projectName + ".json";
                            Path targetPath = Paths.get(projectDirectoryPath + "/" + newJsonFileName);
                            Files.write(targetPath, jsonContent.getBytes());
                            System.out.println("Edited JSON file saved to project directory successfully.");


                        } catch (IOException e) {
                            System.out.println("An error occurred while editing or saving JSON file: " + e.getMessage());
                        }
                    }
                    projectCreator.createProjectFile(projectName, description, expectedOutput);


                    //to relocate the code file to the project directory from Temporary directory.
                    String projectDirectory = "src/main/resources/Projects/" + projectName;
                    if (selectedLanguage.toLowerCase().equals("c++")) {
                        String temporaryFilePath = "src/main/resources/Temporary/" + mainClass + ".cpp";

                    } else {
                        String temporaryFilePath = "src/main/resources/Temporary/" + mainClass + "." + selectedLanguage.toLowerCase();
                    }
                    System.out.println("Temporary file path is:" + temporaryFilePath);

                    if (temporaryFilePath != null) {
                        File temporaryFile = new File(temporaryFilePath);
                        File destinationFile = new File(projectDirectory, temporaryFile.getName());
                        try {
                            Files.move(temporaryFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Code file relocated to Project directory: " + destinationFile.getAbsolutePath());
                        } catch (IOException e) {
                            System.out.println("An error occurred while relocating code file to Project directory: " + e.getMessage());
                        }
                    } else {
                        System.out.println("No code file uploaded to relocate.");
                    }

                    File projectDir = new File(projectDirectory);
                    File[] mainFiles = projectDir.listFiles((dir, name) -> name.matches(mainClass + "\\..+"));

                    if (mainFiles != null && mainFiles.length > 0) {
                        Compiler.UserCodeRunner(projectName);
                    } else {
                        System.out.println("Main file not found, skipping UserCodeRunner.");
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Project created succesfully");
                    alert.showAndWait();

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Choose configuration");
                    alert.showAndWait();
                }
            }
        }
    }



    @FXML
    private void cancel(ActionEvent event) {
        // Code to handle cancellation
        // Clear text in TextAreas
        createNewProject_ProjectName.clear();
        createNewProject_ProjectDescription.clear();
        createNewProject_ExpectedOutput.clear();
        mainClassNameTextArea.clear();

        // Reset selection in ComboBox
        createNewProject_ConfigurationComboBox.getSelectionModel().clearSelection();
        System.out.println("Operation cancelled.");
    }

    @FXML
    ObservableList <String>list2 = FXCollections.observableArrayList("Compiler", "Interpreter");
    @FXML
    void initialize() {
        project_comboBox.setOnShowing(event -> projectComboBox());
        createNewProject_ConfigurationComboBox.setOnShowing(event -> ConfigComboBox());
        editProject_ProjectNameComboBox.setOnShowing(event -> editProjectComboBox());

        config_comboBox.setItems(list2);
        editProject_ChoiceBox.setOnShowing(event -> ConfigComboBox());

    }

    //Run button action for Uploaded Student Files

    @FXML
    private void RunAllStudentFiles(ActionEvent e) throws IOException {
        String project = project_comboBox.getSelectionModel().getSelectedItem();
        compiler.RunAll(project);
    }

    @FXML
    private void projectComboBox() {
        String projectsPath = "src/main/resources/Projects";
        File ProjectsDirectory = new File(projectsPath);
        File[] projectDirectories = ProjectsDirectory.listFiles(File::isDirectory);
        ObservableList<String> list = FXCollections.observableArrayList();
        assert projectDirectories != null;
        for (File projectDirectory : projectDirectories) {
            list.add(projectDirectory.getName());
        }
        project_comboBox.setItems(list);
    }

    @FXML
    private void editProjectComboBox(){
        String projectsPath = "src/main/resources/Projects";
        File ProjectsDirectory = new File(projectsPath);
        File[] projectDirectories = ProjectsDirectory.listFiles(File::isDirectory);
        ObservableList<String> list = FXCollections.observableArrayList();
        assert projectDirectories != null;
        for (File projectDirectory : projectDirectories) {
            list.add(projectDirectory.getName());
        }
        editProject_ProjectNameComboBox.setItems(list);

    }

    @FXML
    public void editSet(ActionEvent event) throws IOException {
        String projectName = editProject_ProjectNameComboBox.getSelectionModel().getSelectedItem();
        if (projectName == null) {
            System.out.println("No project selected.");
            return;
        }

        String projectPath = "src/main/resources/Projects/" + projectName + "/" + projectName;
        BufferedReader description = new BufferedReader(new FileReader(projectPath + "_description.txt"));
        BufferedReader output = new BufferedReader(new FileReader(projectPath + "_output.txt"));
        BufferedReader jsonReader = new BufferedReader(new FileReader(projectPath + ".json"));

        editProject_AssignmentDescription.clear();
        editProject_Output.clear();

        String des;
        String out;
        while ((des = description.readLine()) != null) {
            editProject_AssignmentDescription.appendText(des + "\n");
        }
        while ((out = output.readLine()) != null) {
            editProject_Output.appendText(out + "\n");
        }
        description.close();
        output.close();

        // Parse the language name from the JSON file
        String language = null;
        JSONObject jsonObject = new JSONObject(new JSONTokener(jsonReader));
        if (jsonObject.has("language")) {
            language = jsonObject.getString("language");
        }
        jsonReader.close();

        // Check if the language is available in the JSON files
        boolean languageAvailable = false;
        File jsonsDirectory = new File("src/main/resources/Jsons");
        File[] jsonFiles = jsonsDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (jsonFiles != null) {
            for (File jsonFile : jsonFiles) {
                if (jsonFile.getName().equalsIgnoreCase(language + ".json")) {
                    languageAvailable = true;
                    break;
                }
            }
        }

        if (languageAvailable) {
            editProject_ChoiceBox.setValue(language);
        } else {
            editProject_ChoiceBox.setValue(null);
            System.out.println("Language not available in JSON files.");
        }
    }


    private void ConfigComboBox() {
        String JsonsPath = "src/main/resources/Jsons";
        File JsonsDirectory = new File(JsonsPath);
        File[] jsonDirectories = JsonsDirectory.listFiles(File::isFile);
        ObservableList<String> list = FXCollections.observableArrayList();
        assert jsonDirectories != null;
        for (File file : jsonDirectories) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".json")) {
                String fileName = file.getName();
                // Remove the ".json" extension and add the name to the list
                list.add(fileName.substring(0, fileName.lastIndexOf('.')));
            }
        }
        createNewProject_ConfigurationComboBox.setItems(list);
    }


    @FXML
    protected void refresh(ActionEvent event) throws IOException {
        String project = project_comboBox.getSelectionModel().getSelectedItem();
        String csvPath = "src/main/resources/Projects/" + project + "/comparison_report.csv";

        // Load CSV data
        ObservableList<String[]> data = FXCollections.observableArrayList();
        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            List<String[]> lines = reader.readAll();
            data.addAll(lines);
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        // Ensure there are enough columns to handle the CSV data
        if (!data.isEmpty()) {
            // Assuming column1, column2, and column3 are the columns
            TableColumn<String[], String>[] columns = new TableColumn[]{projectColumn, studentIdColumn, resultColumn};

            for (int i = 0; i < columns.length && i < data.get(0).length; i++) {
                final int colIndex = i;
                columns[i].setCellValueFactory(param -> {
                    String[] rowData = param.getValue();
                    if (rowData.length > colIndex) {
                        return javafx.beans.binding.Bindings.createObjectBinding(() -> rowData[colIndex]);
                    } else {
                        return javafx.beans.binding.Bindings.createObjectBinding(() -> "");
                    }
                });
                columns[i].setCellFactory(TextFieldTableCell.forTableColumn());
            }
        }

        // Set the data to the TableView
        reportTable.setItems(data);
    }




    @FXML
    private void handleSaveConfiguration(ActionEvent event) {
        String language = languageTextArea.getText();
        boolean needsCompilation;
        String selected = needsCompilationComboBox.getSelectionModel().getSelectedItem();
        if(selected.equals("Compiler")){
            needsCompilation=true;
        }else{
            needsCompilation=false;
        }

        String compileCommand = compileCommandTextArea.getText();
        String runCommand = runCommandTextArea.getText();

        // Create the Configuration object with the input values
        Configuration config = new Configuration(language, needsCompilation, compileCommand, runCommand);

        // Attempt to save this configuration to a JSON file
        try {
            config.saveToJsonFile(language);  // Using the language as the file name for simplicity
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Configuration saved successfully.");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save configuration: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file)) {
                JSONTokener tokener = new JSONTokener(fis);
                JSONObject config = new JSONObject(tokener);
                languageTextArea.setText(config.getString("language"));
                boolean needsCompilation = config.getBoolean("needs_compilation");
                needsCompilationComboBox.setValue(needsCompilation ? "Compiler" : "Interpreter");
                compileCommandTextArea.setText(config.getString("command"));
                runCommandTextArea.setText(config.getString("run_command"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExport() {
        // Open a DirectoryChooser dialog for selecting the directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(new Stage());
        if (directory != null) {
            try {
                // Create a JSONObject and populate it with data from the UI elements
                JSONObject config = new JSONObject();
                config.put("language", languageTextArea.getText());
                config.put("needs_compilation", "Compiler".equals(needsCompilationComboBox.getValue()));
                config.put("command", compileCommandTextArea.getText());
                config.put("run_command", runCommandTextArea.getText());

                // Use the language name as the file name
                String fileName = languageTextArea.getText() + ".json";
                File file = new File(directory, fileName);

                // Write the JSONObject to the specified file
                try (FileWriter fileWriter = new FileWriter(file)) {
                    fileWriter.write(config.toString(4)); // Pretty print with an indent of 4 spaces
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleUploadCode() {
        String projectName = createNewProject_ProjectName.getText();
        String temporaryDirectoryPath = "src/main/resources/Temporary/";
        String projectDirectoryPath = "src/main/resources/Projects/" + projectName;

        // Create the Temporary directory if it doesn't exist
        File temporaryDirectory = new File(temporaryDirectoryPath);
        if (!temporaryDirectory.exists()) {
            if (temporaryDirectory.mkdirs()) {
                System.out.println("Temporary directory created successfully.");
            } else {
                System.out.println("Failed to create Temporary directory.");
                return;
            }
        }

        // Open a FileChooser dialog for selecting the file to upload
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // Copy the selected file to the Temporary directory
            File temporaryFile = new File(temporaryDirectory, selectedFile.getName());
            try {
                Files.copy(selectedFile.toPath(), temporaryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Code file uploaded to Temporary directory: " + temporaryFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("An error occurred while uploading code file to Temporary directory: " + e.getMessage());
                return;
            }

            // Save the temporary file path for later relocation
            temporaryFilePath = temporaryFile.getAbsolutePath();


        } else {
            System.out.println("File selection cancelled.");
        }
    }



    @FXML
    protected void handleSelectDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory Containing ZIP Files");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            String projectName = selectedDirectory.getName(); // Use the name of the selected directory as the project name
            System.out.println("Selected Directory: " + selectedDirectory.getAbsolutePath());
            try {
                ProjectHandler.handleStudentProjects(selectedDirectory.getAbsolutePath(), projectName);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Your Zips unpacked OK.");
                alert.showAndWait();
            } catch (IOException e) {
                System.err.println("Error unpacking projects: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Cannot unpacked your zips!");
                alert.showAndWait();
            }
        }
    }
    //Scene Changing when clicked on tha main window


    public void setOpenExistingProjectButton(ActionEvent e) throws IOException {

        //Pane view = new FXMLLoader(HelloApplication.class.getResource("OpenExisting.fxml")).load();
        root = FXMLLoader.load(getClass().getResource("OpenExisting.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void setCreateNewProjectButton(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("CreateProject.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void setAssignmentReportsButton(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("ReportAssignment.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setBackButton(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void setUploadStudentButton(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("upload_Student.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void setConfigurations(ActionEvent e) throws IOException {

        //Pane view = new FXMLLoader(HelloApplication.class.getResource("OpenExisting.fxml")).load();
        root = FXMLLoader.load(getClass().getResource("Configurations.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void setHelpButton(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("help.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    //Scene changing ends


    // edit
    public void edit() throws IOException {

        String projectName = editProject_ProjectNameComboBox.getSelectionModel().getSelectedItem();
        String descriptionText = editProject_AssignmentDescription.getText();
        String outputText = editProject_Output.getText();
        String mainClass = editProject_MainClass.getText();
        String configuration = editProject_ChoiceBox.getValue();

        if (configuration != null) {
            System.out.println("Selected configuration: " + configuration);

            // Convert the selected item to lowercase for case-insensitive comparison
            String selectedLanguageLowerCase = configuration.toLowerCase();

            // Get the JSON file path based on the selected item
            String jsonFilePath = "src/main/resources/Jsons/" + selectedLanguageLowerCase + ".json";
            File jsonFile = new File(jsonFilePath);

            // Check if the JSON file exists
            if (jsonFile.exists()) {
                System.out.println("JSON file found for selected language.");

                try {
                    // Read the JSON file content
                    String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()));

                    // Check if compilation is needed based on the JSON content
                    // Customize JSON content for languages that require compilation
                    if (jsonContent.contains("{main}")) {
                        jsonContent = jsonContent.replace("{main}", mainClass);
                    }

                    // Save the edited JSON content to the project directory
                    String projectDirectoryPath = "src/main/resources/Projects/" + projectName;
                    File projectDirectory = new File(projectDirectoryPath);

                    if (!projectDirectory.exists()) {
                        if (projectDirectory.mkdirs()) {
                            System.out.println("Project directory created successfully.");
                        } else {
                            System.out.println("Failed to create project directory.");
                            return;
                        }
                    }

                    // Copy the edited JSON file to the project directory with the project name as the filename
                    String newJsonFileName = projectName + ".json";
                    Path targetPath = Paths.get(projectDirectoryPath + "/" + newJsonFileName);
                    Files.write(targetPath, jsonContent.getBytes());
                    System.out.println("Edited JSON file saved to project directory successfully.");

                } catch (IOException e) {
                    System.out.println("An error occurred while editing or saving JSON file: " + e.getMessage());
                }
            } else {
                System.out.println("JSON file not found for selected language.");
            }
        } else {
            System.out.println("No item selected.");
        }



        if (projectName != null && descriptionText != null && outputText != null) {
            // Update the description file
            try {
                // Define the description file path
                String descriptionFilePath = "src/main/resources/Projects/" + projectName + "/" + projectName + "_description.txt";

                // Create a FileWriter to write to the description file
                FileWriter descriptionWriter = new FileWriter(new File(descriptionFilePath));
                descriptionWriter.write(descriptionText);
                descriptionWriter.close();
                System.out.println("Description file updated successfully.");
            } catch (IOException e) {
                System.out.println("Error updating description file: " + e.getMessage());
            }

            // Update the output file
            try {
                // Define the output file path
                String outputFilePath = "src/main/resources/Projects/" + projectName + "/" + projectName + "_output.txt";

                // Create a FileWriter to write to the output file
                FileWriter outputWriter = new FileWriter(new File(outputFilePath));
                outputWriter.write(outputText);
                outputWriter.close();
                System.out.println("Output file updated successfully.");
            } catch (IOException e) {
                System.out.println("Error updating output file: " + e.getMessage());
            }
        } else {
            System.out.println("Project name, description text, or output text is null.");
        }

        String projectDirectory = "src/main/resources/Projects/" + projectName;
        String temporaryPath = "src/main/resources/Temporary";

        File tempDir = new File(temporaryPath);
        File[] filesToMove = tempDir.listFiles();

        if (filesToMove != null) {
            for (File file : filesToMove) {
                if (file.isFile()) {
                    Path sourcePath = file.toPath();
                    Path destinationPath = Paths.get(projectDirectory, file.getName());

                    try {
                        // Move the file
                        Files.move(sourcePath, destinationPath);
                        System.out.println("File " + file.getName() + " moved successfully from " + temporaryPath + " to " + projectDirectory);
                    } catch (IOException e) {
                        System.err.println("Failed to move the file " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("No files to move in " + temporaryPath);
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Your file is edited");
        alert.showAndWait();

    }

    public void deleteCode (){
        String projectName = editProject_ProjectNameComboBox.getValue();
        String projectDirectoryPath = "src/main/resources/Projects/" + projectName;
        String jsonPath = projectDirectoryPath + "/" + projectName + ".json";
        String codePath = null;
        String previousLanguage = null;


        //get the previous language by json
        try (FileInputStream fis = new FileInputStream(jsonPath)) {
            JSONTokener tokener = new JSONTokener(fis);
            JSONObject jsonObject = new JSONObject(tokener);
            previousLanguage = jsonObject.getString("language");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Language from JSON: " + previousLanguage);

        //add json file names as extensions we need to check
        String jsonDirectoryPath = "src/main/resources/Jsons";
        File jsonDirectory = new File(jsonDirectoryPath);
        ObservableList<String> fileList = FXCollections.observableArrayList();

        if (jsonDirectory.exists() && jsonDirectory.isDirectory()) {
            File[] jsonFiles = jsonDirectory.listFiles((dir, name) -> name.endsWith(".json"));

            if (jsonFiles != null) {
                for (File jsonFile : jsonFiles) {
                    String fileName = jsonFile.getName();
                    String extension = fileName.substring(0, fileName.lastIndexOf('.'));
                    fileList.add(extension);
                }
            }
        }

        //check if there is any file at the project directory with the extension from the observable list
        File directory = new File(projectDirectoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name)-> {
                for (String ext : fileList) {
                    if (name.endsWith("." + ext) || name.endsWith(".cpp")) {
                        return true;
                    }
                }
                return false;
            });

            if (files != null && files.length > 0) {
                codePath = files[0].getAbsolutePath();
            }

        }

        //if there is any file with the any of the extensions delete it.
        if (codePath != null) {
            File fileToDelete = new File(codePath);
            if (fileToDelete.delete()) {
                System.out.println("File " + fileToDelete.getName() + " was deleted successfully.");
            } else {
                System.out.println("Failed to delete the file " + fileToDelete.getName());
            }
        } else {
            System.out.println("No file found with matching extensions.");
        }

    }


    @FXML
    private void handleUploadCodeForEdit() {
        String projectName = editProject_ProjectNameComboBox.getValue();
        String temporaryDirectoryPath = "src/main/resources/Temporary/";
        String projectDirectoryPath = "src/main/resources/Projects/" + projectName;

        // Create the Temporary directory if it doesn't exist
        File temporaryDirectory = new File(temporaryDirectoryPath);
        if (!temporaryDirectory.exists()) {
            if (temporaryDirectory.mkdirs()) {
                System.out.println("Temporary directory created successfully.");
            } else {
                System.out.println("Failed to create Temporary directory.");
                return;
            }
        }

        // Open a FileChooser dialog for selecting the file to upload
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // Copy the selected file to the Temporary directory
            File temporaryFile = new File(temporaryDirectory, selectedFile.getName());
            try {
                Files.copy(selectedFile.toPath(), temporaryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Code file uploaded to Temporary directory: " + temporaryFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("An error occurred while uploading code file to Temporary directory: " + e.getMessage());
                return;
            }

            // Save the temporary file path for later relocation
            temporaryFilePath = temporaryFile.getAbsolutePath();


        } else {
            System.out.println("File selection cancelled.");
        }


    }
    @FXML
    private void handleExportReport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("report.txt");

        File userDirectory = new File(System.getProperty("user.home"));
        if (userDirectory.canRead()) {
            fileChooser.setInitialDirectory(userDirectory);
        }

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String[] row : reportTable.getItems()) {
                    writer.write(String.join("\t", row));
                    writer.newLine();
                }
                writer.flush();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Report exported successfully.");
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to export report: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    public void deleteProject(ActionEvent event){
        String projectName = editProject_ProjectNameComboBox.getValue();
        String projectDirectory = "src/main/resources/Projects/" + projectName;

        Path path = Paths.get(projectDirectory);

        try {
            if (Files.exists(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            System.out.println("Directory deleted successfully.");
        } catch (IOException e) {
            System.err.println("Failed to delete directory: " + e.getMessage());
        }

    }

}
