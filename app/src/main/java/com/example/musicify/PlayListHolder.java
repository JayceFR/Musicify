package com.example.musicify;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlayListHolder extends RecyclerView.ViewHolder {

    TextView playlist_name;
    ImageView playlist_menu_btn;
    public PlayListHolder(@NonNull View itemView) {
        super(itemView);
        playlist_name = itemView.findViewById(R.id.playlist_name);
        playlist_menu_btn = itemView.findViewById(R.id.playlist_menu_btn);
    }
}
