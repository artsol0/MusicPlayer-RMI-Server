package com.artsolo.musicplayer.entitis;

import java.io.Serializable;

public class Album implements Serializable {
    private final Long id;
    private final String name;

    public Album(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
