package com.example.musicify;

import com.example.musicify.holder.PlayListHolder;

import java.util.ArrayList;

public class Playlists {
    public int id;
    public String name;
    public ArrayList<Song> songs;
    public boolean is_selected;
    public PlayListHolder holder;

    public Playlists(int id, String name) {
        this.id = id;
        this.name = name;
        this.is_selected = false;
        this.songs = new ArrayList<Song>();
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

    public int getId() { return id; }

    public ArrayList<Song> getSongs(){
        return songs;
    }

    public PlayListHolder getHolder(){
        return this.holder;
    }

    public void setHolder(PlayListHolder holder){
        this.holder = holder;
    }

    public boolean getIs_selected(){
        return this.is_selected;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
