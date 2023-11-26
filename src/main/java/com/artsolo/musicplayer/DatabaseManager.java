package com.artsolo.musicplayer;

import com.artsolo.musicplayer.entitis.User;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    private final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private Connection connection;

    public Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer","root","p00rGe()n");
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.WARNING, "The connection was not established");
        }
        return connection;
    }

}
