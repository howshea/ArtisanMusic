package com.howshea.artisanmusic.model;


import java.util.UUID;

/**
 * PackageName: com.haipo.artisanmusic.model
 * FileName：   SongList
 * Created by haipo on 2016/11/8.
 */

public class SongList {
    private UUID mId;
    private String mTitle;
    private int songCount=0;


    public SongList(){
        mId= UUID.randomUUID();
    }

    public SongList(UUID id){
        mId=id;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }
}
