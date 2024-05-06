module com.example.ce316 {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
                        requires org.kordamp.bootstrapfx.core;
    requires org.json;

    opens com.example.ce316 to javafx.fxml;
    exports com.example.ce316;
}