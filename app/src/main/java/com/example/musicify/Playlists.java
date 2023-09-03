package com.example.musicify;

import java.util.ArrayList;

public class Playlists {
    public String name;
    public ArrayList<Song> songs;
    public boolean is_selected;

    public Playlists(String name) {
        this.name = name;
        this.is_selected = false;
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

    public void setIs_selected(boolean val){
        this.is_selected = val;
    }

    public boolean getIs_selected(){
        return this.is_selected;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
