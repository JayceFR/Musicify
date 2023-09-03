package com.example.musicify;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlayListHolder extends RecyclerView.ViewHolder {

    TextView playlist_name;
    ImageView playlist_menu_btn;
    View playlist_view;
    LinearLayout selected_line;
    public PlayListHolder(@NonNull View itemView) {
        super(itemView);
        playlist_name = itemView.findViewById(R.id.playlist_name);
        playlist_menu_btn = itemView.findViewById(R.id.playlist_menu_btn);
        playlist_view = itemView.findViewById(R.id.playlist_view);
        selected_line = itemView.findViewById(R.id.selected_line);
    }
}
