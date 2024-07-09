package com.example.musicify.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.provider.FontsContractCompat;

import com.example.musicify.Playlists;
import com.example.musicify.Song;

import java.util.ArrayList;

//Song table holds the id of all songs downloaded in the phone
//Playlists table holds the data of the playlists
//Linker table links between the song table and the playlist table
public class MusicDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String DatabseName = "Music.db";
    public static final int DatabaseVersion = 1;
    public static final String TableName = "playlists";
    public static final String ColumnId = "id";
    public static final String Column_Name = "playlist_name";
    public static final String Column_Noofsongs = "no_of_songs";
    public static final String TableSong = "songs";
    public static final String SongColumnID = "id";
    public static final String SongColumnTitle = "title";
    public static final String SongColumnArtist = "artist";
    public static final String SongColumnDuration = "duration";
    public static final String SongColumnPath = "path";
    public static final String TableLinker = "linker";
    public static final String LinkerColumnSongID = "song_id";
    public static final String LinkerColumnPlaylistID = "playlist_id";
    public static final String LinkerColumnOrder = "order_of_songs";

    public MusicDatabaseHelper(@Nullable Context context) {
        super(context, DatabseName, null, DatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE "+ TableName + " (" + ColumnId + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Column_Name + " TEXT, " + Column_Noofsongs + " INTEGER);";
        db.execSQL(query);
        String createSongTableQuery =
                "CREATE TABLE " + TableSong + " (" + SongColumnID + " TEXT PRIMARY KEY, " + SongColumnTitle + " TEXT, " + SongColumnArtist + " TEXT, " + SongColumnDuration + " INTEGER, " + SongColumnPath + " TEXT);";
        db.execSQL(createSongTableQuery);
        String createLinkerTableQuery =
                "CREATE TABLE " + TableLinker + " (" + LinkerColumnSongID + " TEXT, " + LinkerColumnPlaylistID + " INTEGER, " + LinkerColumnOrder + " INTEGER);";
        db.execSQL(createLinkerTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        db.execSQL("DROP TABLE IF EXISTS " + TableSong);
        onCreate(db);
    }

    public boolean isPresent(String tableName, String column_Name, String value){
        String query = "SELECT COUNT(*)" + " FROM " + tableName + " WHERE " + column_Name + " = '" + value + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return (count == 0) ? false : true;
    }

    public boolean verifySong(String id, String title, String artist, Integer duration, String path){
        if (!isPresent(TableSong, SongColumnID, id)){
            //insert into the table
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(SongColumnID, id);
            cv.put(SongColumnTitle, title);
            cv.put(SongColumnArtist, artist);
            cv.put(SongColumnDuration, duration);
            cv.put(SongColumnPath, path);
            long result = db.insert(TableSong, null, cv);
            return result != -1;
        }
        return false;
    }

    public boolean addToPlaylist(String playlist_name, String songID){
        //Fetch the id from the playlist
        String get_id_query = "SELECT " + ColumnId + " FROM " + TableName + " WHERE " + Column_Name + " = '" + playlist_name + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(get_id_query, null);
        int id = -1;
        if (cursor.moveToFirst()){
            id = cursor.getInt(0);
        }
        cursor.close();
        //fetch the number of songs in the playlist
        String fetch_no_songs = "SELECT " + Column_Noofsongs + " FROM " + TableName + " WHERE " + ColumnId + " = " + id;
        Cursor id_cursor = db.rawQuery(fetch_no_songs, null);
        int no_of_songs = -1;
        if (id_cursor.moveToFirst()){
            no_of_songs = id_cursor.getInt(0);
        }
        id_cursor.close();

        no_of_songs++;

        ContentValues cv = new ContentValues();
        cv.put(LinkerColumnSongID, songID);
        cv.put(LinkerColumnPlaylistID, id);
        cv.put(LinkerColumnOrder, no_of_songs);
        long result = db.insert(TableLinker, null, cv);

        //update the number of songs
        ContentValues values = new ContentValues();
        values.put(ColumnId, id);
        values.put(Column_Name, playlist_name);
        values.put(Column_Noofsongs, no_of_songs);
        db.update(TableName, values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result != -1;
    }

    public ArrayList<Song> getSongFromPlaylist(int playlist_id){
        ArrayList<Song> songs = new ArrayList<>();
        String query = "SELECT " + LinkerColumnSongID + " FROM " + TableLinker + " WHERE " + LinkerColumnPlaylistID + " = " + playlist_id + " ORDER BY " + LinkerColumnOrder;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do{
                String id = cursor.getString(0);
                Song song = getSong(id);
                if (song != null){
                    Log.i("TEST", song.toString());
                    songs.add(song);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }

    public Song getSong(String id){
        //Check if the id is present in the datatabase
        boolean is_there = isPresent(TableSong, SongColumnID, id);
        if (is_there){
            String query_string = "SELECT * FROM " + TableSong + " WHERE " + SongColumnID + " = '" + id + "';";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query_string, null);
            if (cursor.moveToFirst()){
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                int duration = cursor.getInt(3);
                String path = cursor.getString(4);
                return new Song(id, title, artist, duration, path);
            }
            cursor.close();
            db.close();
        }
        return null;
    }

    public int addPlaylist(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        boolean isthere = isPresent(TableName, Column_Name, name);
        int id = -1;
        long result = -1;
        if (!isthere){
            cv.put(Column_Name, name);
            cv.put(Column_Noofsongs, 0);
            result = db.insert(TableName, null, cv);
        }
        if (result != -1){
            String query = "SELECT " + ColumnId + " FROM " + TableName + " WHERE " + Column_Name + " = '" + name + "';";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()){
                id = cursor.getInt(0);
            }
            cursor.close();
        }
        return id;
    }

    public boolean delete_playlist(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean referential_integrity = false;
        //remove the link in the linker for maintaining referential integrity
        db.delete(TableLinker, LinkerColumnPlaylistID + " = ? ", new String[]{id});
        //remove the playlist in the playlist table
        db.delete(TableName, ColumnId + " = ?", new String[]{id});
        db.close();
        return referential_integrity;
    }

    public ArrayList<Playlists> getPlaylists(){
        ArrayList<Playlists> returnPlaylists = new ArrayList<>();
        String queryString = "SELECT * FROM " + TableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                returnPlaylists.add(new Playlists(id, name));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return returnPlaylists;
    }

}
