package com.example.musicify;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import kotlin.text.RegexOption;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListHolder> {
    Context context;
    List<Playlists> playlists;
    ExoPlayer player;
    RecyclerView recyclerView;
    MyAdapter songAdapter;
    Boolean onLoad;

    public PlayListAdapter(Context context, List<Playlists> playlists, ExoPlayer player, RecyclerView recyclerView) {
        this.context = context;
        this.playlists = playlists;
        this.player = player;
        this.recyclerView = recyclerView;
        this.songAdapter = null;
        this.onLoad = true;
    }

    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_view, parent, false);
        return new PlayListHolder(view).linkAdapter(this);
//        return new PlayListHolder(LayoutInflater.from(context).inflate(R.layout.playlist_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListHolder holder, int position) {
        holder.playlist_name.setText(playlists.get(position).name);
        holder.playlist_menu_btn.setImageResource(R.drawable.ic_trash);
        Log.i("TEST", String.valueOf(playlists.get(position).getIs_selected()));
        if ( onLoad && playlists.get(position).id == -1 && playlists.get(position).getIs_selected()){
            playlists.get(position).setHolder(holder);
            holder.selected_line.setBackgroundColor(Color.parseColor("#ffffff"));
            fetch_songs(context, player, recyclerView, playlists.get(position).getSongs());
            onLoad = false;
        }
        holder.playlist_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!playlists.get(position).getIs_selected()){
                    reset_rest();
                    Log.i("TEST", "selected is true");
                    holder.selected_line.setBackgroundColor(Color.parseColor("#ffffff"));
                    playlists.get(position).setIs_selected(true);
                    playlists.get(position).setHolder(holder);
                    //get all the songs
                    if (playlists.get(position).id != -1){
                        update_songs(position);
                    }
                    fetch_songs(context, player, recyclerView, playlists.get(position).getSongs());


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
                playlist.getHolder().selected_line.setBackgroundColor(Color.parseColor("#000000"));
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

    public void fetch_songs(Context context, ExoPlayer player, RecyclerView recyclerView, ArrayList<Song> songs){
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        MyAdapter songAdapter = new MyAdapter(context, songs, player, true);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(songAdapter);
        scaleInAnimationAdapter.setDuration(1000);
        scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
        scaleInAnimationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(scaleInAnimationAdapter);
    }


}
