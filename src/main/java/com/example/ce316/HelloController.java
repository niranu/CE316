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
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import com.opencsv.CSVReader;

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
    private void createProject(ActionEvent event) {
        System.out.println("Project creation initiated.");
        projectCreator = new CreateNewProject();
        String projectName = createNewProject_ProjectName.getText();
        String description = createNewProject_ProjectDescription.getText();
        String expectedOutput = createNewProject_ExpectedOutput.getText();

        String selectedLanguage = createNewProject_ConfigurationComboBox.getSelectionModel().getSelectedItem();
        if (selectedLanguage != null) {
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
                    boolean needsCompilation = jsonContent.contains("\"needs_compilation\": true");

                    if (needsCompilation) {
                        // Customize JSON content for languages that require compilation
                        String mainClassName = mainClassNameTextArea.getText();
                        if (jsonContent.contains("{main}")) {
                            jsonContent = jsonContent.replace("{main}", mainClassName);
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
                    } else {
                        System.out.println("Compilation not needed for selected language.");
                        // Handle cases where compilation is not needed
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while editing or saving JSON file: " + e.getMessage());
                }
            } else {
                System.out.println("JSON file not found for selected language.");
            }
        } else {
            System.out.println("No item selected.");
        }

        projectCreator.createProjectFile(projectName, description, expectedOutput);
    }





    private void copyJsonFile(String selectedItem, String projectName) {
        String jsonFileName = selectedItem.toLowerCase() + ".json";
        Path source = Paths.get("src/main/resources/Jsons/" + jsonFileName);
        Path destination = Paths.get("src/main/resources/Projects/" + projectName + "/" + jsonFileName);

        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("JSON file copied successfully.");
        } catch (IOException e) {
            System.out.println("Error copying JSON file: " + e.getMessage());
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
    void initialize() {
        project_comboBox.setOnShowing(event -> projectComboBox());
        createNewProject_ConfigurationComboBox.setOnShowing(event -> ConfigComboBox());



    }

    //Run button action for Uploaded Student Files
    Compiler compiler = new Compiler();

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







    //Scene Changing when clicked on tha main window
    private Stage stage;
    private Scene scene;
    @FXML
    private Parent root;

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

    /*public void handleCreateProjectButtonAction(ActionEvent event){
        projectCreater = new CreateNewProject();
        projectCreater.createProjectFile();
    }*/










}
