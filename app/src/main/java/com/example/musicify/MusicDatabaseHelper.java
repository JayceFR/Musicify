package com.example.musicify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.provider.FontsContractCompat;

import java.util.ArrayList;

public class MusicDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String DatabseName = "Music.db";
    public static final int DatabaseVersion = 1;
    public static final String TableName = "playlists";
    public static final String ColumnId = "id";
    public static final String Column_Name = "playlist_name";
    public static final String Column_Noofsongs = "no_of_songs";
    public MusicDatabaseHelper(@Nullable Context context) {
        super(context, DatabseName, null, DatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE "+ TableName + " (" + ColumnId + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Column_Name + " TEXT, " + Column_Noofsongs + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        onCreate(db);
    }

    public boolean addPlaylist(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        //TODO need to perform a check to see whether there exists a playlist with the same name stored in the database
        cv.put(Column_Name, name);
        cv.put(Column_Noofsongs, 0);
        long result = db.insert(TableName, null, cv);
        return result != -1;
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
        return returnPlaylists;
    }

}
