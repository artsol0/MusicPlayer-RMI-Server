module com.artsolo.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires com.google.gson;
    requires java.sql;
    requires mysql.connector.j;


    opens com.artsolo.musicplayer to javafx.fxml;
    exports com.artsolo.musicplayer;
}