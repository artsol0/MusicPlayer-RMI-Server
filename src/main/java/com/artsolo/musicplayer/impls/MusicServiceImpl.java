package com.artsolo.musicplayer.impls;

import com.artsolo.musicplayer.DatabaseManager;
import com.artsolo.musicplayer.entitis.Music;
import com.artsolo.musicplayer.services.MusicService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MusicServiceImpl extends UnicastRemoteObject implements MusicService {
    private int userId;

    private Connection connection;
    private final DatabaseManager databaseManager;
    private final Logger logger = Logger.getLogger(DatabaseManager.class.getName());

    public MusicServiceImpl() throws RemoteException {
        super();
        databaseManager = new DatabaseManager();
    }

    @Override
    public List<Music> getMusic() throws RemoteException {
        List<Music> musicList = new ArrayList<>();

        connection = databaseManager.connect();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT music.music_id, music.title, performers.performername FROM liked JOIN music ON liked.music_id = music.music_id JOIN performers ON music.performer_id = performers.performer_id WHERE liked.user_id = ?");
            statement.setString(1, String.valueOf(userId));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Music music = new Music(resultSet.getLong("music_id"), resultSet.getString("title"), resultSet.getString("performer"));
                musicList.add(music);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error in listing music.");
        }

        return musicList;
    }

    @Override
    public List<Music> getMusicByGenre(int genreId) throws RemoteException {
        List<Music> musicList = new ArrayList<>();

        connection = databaseManager.connect();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT music_id, music.title, performers.performername FROM music JOIN performers ON music.performer_id = performers.performer_id WHERE genre_id = ?");
            statement.setString(1, String.valueOf(genreId));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Music music = new Music(resultSet.getLong("music_id"), resultSet.getString("title"), resultSet.getString("performer"));
                musicList.add(music);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error in listing music.");
        }

        return musicList;
    }

    @Override
    public List<Music> getMusicByString(String searchString) throws RemoteException {
        List<Music>musicList = new ArrayList<>();

        connection = databaseManager.connect();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT music_id, music.title, performers.performername FROM music JOIN performers ON music.performer_id = performers.performer_id WHERE title LIKE ? OR performername LIKE ?");
            statement.setString(1, "%" + searchString + "%");
            statement.setString(2, "%" + searchString + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Music music = new Music(resultSet.getLong("music_id"), resultSet.getString("title"), resultSet.getString("performer"));
                musicList.add(music);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error in listing music.");
        }

        return musicList;
    }

    @Override
    public List<Music> likedSearch(String searchString) throws RemoteException {
        List<Music> musicList = new ArrayList<>();

        connection = databaseManager.connect();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT music.music_id, music.title, performers.performername FROM liked JOIN music ON liked.music_id = music.music_id JOIN performers ON music.performer_id = performers.performer_id WHERE title LIKE ? OR performername LIKE ?");
            statement.setString(1, "%" + searchString + "%");
            statement.setString(2, "%" + searchString + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Music music = new Music(resultSet.getLong("music_id"), resultSet.getString("title"), resultSet.getString("performer"));
                musicList.add(music);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error in listing music.");
        }

        return musicList;
    }

    @Override
    public byte[] getMusicInBytes(String musicId) throws RemoteException {
        byte[] data = null;

        connection = databaseManager.connect();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT path FROM music WHERE music_id = ?");
            statement.setString(1, musicId);
            ResultSet resultSet = statement.executeQuery();

            Path path = null;
            while (resultSet.next()) {
                path = Paths.get(resultSet.getString("path"));
            }

            if (path != null) {
                data = Files.readAllBytes(path);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error in reading bites from file.");
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error in listing music.");
        }
        return data;
    }

    @Override
    public String addMusicToLiked(String musicId) throws RemoteException {
        String result = null;

        connection = databaseManager.connect();

        try {
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
            logger.log(Level.WARNING, "Error while adding music to liked list.");
        }

        return result;
    }

    @Override
    public String removeMusicFromLiked(String musicId) throws RemoteException {
        String result = null;

        connection = databaseManager.connect();

        try {

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
            logger.log(Level.WARNING, "Error while removing music from liked list.");
        }

        return result;
    }
}
