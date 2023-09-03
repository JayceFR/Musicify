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

    public PlayListAdapter(Context context, List<Playlists> playlists, ExoPlayer player, RecyclerView recyclerView) {
        this.context = context;
        this.playlists = playlists;
        this.player = player;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayListHolder(LayoutInflater.from(context).inflate(R.layout.playlist_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListHolder holder, int position) {
        holder.playlist_name.setText(playlists.get(position).name);
        holder.playlist_menu_btn.setImageResource(R.drawable.ic_trash);
        Log.i("TEST", String.valueOf(playlists.get(position).getIs_selected()));
        holder.playlist_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playlists.get(position).getIs_selected()){
                    Log.i("TEST", "selected is true");
                    holder.selected_line.setBackgroundColor(Color.parseColor("#ffffff"));
                    playlists.get(position).setIs_selected(true);
                    //get all the songs
                    fetch_songs(context, player, recyclerView, playlists.get(position).getSongs());
                }
                else{
                    holder.selected_line.setBackgroundColor(Color.parseColor("#000000"));
                    playlists.get(position).setIs_selected(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void fetch_songs(Context context, ExoPlayer player, RecyclerView recyclerView, ArrayList<Song> songs){
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        MyAdapter songAdapter = new MyAdapter(context, songs, player);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(songAdapter);
        scaleInAnimationAdapter.setDuration(1000);
        scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
        scaleInAnimationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(scaleInAnimationAdapter);
    }


}
