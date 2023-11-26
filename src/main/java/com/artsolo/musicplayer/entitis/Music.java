package com.artsolo.musicplayer.entitis;

import java.io.Serializable;

public class Music implements Serializable {
    private final Long id;
    private final String title;
    private final String performer;

    public Music(Long id, String title, String performer) {
        this.id = id;
        this.title = title;
        this.performer = performer;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPerformer() {
        return performer;
    }
}
