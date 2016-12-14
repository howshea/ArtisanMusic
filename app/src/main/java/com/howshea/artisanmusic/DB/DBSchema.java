package com.howshea.artisanmusic.DB;

/**
 * PackageName: com.haipo.artisanmusic.DB
 * FileName：   DBSchema
 * Created by haipo on 2016/11/6.
 */

public class DBSchema {
    public static final class SongTable{
        public static final String TABLE_NAME= "song";
        public static final String SONG_ID = "song_id";
        public static final String TITLE = "title";
        public static final String ARTIST = "artist";
        public static final String ALBUM = "album";
        public static final String DURATION = "duration";
        public static final String URI = "uri";
        public static final String COVERURI = "cover_uri";
        public static final String FILENAME = "file_name";
        public static final String FILESIZE = "file_size";
        public static final String YEAR = "year";
        public static final String ISLIKE = "islike";
    }

    public static final class SongListTable{
        public static final String TABLE_NAME = "songlist";
        public static final String UUID = "songlist_id";
        public static final String TITLE = "title";
        public static final String SONG_COUNT = "song_count";
    }

    public static final class SongAndListTable{
        public static final String TABLE_NAME = "song_songlist";
        public static final String SONG_ID = "song_id";
        public static final String SONGLIST_UUID = "songlist_id";
    }
}
