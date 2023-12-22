package com.artsolo.musicplayer.impls;

import com.artsolo.musicplayer.JsonManager;
import com.artsolo.musicplayer.models.User;
import com.artsolo.musicplayer.services.UserService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    private final JsonManager jsonManager = new JsonManager();
    private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    public UserServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public User loginUser(String data) throws RemoteException {
        String[] loginData = jsonManager.getLoginData(data);
        String username = loginData[0];
        String password = loginData[1];

        Connection connection = connect();

        if (userIsValid(username, password, connection)) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM users WHERE username = ?");
                statement.setString(1, username);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {

                    User user = new User(resultSet.getInt("user_id"), username);

                    statement.close();
                    resultSet.close();
                    connection.close();

                    return user;
                }

            } catch (SQLException e) {
                e.printStackTrace();
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

        Connection connection = connect();

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

    private boolean userIsValid(String username, String password, Connection connection) {
        boolean valid = false;

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

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error in validation user.");
        }

        return valid;
    }

    private boolean usernameIsTaken(String username, Connection connection) {
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

    private boolean emailIsTaken(String email, Connection connection) {
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

    private Connection connect() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer","root","p00rGe()n");
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.WARNING, "The connection was not established");
        }
        return connection;
    }
}
