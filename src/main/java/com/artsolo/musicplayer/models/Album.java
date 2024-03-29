package com.artsolo.musicplayer.models;

import java.io.Serializable;

public class Album implements Serializable {
    private final int id;
    private final String name;

    public Album(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
