package com.artsolo.musicplayer;

import com.artsolo.musicplayer.impls.AlbumServiceImpl;
import com.artsolo.musicplayer.impls.MusicServiceImpl;
import com.artsolo.musicplayer.impls.UserServiceImpl;
import com.artsolo.musicplayer.services.AlbumService;
import com.artsolo.musicplayer.services.MusicService;
import com.artsolo.musicplayer.services.UserService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(5294);

            MusicService musicService = new MusicServiceImpl();
            registry.rebind("MusicService", musicService);

            AlbumService albumService = new AlbumServiceImpl();
            registry.rebind("AlbumService", albumService);

            UserService userService = new UserServiceImpl();
            registry.rebind("UserService", userService);

            System.out.println("The server started successfully.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
