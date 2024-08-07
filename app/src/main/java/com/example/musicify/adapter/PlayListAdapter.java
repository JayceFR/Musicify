package com.example.musicify.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicify.interfaces.AdapterInterface;
import com.example.musicify.util.MusicDatabaseHelper;
import com.example.musicify.holder.PlayListHolder;
import com.example.musicify.Playlists;
import com.example.musicify.R;
import com.example.musicify.Song;
import com.example.musicify.interfaces.PlaylistInterface;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListHolder> implements AdapterInterface {
    private final PlaylistInterface playlistInterface;
    Context context;
    List<Playlists> playlists;
    ExoPlayer player;
    RecyclerView recyclerView;
    MyAdapter songAdapter;
    Boolean onLoad;
    int curr_playlist_pos = -1;

    public PlayListAdapter(Context context, List<Playlists> playlists, ExoPlayer player, RecyclerView recyclerView, PlaylistInterface playlistInterface) {
        this.context = context;
        this.playlists = playlists;
        this.player = player;
        this.recyclerView = recyclerView;
        this.songAdapter = null;
        this.onLoad = true;
        this.playlistInterface = playlistInterface;
    }

    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_view, parent, false);
        return new PlayListHolder(view, playlistInterface).linkAdapter(this);
//        return new PlayListHolder(LayoutInflater.from(context).inflate(R.layout.playlist_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListHolder holder, int position) {
        holder.getPlaylist_name().setText(playlists.get(position).name);
        holder.getPlaylist_menu_btn().setImageResource(R.drawable.ic_trash);
        Log.i("TEST", String.valueOf(playlists.get(position).getIs_selected()));
        if ( onLoad && playlists.get(position).id == -1 && playlists.get(position).getIs_selected()){
            playlists.get(position).setHolder(holder);
            holder.getSelected_line().setBackgroundColor(Color.parseColor("#ffffff"));
            fetch_songs(context, player, recyclerView, playlists.get(position).getSongs(), position);
            onLoad = false;
        }
        holder.getPlaylist_view().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!playlists.get(position).getIs_selected()){
                    reset_rest();
                    Log.i("TEST", "selected is true");
                    holder.getSelected_line().setBackgroundColor(Color.parseColor("#ffffff"));
                    playlists.get(position).setIs_selected(true);
                    playlists.get(position).setHolder(holder);
                    //get all the songs
                    if (playlists.get(position).id != -1){
                        update_songs(position);
                    }
                    curr_playlist_pos = position;
                    fetch_songs(context, player, recyclerView, playlists.get(position).getSongs(), position);


                }
//                else{
//                    holder.selected_line.setBackgroundColor(Color.parseColor("#000000"));
//                    playlists.get(position).setIs_selected(false);
//                }
            }
        });
    }

    public void reset_rest(){
        for (Playlists playlist : playlists){
            if(playlist.getIs_selected()){
                playlist.setIs_selected(false);
                playlist.getHolder().getSelected_line().setBackgroundColor(Color.parseColor("#00ff0000"));
                playlist.setHolder(null);
            }
        }
    }


    public void update_songs(int position){
        MusicDatabaseHelper databaseHelper = new MusicDatabaseHelper(this.context);
        ArrayList<Song> songs = databaseHelper.getSongFromPlaylist(playlists.get(position).id);
        Log.i("TEST", songs.toString());
        playlists.get(position).setSongs(songs);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void fetch_songs(Context context, ExoPlayer player, RecyclerView recyclerView, ArrayList<Song> songs, int position){
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        songAdapter = new MyAdapter(context, songs, player, true, playlists.get(position), this);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(songAdapter);
        scaleInAnimationAdapter.setDuration(400);
        scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
        scaleInAnimationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(scaleInAnimationAdapter);
    }

    @Override
    public void onDustBinClicked(int position, int curr_playlist_id) {
        MusicDatabaseHelper db = new MusicDatabaseHelper(context);
        db.delete_song(playlists.get(curr_playlist_pos).getSongs().get(position).id, String.valueOf(curr_playlist_id));
        playlists.get(curr_playlist_pos).getSongs().remove(position);
        songAdapter.notifyItemRemoved(position);
    }
}
