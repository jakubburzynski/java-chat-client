module com.example.javachatclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.json;

    opens com.example.javachatclient to javafx.fxml;
    exports com.example.javachatclient;
}