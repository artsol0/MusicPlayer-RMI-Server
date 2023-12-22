package com.artsolo.musicplayer.impls;

import com.artsolo.musicplayer.models.Album;
import com.artsolo.musicplayer.models.Music;
import com.artsolo.musicplayer.services.AlbumService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlbumServiceImpl extends UnicastRemoteObject implements AlbumService {

    private final Logger logger = Logger.getLogger(AlbumServiceImpl.class.getName());

    public AlbumServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean createNewAlbum(int userId, String newAlbumTitle) throws RemoteException {
        boolean result = false;

        Connection connection = connect();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO albums(user_id, albumname) VALUES (?, ?)");
            statement.setString(1, String.valueOf(userId));
            statement.setString(2, newAlbumTitle);

            int resultOfCreating = statement.executeUpdate();

            result = resultOfCreating > 0;

            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while creating new album.");
        }

        return result;
    }

    @Override
    public List<Album> getAlbum(int userId) throws RemoteException {
        List<Album> albumsList = new ArrayList<>();

        Connection connection = connect();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT album_id, albumname FROM albums WHERE user_id = ?");
            statement.setString(1, String.valueOf(userId));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                albumsList.add(new Album(resultSet.getInt("album_id"),resultSet.getString("albumname")));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while removing music from liked list.");
        }

        return albumsList;
    }

    @Override
    public String addMusicToAlbum(int albumId, int musicId) throws RemoteException {
        String result = null;

        Connection connection = connect();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM album_music WHERE album_id = ? AND music_id = ?");
            statement.setString(1, String.valueOf(albumId));
            statement.setString(2, String.valueOf(musicId));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    result = "Song already in album";
                } else {
                    statement = connection.prepareStatement("INSERT INTO album_music(album_id, music_id) VALUES (?, ?)");
                    statement.setString(1, String.valueOf(albumId));
                    statement.setString(2, String.valueOf(musicId));

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
            logger.log(Level.WARNING, "Error while adding music to album.");
        }

        return result;
    }

    @Override
    public List<Music> getAlbumMusic(int albumId) throws RemoteException {
        List<Music>  musicList = new ArrayList<>();

        Connection connection = connect();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT album_music.music_id, music.title, performers.performername FROM album_music INNER JOIN music ON album_music.music_id = music.music_id INNER JOIN performers ON music.performer_id = performers.performer_id WHERE album_music.album_id = ?");
            statement.setString(1, String.valueOf(albumId));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Music music = new Music(resultSet.getInt("music_id"), resultSet.getString("title"), resultSet.getString("performername"));
                musicList.add(music);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error in listing album music.");
        }

        return musicList;
    }

    @Override
    public String removeMusicFromAlbum(int albumId, int musicId) throws RemoteException {
        String result = null;

        Connection connection = connect();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM album_music WHERE album_id = ? AND music_id = ?");
            statement.setString(1, String.valueOf(albumId));
            statement.setString(2, String.valueOf(musicId));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count < 0) {
                    result = "Song already removed";
                } else {
                    statement = connection.prepareStatement("DELETE FROM album_music WHERE album_id = ? AND music_id = ?");
                    statement.setString(1, String.valueOf(albumId));
                    statement.setString(2, String.valueOf(musicId));

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
            logger.log(Level.WARNING, "Error while removing music from album.");
        }

        return result;
    }

    @Override
    public boolean removeAlbum(int albumId) throws RemoteException {
        boolean result = false;

        Connection connection = connect();

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM album_music WHERE album_id = ?");
            statement.setString(1, String.valueOf(albumId));

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted >= 0) {
                statement = connection.prepareStatement("DELETE FROM albums WHERE album_id = ?");
                statement.setString(1, String.valueOf(albumId));

                rowsDeleted = statement.executeUpdate();

                result = rowsDeleted > 0;
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while remove album.");
        }

        return result;
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
