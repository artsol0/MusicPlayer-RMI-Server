package com.artsolo.musicplayer.impls;

import com.artsolo.musicplayer.DatabaseManager;
import com.artsolo.musicplayer.JsonManager;
import com.artsolo.musicplayer.entitis.User;
import com.artsolo.musicplayer.services.UserService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    private Connection connection;
    private final DatabaseManager databaseManager;
    private final JsonManager jsonManager = new JsonManager();
    private final Logger logger = Logger.getLogger(DatabaseManager.class.getName());

    public UserServiceImpl() throws RemoteException {
        super();
        databaseManager = new DatabaseManager();
    }

    @Override
    public User loginUser(String data) throws RemoteException {
        String[] loginData = jsonManager.getLoginData(data);
        String username = loginData[0];
        String password = loginData[1];

        connection = databaseManager.connect();

        if (userIsValid(username, password)) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM users WHERE username = ?");
                statement.setString(1, username);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {

                    statement.close();
                    resultSet.close();
                    connection.close();

                    return new User(resultSet.getLong("user_id"), username);
                }

            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error in login user.");
            }
        }

        return null;
    }

    @Override
    public String registerNewUser(String data) throws RemoteException {
        String[] registerData = jsonManager.getRegistrationData(data);
        String username = registerData[0];
        String email = registerData[1];
        String password = registerData[2];

        connection = databaseManager.connect();

        if (usernameIsTaken(username, connection)) {
            return "Username is already taken";
        } else if (emailIsTaken(email, connection)) {
            return "The email address is already taken";
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);

            int resultOfAdding = statement.executeUpdate();

            if (resultOfAdding > 0) {
                statement.close();
                connection.close();

                return "Registration was completed successfully";
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error in new user registration.");
        }

        return "Something went wrong. Try again latter";
    }

    public boolean userIsValid(String username, String password) {
        boolean valid = false;

        connection = databaseManager.connect();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                valid = count > 0;
            }

            statement.close();
            resultSet.close();
            connection.close();

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error in validation user.");
        }

        return valid;
    }

    public boolean usernameIsTaken(String username, Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {

                statement.close();
                resultSet.close();

                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking for username.");
        }
        return false;
    }

    public boolean emailIsTaken(String email, Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {

                statement.close();
                resultSet.close();

                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking for email.");
        }
        return false;
    }
}