package com.example.musicify;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListHolder> {
    Context context;
    List<Playlists> playlists;

    public PlayListAdapter(Context context, List<Playlists> playlists) {
        this.context = context;
        this.playlists = playlists;
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
}
