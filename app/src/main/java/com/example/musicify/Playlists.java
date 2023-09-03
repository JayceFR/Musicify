package com.example.musicify;

import java.util.ArrayList;

public class Playlists {
    public String name;
    public ArrayList<Song> songs;

    public Playlists(String name) {
        this.name = name;
    }
    public void addSong(Song song){
        this.songs.add(song);
    }
    public String getStringSongs(){
        StringBuffer songString = new StringBuffer();
        for(Song song: songs){
            songString.append(song.getTitle());
        }
        return songString.toString();
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
