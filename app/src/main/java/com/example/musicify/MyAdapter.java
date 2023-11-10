package com.example.musicify;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements PopupMenu.OnMenuItemClickListener {
    Context context;
    List<Song> songs;
    ExoPlayer player;

    int current_song_pos = -1;

    public MyAdapter(Context context, List<Song> songs, ExoPlayer player) {
        this.context = context;
        this.songs = songs;
        this.player = player;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.music_view,parent, false));
    }

    public void showPopup(View view, int position){
        PopupMenu popupMenu = new PopupMenu(context, view);
        MusicDatabaseHelper databaseHelper = new MusicDatabaseHelper(context);
        //Adding items to the popup menu.
        ArrayList<Playlists> playlists = databaseHelper.getPlaylists();
        popupMenu.inflate(R.menu.popup_menu);
        Menu menu = popupMenu.getMenu();
        for (int i =0; i < playlists.size(); i++) {
            menu.add(playlists.get(i).name);
        }
        this.current_song_pos = position;
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Toast.makeText(context, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
        //TODO Add the song to the song class if not there
        //TODO Then link the song to the playlist if not present

        return false;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.titleView.setText(songs.get(position).title);
        holder.menu_btn.setImageResource(R.drawable.ic_more);
        byte[] image = getAlbumArt(songs.get(position).getPath());
        if (image != null){
            Glide.with(context).asBitmap()
                    .load(image)
                    .into(holder.imageView);
        }
        else{
            Glide.with(context).
                    load(R.drawable.headphone_pic).
                    into(holder.imageView);
        }
        holder.songView.setOnClickListener(view -> {
            context.startService(new Intent(context.getApplicationContext(), PlayerService.class));
            if (!player.isPlaying()){
                player.setMediaItems(getMediaItems(), position, 0);
            }
            else{
                player.pause();
                player.seekTo(position,0);
            }
            player.prepare();
            player.play();
        });
        holder.menu_btn.setOnClickListener(view -> showPopup(view, position));
//        holder.songView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!player.isPlaying()){
//                    player.setMediaItems(getMediaItems(), position, 0);
//                }
//                else{
//                    player.pause();
//                    player.seekTo(position, 0);
//                }
//                player.prepare();
//                player.play();
//            }
//        });
    }

    public List<MediaItem> getMediaItems(){
        List<MediaItem> mediaItems = new ArrayList<>();
        for (Song song : songs){
            MediaItem mediaItem = new MediaItem.Builder().setUri(song.getPath()).setMediaMetadata(getMetaData(song)).build();
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    public MediaMetadata getMetaData(Song song){
        return new MediaMetadata.Builder().setTitle(song.getTitle()).setArtworkUri(Uri.parse(song.getPath())).build();
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        try {
            retriever.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  art;
    }

}
