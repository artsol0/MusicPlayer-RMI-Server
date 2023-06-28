package com.artsolo.musicplayer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            MusicService musicService = new MusicServiceImpl();
            Registry registry = LocateRegistry.createRegistry(5294);
            registry.rebind("MusicPlayer", musicService);
            System.out.println("Server start successful");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
