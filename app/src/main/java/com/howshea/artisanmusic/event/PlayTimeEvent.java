package com.howshea.artisanmusic.event;

/**
 * PackageName: com.howshea.artisanmusic.event
 * FileName：   PlayTimeEvent
 * Created by haipo on 2016/11/30.
 */

public class PlayTimeEvent {
    private int time;

    public PlayTimeEvent(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
