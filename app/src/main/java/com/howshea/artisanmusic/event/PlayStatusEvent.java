package com.howshea.artisanmusic.event;

import com.howshea.artisanmusic.model.Song;

/**
 * PackageName: com.howshea.artisanmusic.event
 * FileName：   PlayStatusEvent
 * Created by haipo on 2016/11/25.
 */

public class PlayStatusEvent {
    private Song song;
    private boolean isPlaying=true;

    public PlayStatusEvent(Song song) {
        this.song = song;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Song getSong() {
        return song;
    }
}
