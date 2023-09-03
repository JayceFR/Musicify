package com.example.musicify;

import android.content.Context;
import android.view.LayoutInflater;
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
        holder.playlist_menu_btn.setImageResource(R.drawable.ic_more);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}
