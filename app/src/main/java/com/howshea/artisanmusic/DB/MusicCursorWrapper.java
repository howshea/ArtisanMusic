package com.howshea.artisanmusic.DB;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.howshea.artisanmusic.DB.DBSchema.SongAndListTable;
import com.howshea.artisanmusic.DB.DBSchema.SongListTable;
import com.howshea.artisanmusic.DB.DBSchema.SongTable;
import com.howshea.artisanmusic.model.Song;
import com.howshea.artisanmusic.model.SongAndList;
import com.howshea.artisanmusic.model.SongList;

import java.util.UUID;

/**
 * Created by haipo
 * on 2016/11/5.
 *
 */

public class MusicCursorWrapper extends CursorWrapper {
    public MusicCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    /**
     * 查询歌曲表
     */
    public Song getSong() {
        long songId = getLong(getColumnIndex(SongTable.SONG_ID));
        String title = getString(getColumnIndex(SongTable.TITLE));
        String artist = getString(getColumnIndex(SongTable.ARTIST));
        String album = getString(getColumnIndex(SongTable.ALBUM));
        long duration = getLong(getColumnIndex(SongTable.DURATION));
        String uri = getString(getColumnIndex(SongTable.URI));
        String coverUri = getString(getColumnIndex(SongTable.COVERURI));
        String filename = getString(getColumnIndex(SongTable.FILENAME));
        long filesize = getLong(getColumnIndex(SongTable.FILESIZE));
        String year = getString(getColumnIndex(SongTable.YEAR));
        boolean islike = getInt(getColumnIndex(SongTable.ISLIKE)) != 0;
        Song song = new Song();
        song.setSongId(songId);
        song.setTitle(title);
        song.setArtist(artist);
        song.setAlbum(album);
        song.setDuration(duration);
        song.setUri(uri);
        song.setCoverUri(coverUri);
        song.setFileName(filename);
        song.setFileSize(filesize);
        song.setYear(year);
        song.setLike(islike);
        return song;
    }

    /**
     * 查询歌单表
     */
    public SongList getSongList() {
        String uuidString = getString(getColumnIndex(SongListTable.UUID));
        String title = getString(getColumnIndex(SongListTable.TITLE));
        int song_count = getInt(getColumnIndex(SongListTable.SONG_COUNT));
        SongList songList = new SongList(UUID.fromString(uuidString));
        songList.setTitle(title);
        songList.setSongCount(song_count);
        return songList;
    }

    /**
     * 查询中间表
     */
    public SongAndList getSongAndList() {
        Long songId = getLong(getColumnIndex(SongAndListTable.SONG_ID));
        UUID songlist_uuid = UUID.fromString(getString(getColumnIndex(SongAndListTable.SONGLIST_UUID)));
        SongAndList songAndList = new SongAndList();
        songAndList.setSongId(songId);
        songAndList.setSongListId(songlist_uuid);
        return songAndList;
    }

}
