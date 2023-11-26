package com.artsolo.musicplayer.services;

import com.artsolo.musicplayer.entitis.Music;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

public interface MusicService extends Remote {
    public List<Music> getMusic() throws RemoteException;
    public List<Music> getMusicByGenre(int genreId) throws RemoteException;
    public List<Music> getMusicByString(String searchString) throws RemoteException;
    public List<Music> likedSearch(String searchString) throws RemoteException;
    public byte[] getMusicInBytes(String musicId) throws RemoteException;
    public String addMusicToLiked(String musicId) throws RemoteException;
    public String removeMusicFromLiked(String musicId) throws RemoteException;
}
