package com.howshea.artisanmusic.event;

import com.howshea.artisanmusic.model.Song;

/**
 * PackageName: com.howshea.artisanmusic.event
 * FileName：   DeleteSongEvent
 * Created by haipo on 2016/12/12.
 */

public class DeleteSongEvent {
    private Song mDeleteSong;

    public DeleteSongEvent(Song deleteSong) {
        mDeleteSong = deleteSong;
    }

    public Song getDeleteSong() {
        return mDeleteSong;
    }
}
