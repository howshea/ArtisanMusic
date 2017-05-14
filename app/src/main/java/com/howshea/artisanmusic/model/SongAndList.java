package com.howshea.artisanmusic.model;

import java.util.UUID;

/**
 * PackageName: com.howshea.artisanmusic.model
 * FileName：   SongAndList
 * Created by haipo on 2016/12/9.
 */

public class SongAndList {
    private long mSongId;
    private UUID mSongListId;

    public SongAndList() {
    }

    long getSongId() {
        return mSongId;
    }

    public void setSongId(long songId) {
        mSongId = songId;
    }

    UUID getSongListId() {
        return mSongListId;
    }

    public void setSongListId(UUID songListId) {
        mSongListId = songListId;
    }
}
