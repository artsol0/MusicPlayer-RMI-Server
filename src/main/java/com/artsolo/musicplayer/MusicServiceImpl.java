package com.artsolo.musicplayer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MusicServiceImpl extends UnicastRemoteObject implements MusicService {
    private int userId;

    protected MusicServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean login(String loginData) throws RemoteException {

        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(loginData, JsonObject.class);
        String username = userData.get("username").getAsString();
        String password = userData.get("password").getAsString();

        boolean result = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer","root","p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                result = count > 0;
            }

            if (result) {
                statement = connection.prepareStatement("SELECT user_id FROM users WHERE username = ?");
                statement.setString(1, username);

                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    this.userId = resultSet.getInt("user_id");
                }

            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String registration(String registrationData) throws RemoteException {

        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(registrationData, JsonObject.class);
        String username = userData.get("username").getAsString();
        String email = userData.get("email").getAsString();
        String password = userData.get("password").getAsString();

        String result = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer","root","p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                result = "Username is already taken";
            } else {
                statement = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
                statement.setString(1, email);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    result = "The email address is already in use";

                } else {
                    statement = connection.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
                    statement.setString(1, username);
                    statement.setString(2, email);
                    statement.setString(3, password);

                    int resultOfAdding = statement.executeUpdate();

                    if (resultOfAdding > 0) {
                        result = "Registration was completed successfully";
                    } else {
                        result = "Something went wrong. Try again latter";
                    }
                }
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String[][] getMusic() throws RemoteException {
        List<String[]> musicList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT music.music_id, music.title, performers.performername FROM liked JOIN music ON liked.music_id = music.music_id JOIN performers ON music.performer_id = performers.performer_id WHERE liked.user_id = ?");
            statement.setString(1, String.valueOf(userId));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String[] musicData = new String[3];
                musicData[0] = resultSet.getString("music_id");
                musicData[1] = resultSet.getString("title");
                musicData[2] = resultSet.getString("performername");
                musicList.add(musicData);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return musicList.toArray(new String[0][0]);
    }

    @Override
    public String[][] getMusic(int genreId) throws RemoteException {
        List<String[]> musicList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT music_id, music.title, performers.performername FROM music JOIN performers ON music.performer_id = performers.performer_id WHERE genre_id = ?");
            statement.setString(1, String.valueOf(genreId));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String[] musicData = new String[3];
                musicData[0] = resultSet.getString("music_id");
                musicData[1] = resultSet.getString("title");
                musicData[2] = resultSet.getString("performername");
                musicList.add(musicData);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return musicList.toArray(new String[0][0]);
    }

    @Override
    public String[][] getMusic(String searchString) throws RemoteException {
        List<String[]> musicList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT music_id, music.title, performers.performername FROM music JOIN performers ON music.performer_id = performers.performer_id WHERE title LIKE ? OR performername LIKE ?");
            statement.setString(1, "%" + searchString + "%");
            statement.setString(2, "%" + searchString + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String[] musicData = new String[3];
                musicData[0] = resultSet.getString("music_id");
                musicData[1] = resultSet.getString("title");
                musicData[2] = resultSet.getString("performername");
                musicList.add(musicData);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return musicList.toArray(new String[0][0]);
    }

    @Override
    public String[][] likedSearch(String searchString) throws RemoteException {
        List<String[]> musicList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT music.music_id, music.title, performers.performername FROM liked JOIN music ON liked.music_id = music.music_id JOIN performers ON music.performer_id = performers.performer_id WHERE title LIKE ? OR performername LIKE ?");
            statement.setString(1, "%" + searchString + "%");
            statement.setString(2, "%" + searchString + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String[] musicData = new String[3];
                musicData[0] = resultSet.getString("music_id");
                musicData[1] = resultSet.getString("title");
                musicData[2] = resultSet.getString("performername");
                musicList.add(musicData);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return musicList.toArray(new String[0][0]);
    }

    @Override
    public byte[] getMusicData(String musicId) throws RemoteException {
        byte[] data = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT path FROM music WHERE music_id = ?");
            statement.setString(1, musicId);
            ResultSet resultSet = statement.executeQuery();

            Path path = null;
            while (resultSet.next()) {
                path = Paths.get(resultSet.getString("path"));
            }
            data = Files.readAllBytes(path);

            resultSet.close();
            statement.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String addToLiked(String musicId) throws RemoteException {
        String result = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM liked WHERE user_id = ? AND music_id = ?");
            statement.setString(1, String.valueOf(userId));
            statement.setString(2, musicId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    result = "Song already in your Liked List";
                } else {
                    statement = connection.prepareStatement("INSERT INTO liked(user_id, music_id) VALUES (?, ?)");
                    statement.setString(1, String.valueOf(userId));
                    statement.setString(2, musicId);


                    int resultOfAdding = statement.executeUpdate();

                    if (resultOfAdding > 0) {
                        result = "Song was added to your Liked List!";
                    } else {
                        result = "Something went wrong.";
                    }
                }
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String removeFromLiked(String musicId) throws RemoteException {
        String result = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM liked WHERE user_id = ? AND music_id = ?");
            statement.setString(1, String.valueOf(userId));
            statement.setString(2, musicId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count < 0) {
                    result = "Song already removed";
                } else {
                    statement = connection.prepareStatement("DELETE FROM liked WHERE user_id = ? AND music_id = ?");
                    statement.setString(1, String.valueOf(userId));
                    statement.setString(2, musicId);

                    int rowsDeleted = statement.executeUpdate();

                    if (rowsDeleted > 0) {
                        result = "Song was removed from Liked List";
                    } else {
                        result = "Something went wrong.";
                    }
                }
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean createNewAlbum(String newAlbumTitle) throws RemoteException {
        boolean result = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("INSERT INTO albums(user_id, albumname) VALUES (?, ?)");
            statement.setString(1, String.valueOf(userId));
            statement.setString(2, newAlbumTitle);

            int resultOfCreating = statement.executeUpdate();

            result = resultOfCreating > 0;

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String[][] getAlbum() throws RemoteException {
        List<String[]> albumsList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT album_id, albumname FROM albums WHERE user_id = ?");
            statement.setString(1, String.valueOf(userId));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String[] albumData = new String[2];
                albumData[0] = resultSet.getString("album_id");
                albumData[1] = resultSet.getString("albumname");
                albumsList.add(albumData);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return albumsList.toArray(new String[0][0]);
    }

    @Override
    public String addToAlbum(String albumId, String musicId) throws RemoteException {
        String result = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM album_music WHERE album_id = ? AND music_id = ?");
            statement.setString(1, albumId);
            statement.setString(2, musicId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    result = "Song already in album";
                } else {
                    statement = connection.prepareStatement("INSERT INTO album_music(album_id, music_id) VALUES (?, ?)");
                    statement.setString(1, albumId);
                    statement.setString(2, musicId);

                    int resultOfCreating = statement.executeUpdate();

                    if (resultOfCreating > 0) {
                        result = "Song was added to album!";
                    } else {
                        result = "Something went wrong.";
                    }
                }
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String[][] getAlbumMusic(String albumId) throws RemoteException {
        List<String[]> musicList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT album_music.music_id, music.title, performers.performername FROM album_music INNER JOIN music ON album_music.music_id = music.music_id INNER JOIN performers ON music.performer_id = performers.performer_id WHERE album_music.album_id = ?");
            statement.setString(1,albumId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String[] musicData = new String[3];
                musicData[0] = resultSet.getString("music_id");
                musicData[1] = resultSet.getString("title");
                musicData[2] = resultSet.getString("performername");
                musicList.add(musicData);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return musicList.toArray(new String[0][0]);
    }

    @Override
    public String removeFromAlbum(String albumId, String musicId) throws RemoteException {
        String result = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM album_music WHERE album_id = ? AND music_id = ?");
            statement.setString(1, albumId);
            statement.setString(2, musicId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count < 0) {
                    result = "Song already removed";
                } else {
                    statement = connection.prepareStatement("DELETE FROM album_music WHERE album_id = ? AND music_id = ?");
                    statement.setString(1, albumId);
                    statement.setString(2, musicId);

                    int rowsDeleted = statement.executeUpdate();

                    if (rowsDeleted > 0) {
                        result = "Song was removed from album";
                    } else {
                        result = "Something went wrong.";
                    }
                }
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean removeAlbum(String albumId) throws RemoteException {
        boolean result = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MusicPlayer", "root", "p00rGe()n");

            PreparedStatement statement = connection.prepareStatement("DELETE FROM album_music WHERE album_id = ?");
            statement.setString(1, albumId);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted >= 0) {
                statement = connection.prepareStatement("DELETE FROM albums WHERE album_id = ?");
                statement.setString(1, albumId);

                rowsDeleted = statement.executeUpdate();

                result = rowsDeleted > 0;
            } else {
                result = false;
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }


}
