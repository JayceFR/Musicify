package com.example.musicify.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicify.R;
import com.example.musicify.adapter.PlayListAdapter;

public class PlayListHolder extends RecyclerView.ViewHolder {

    TextView playlist_name;
    ImageView playlist_menu_btn;
    View playlist_view;
    LinearLayout selected_line;
    private PlayListAdapter adapter;
    public PlayListHolder(@NonNull View itemView) {
        super(itemView);
        playlist_name = itemView.findViewById(R.id.playlist_name);
        playlist_menu_btn = itemView.findViewById(R.id.playlist_menu_btn);
        playlist_view = itemView.findViewById(R.id.playlist_view);
        selected_line = itemView.findViewById(R.id.selected_line);
    }

    public TextView getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(TextView playlist_name) {
        this.playlist_name = playlist_name;
    }

    public ImageView getPlaylist_menu_btn() {
        return playlist_menu_btn;
    }

    public void setPlaylist_menu_btn(ImageView playlist_menu_btn) {
        this.playlist_menu_btn = playlist_menu_btn;
    }

    public View getPlaylist_view() {
        return playlist_view;
    }

    public void setPlaylist_view(View playlist_view) {
        this.playlist_view = playlist_view;
    }

    public LinearLayout getSelected_line() {
        return selected_line;
    }

    public void setSelected_line(LinearLayout selected_line) {
        this.selected_line = selected_line;
    }

    public PlayListHolder linkAdapter(PlayListAdapter adapter){
        this.adapter = adapter;
        return this;
    }

}
