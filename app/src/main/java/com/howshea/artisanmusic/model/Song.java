package com.howshea.artisanmusic.model;

import java.io.Serializable;

/**
 * Created by haipo
 *  on 2016/11/5.
 */

public class Song implements Serializable {
    private long mSongId;
    // 音乐标题
    private String mTitle;
    // 艺术家
    private String mArtist;
    // 专辑
    private String mAlbum;
    // 持续时间
    private long mDuration;
    // 音乐路径
    private String mUri;
    // 专辑封面路径
    private String mCoverUri;
    // 文件名
    private String mFileName;
    // 文件大小
    private long mFileSize;
    // 发行日期
    private String mYear;

    private boolean mIsLike = false;

    public void setLike(boolean like) {
        mIsLike = like;
    }

    public boolean isLike() {
        return mIsLike;
    }

    public long getSongId() {
        return mSongId;
    }

    public void setSongId(long songId) {
        mSongId = songId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }


    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public String getCoverUri() {
        return mCoverUri;
    }

    public void setCoverUri(String coverUri) {
        mCoverUri = coverUri;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    long getFileSize() {
        return mFileSize;
    }

    public void setFileSize(long fileSize) {
        mFileSize = fileSize;
    }

    String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof Song) {
            Song song = (Song) obj;
            //id和歌曲名都相同，则认为是一首歌
            return (this.mSongId == song.mSongId) && (this.mTitle.equals(song.mTitle));
        }
        return false;
    }
}
