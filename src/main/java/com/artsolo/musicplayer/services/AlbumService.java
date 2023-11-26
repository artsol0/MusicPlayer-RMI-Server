package com.artsolo.musicplayer.services;

import com.artsolo.musicplayer.entitis.Album;
import com.artsolo.musicplayer.entitis.Music;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AlbumService extends Remote {
    public boolean createNewAlbum(String newAlbumTitle) throws RemoteException;
    public List<Album> getAlbum() throws RemoteException;
    public String addMusicToAlbum(String albumId, String musicId) throws RemoteException;
    public List<Music> getAlbumMusic(String albumId) throws RemoteException;
    public String removeMusicFromAlbum(String albumId, String musicId) throws RemoteException;
    public boolean removeAlbum(String albumId) throws RemoteException;
}
