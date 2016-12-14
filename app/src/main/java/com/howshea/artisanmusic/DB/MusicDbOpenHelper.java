package com.howshea.artisanmusic.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.howshea.artisanmusic.DB.DBSchema.SongAndListTable;
import com.howshea.artisanmusic.DB.DBSchema.SongListTable;
import com.howshea.artisanmusic.DB.DBSchema.SongTable;

/**
 * Created by haipo on 2016/11/6.
 */

public class MusicDbOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String DATABASE_NAME = "music.db";

    public MusicDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table " + SongTable.TABLE_NAME + "(" +
                        "_id integer primary key autoincrement, " +
                        SongTable.SONG_ID + " integer UNIQUE, " +
                        SongTable.TITLE + ", " +
                        SongTable.ARTIST + ", " +
                        SongTable.ALBUM + ", " +
                        SongTable.DURATION + " integer, " +
                        SongTable.URI + ", " +
                        SongTable.COVERURI + ", " +
                        SongTable.FILENAME + ", " +
                        SongTable.FILESIZE + " integer, " +
                        SongTable.YEAR + ", " +
                        SongTable.ISLIKE + " integer)"
        );

        sqLiteDatabase.execSQL("create table " + SongListTable.TABLE_NAME + "(" +
                "_id integer primary key autoincrement, " +
                SongListTable.UUID + ", " +
                SongListTable.TITLE + " UNIQUE, " +
                SongListTable.SONG_COUNT + " integer)");

        sqLiteDatabase.execSQL("create table " + SongAndListTable.TABLE_NAME + "(" +
                "_id integer primary key autoincrement, " +
                SongAndListTable.SONG_ID + " interger, " +
                SongAndListTable.SONGLIST_UUID + ", " +
                "constraint songid_listid unique(" +
                SongAndListTable.SONG_ID + "," +
                SongAndListTable.SONGLIST_UUID + "))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
