module com.artsolo.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires com.google.gson;
    requires java.sql;
    requires mysql.connector.j;


    opens com.artsolo.musicplayer to javafx.fxml;
    exports com.artsolo.musicplayer;
    exports com.artsolo.musicplayer.entitis;
    opens com.artsolo.musicplayer.entitis to javafx.fxml;
    exports com.artsolo.musicplayer.services;
    opens com.artsolo.musicplayer.services to javafx.fxml;
    exports com.artsolo.musicplayer.impls;
    opens com.artsolo.musicplayer.impls to javafx.fxml;
    exports com.artsolo.musicplayer.singletons;
    opens com.artsolo.musicplayer.singletons to javafx.fxml;
}