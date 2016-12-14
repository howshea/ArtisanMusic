package com.howshea.artisanmusic.event;

import com.howshea.artisanmusic.model.Song;

/**
 * PackageName: com.howshea.artisanmusic.event
 * FileName：   UpdateSongEvent
 * Created by haipo on 2016/12/12.
 */

public class UpdateSongEvent {
    private Song mRevisedSong;

    public UpdateSongEvent(Song revisedSong) {
        mRevisedSong = revisedSong;
    }

    public Song getRevisedSong() {
        return mRevisedSong;
    }
}
