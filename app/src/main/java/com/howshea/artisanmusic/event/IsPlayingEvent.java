package com.howshea.artisanmusic.event;

/**
 * PackageName: com.howshea.artisanmusic.event
 * FileName：   IsPlayingEvent
 * Created by haipo on 2016/12/14.
 */

public class IsPlayingEvent {
    private boolean isPlaying=true;

    public IsPlayingEvent(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
