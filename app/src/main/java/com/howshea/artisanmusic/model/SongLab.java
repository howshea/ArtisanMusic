package com.howshea.artisanmusic.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.howshea.artisanmusic.DB.DBSchema.SongAndListTable;
import com.howshea.artisanmusic.DB.DBSchema.SongListTable;
import com.howshea.artisanmusic.DB.DBSchema.SongTable;
import com.howshea.artisanmusic.DB.MusicCursorWrapper;
import com.howshea.artisanmusic.DB.MusicDbOpenHelper;
import com.howshea.artisanmusic.app.AppApplication;
import com.howshea.artisanmusic.base.baseutils.LogUtils;
import com.howshea.artisanmusic.utils.DBUtils;
import com.howshea.artisanmusic.utils.SongUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import rx.Observable;

/**
 * PackageName: com.haipo.artisanmusic.model
 * FileName：   SongLab
 * Created by haipo on 2016/11/6.
 */

public class SongLab {
    private SQLiteDatabase mDatabase;
    private volatile static SongLab sInstance = null;

    private SongLab() {
        mDatabase = new MusicDbOpenHelper(AppApplication.getAppContext()).getWritableDatabase();
    }

    public static SongLab get() {
        if (sInstance == null) {
            synchronized (SongLab.class) {
                if (sInstance == null) {
                    sInstance = new SongLab();
                }
            }
        }
        return sInstance;
    }

    private static final class Holder {
        private static SongLab sInstance = new SongLab();
    }

    public void setDatabase() {

    }

    /**
     * 操作音乐的业务
     */

    //扫描磁盘音乐
    public Observable<List<Song>> scanMusic() {
        return DBUtils.markObservable(SongUtil.scanSongs(AppApplication.getAppContext()));
    }

    //获取所有音乐
    public Observable<ArrayList<Song>> getSongsByRx() {
        return DBUtils.markObservable(getSongsCallable());
    }


    public Callable<ArrayList<Song>> getSongsCallable() {
        return new Callable<ArrayList<Song>>() {
            @Override
            public ArrayList<Song> call() {
                return getSongs();
            }
        };
    }


    public ArrayList<Song> getSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        MusicCursorWrapper cursor = querySongs(null, null);
        try {
            if (cursor.getCount() == 0)
                return null;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                songs.add(cursor.getSong());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return songs;
    }

    private MusicCursorWrapper querySongs(String whereClause, String[] args) {
        Cursor cursor = mDatabase.query(SongTable.TABLE_NAME,
                null,
                whereClause,
                args,
                null,
                null,
                SongTable.TITLE);
        return new MusicCursorWrapper(cursor);
    }

    private static ContentValues getSongContentValues(Song song) {
        ContentValues values = new ContentValues();
        values.put(SongTable.SONG_ID, song.getSongId());
        values.put(SongTable.TITLE, song.getTitle());
        values.put(SongTable.ARTIST, song.getArtist());
        values.put(SongTable.ALBUM, song.getAlbum());
        values.put(SongTable.DURATION, song.getDuration());
        values.put(SongTable.URI, song.getUri());
        values.put(SongTable.COVERURI, song.getCoverUri());
        values.put(SongTable.FILENAME, song.getFileName());
        values.put(SongTable.FILESIZE, song.getFileSize());
        values.put(SongTable.YEAR, song.getYear());
        values.put(SongTable.ISLIKE, song.isLike() == false ? 0 : 1);
        return values;
    }

    public Song getSong(long id) {
        MusicCursorWrapper cursor = querySongs(SongTable.SONG_ID + "= ?", new String[]{String.valueOf(id)});
        try {
            if (cursor.getCount() == 0) {
                LogUtils.logi("歌曲不存在" + id + "  " + cursor.getCount() + String.valueOf(id));
                return null;
            } else {
                cursor.moveToFirst();
                return cursor.getSong();
            }
        } finally {
            cursor.close();
        }
    }

    public Song getSongByTitle(String title) {
        MusicCursorWrapper cursor = querySongs(SongTable.TITLE + "= ?", new String[]{title});
        try {
            if (cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();
                return cursor.getSong();
            }
        } finally {
            cursor.close();
        }
    }


    public void addSong(Song song) {
        ContentValues values = getSongContentValues(song);
        mDatabase.insert(SongTable.TABLE_NAME, null, values);
    }

    public int deleteSong(long id) {
        int song = mDatabase.delete(SongTable.TABLE_NAME, SongTable.SONG_ID + "= ?", new String[]{Long.toString(id)});
        ArrayList<SongList> songLists = getSongListsOfSongId(id);
        if (songLists != null)
            for (SongList songList : songLists) {
                songList.setSongCount(songList.getSongCount() - 1);
                updateSongList(songList);
            }

        int list = mDatabase.delete(SongAndListTable.TABLE_NAME, SongTable.SONG_ID + "= ?", new String[]{Long.toString(id)});
        return song + list;
    }

    public int updateSong(Song song) {
        ContentValues values = getSongContentValues(song);
        int row = mDatabase.update(
                SongTable.TABLE_NAME,
                values,
                SongTable.SONG_ID + "= ?",
                new String[]{Long.toString(song.getSongId())
                });
        return row;
    }


    /**
     * 我是分割线
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * 操作列表的业务
     */
    public long addSonglist(SongList songList) {
        ContentValues values = getSongListContentValues(songList);
        long rowId = mDatabase.insert(SongListTable.TABLE_NAME, null, values);
        return rowId;
    }


    private static ContentValues getSongListContentValues(SongList songList) {
        ContentValues values = new ContentValues();
        values.put(SongListTable.UUID, songList.getId().toString());
        values.put(SongListTable.TITLE, songList.getTitle());
        values.put(SongListTable.SONG_COUNT, songList.getSongCount());
        return values;
    }

    private MusicCursorWrapper querySongLists(String whereClause, String[] args) {
        Cursor cursor = mDatabase.query(SongListTable.TABLE_NAME,
                null,
                whereClause,
                args,
                null,
                null,
                null);
        return new MusicCursorWrapper(cursor);
    }

    public ArrayList<SongList> getSonglists() {
        MusicCursorWrapper cursor = querySongLists(null, null);
        ArrayList<SongList> lists = new ArrayList<>();
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                lists.add(cursor.getSongList());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return lists;
    }

    public int updateSongList(SongList songList) {
        String uuidString = songList.getId().toString();
        ContentValues values = getSongListContentValues(songList);
        return mDatabase.update(SongListTable.TABLE_NAME, values,
                SongListTable.UUID + "= ?",
                new String[]{uuidString});
    }

    /**
     * @param songId
     * @param songList
     */
    public boolean addToList(long songId, SongList songList) {
        ContentValues values = new ContentValues();
        values.put(SongAndListTable.SONG_ID, songId);
        values.put(SongAndListTable.SONGLIST_UUID, songList.getId().toString());
        long rowId = mDatabase.insert(SongAndListTable.TABLE_NAME, null, values);
        if (rowId == -1) {
            return false;
        } else {
            songList.setSongCount(songList.getSongCount() + 1);
            updateSongList(songList);
            return true;
        }
    }

    private SongList getSongListByTitle(String listTitle) {
        MusicCursorWrapper cursor = querySongLists(SongListTable.TITLE + "= ?", new String[]{listTitle});
        try {
            if (cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();
                return cursor.getSongList();
            }
        } finally {
            cursor.close();
        }
    }

    public SongList getSongList(UUID uuid) {
        MusicCursorWrapper cursor = querySongLists(SongListTable.UUID + "= ?", new String[]{uuid.toString()});
        try {
            if (cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();
                return cursor.getSongList();
            }
        } finally {
            cursor.close();
        }
    }


    private MusicCursorWrapper querySongAndList(String whereClause, String[] args) {
        Cursor cursor = mDatabase.query(SongAndListTable.TABLE_NAME,
                null,
                whereClause,
                args,
                null,
                null,
                null);
        return new MusicCursorWrapper(cursor);
    }

    private List<Long> getSongIds(UUID songListId) {
        MusicCursorWrapper cursor = querySongAndList(SongAndListTable.SONGLIST_UUID + "= ?", new String[]{songListId.toString()});
        List<Long> idList = new ArrayList<>();
        try {
            if (cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    idList.add(cursor.getSongAndList().getSongId());
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
        }
        return idList;
    }

    public ArrayList<Song> getSongsOfList(String listTitle) {
        ArrayList<Song> songs = new ArrayList<>();
        SongList songList = getSongListByTitle(listTitle);
        UUID songListId = songList.getId();
        //从中间表获取属于该歌单的所有歌曲id
        List<Long> songIds = getSongIds(songListId);
        if (songIds != null) {
            for (Long id : songIds) {
                songs.add(getSong(id));
            }
        }
        return songs;
    }

    /**
     * 找出所有包含该歌曲id的歌单
     *
     * @param songId
     * @return
     */
    public ArrayList<SongList> getSongListsOfSongId(long songId) {
        MusicCursorWrapper cursor = querySongAndList(SongAndListTable.SONG_ID + "= ?", new String[]{String.valueOf(songId)});
        List<UUID> listIds = new ArrayList<>();
        ArrayList<SongList> songLists = new ArrayList<>();
        try {
            if (cursor.getCount() == 0) {
                LogUtils.logi("找不到找不到");
                return null;
            } else {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    listIds.add(cursor.getSongAndList().getSongListId());
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
        }
        for (UUID listId : listIds) {
            SongList songList = getSongList(listId);
            songLists.add(songList);
        }
        return songLists;
    }


    public boolean deletSongList(UUID id) {
        int i = mDatabase.delete(SongListTable.TABLE_NAME, SongListTable.UUID + "=?",
                new String[]{id.toString()});
        int i1 = mDatabase.delete(SongAndListTable.TABLE_NAME,
                SongAndListTable.SONGLIST_UUID + "=?",
                new String[]{id.toString()});
        if (i + i1 >= 1) {
            return true;
        } else {
            return false;
        }
    }
}
