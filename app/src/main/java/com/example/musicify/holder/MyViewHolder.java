package com.example.musicify.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicify.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView, menu_btn;
    TextView titleView;
    View songView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageview);
        titleView = itemView.findViewById(R.id.title);
        songView = itemView.findViewById(R.id.songView);
        menu_btn = itemView.findViewById(R.id.menu_btn);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public ImageView getMenu_btn() {
        return menu_btn;
    }

    public void setMenu_btn(ImageView menu_btn) {
        this.menu_btn = menu_btn;
    }

    public TextView getTitleView() {
        return titleView;
    }

    public void setTitleView(TextView titleView) {
        this.titleView = titleView;
    }

    public View getSongView() {
        return songView;
    }

    public void setSongView(View songView) {
        this.songView = songView;
    }
}
