package com.example.musicify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class MainActivity extends AppCompatActivity {

    public EditText song_text;
    private static final String Tag = "MainActivity";
    public ExoPlayer player;
    ConstraintLayout miniPlayer;
    TextView homeSongNameView;
    SeekBar seekBar;
    TextView playpausebtn, skipNextBtn, skipPrevBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(Tag, String.valueOf(AppCompatDelegate.getDefaultNightMode()));
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO)){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
        else{
            fetch_songs();
        }
        player = new ExoPlayer.Builder(this).build();
        homeSongNameView = findViewById(R.id.homeSongNameView);
        miniPlayer = findViewById(R.id.miniPlayer);
        seekBar = findViewById(R.id.seekbar);
        playpausebtn = findViewById(R.id.homePlayPauseBtn);
        skipNextBtn = findViewById(R.id.skipNextBtn);
        skipPrevBtn = findViewById(R.id.skipPrevBtn);
        playwerControls();
    }
    public void playwerControls(){
        homeSongNameView.setSelected(true);
        player.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                //Display title of the song
                assert mediaItem != null;
                Log.i(Tag, "Here I am ");
                Log.i(Tag, (String) mediaItem.mediaMetadata.title);
                homeSongNameView.setText(mediaItem.mediaMetadata.title);
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                seekBar.setProgress((int) player.getCurrentPosition());
                Log.i(Tag, "Duration is" + player.getDuration());
                seekBar.setMax((int) player.getDuration());
                playpausebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0,0,0);
                updatePlayerPositionProgress();
                if(!player.isPlaying()){
                    player.play();
                }

            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == ExoPlayer.STATE_READY){
                    homeSongNameView.setText(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.title);
                    seekBar.setMax((int) player.getDuration());
                    playpausebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0,0,0);
                    updatePlayerPositionProgress();
                }
                else{
                    playpausebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0,0,0);
                }
            }
        });

        miniPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        skipNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.hasNextMediaItem()){
                    player.seekToNext();
                }
            }
        });

        skipPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.hasPreviousMediaItem()){
                    player.seekToPrevious();
                }
            }
        });

        playpausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()){
                    player.pause();
                    playpausebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0,0,0);
                }
                else{
                    player.play();
                    playpausebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0,0,0);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressValue = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player.getPlaybackState() == ExoPlayer.STATE_READY){
                    seekBar.setProgress(progressValue);
                    player.seekTo(progressValue);
                }
            }
        });

    }

    public void updatePlayerPositionProgress(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player.isPlaying()){
                    seekBar.setProgress((int) player.getCurrentPosition());

                }
                updatePlayerPositionProgress();
            }
        }, 1000);
    }

    //Modify this function in the future for Recycler View
    public void fetch_songs(){
        ArrayList<Song> songs = getMusic();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter songAdapter = new MyAdapter(getApplicationContext(), songs, player);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(songAdapter);
        scaleInAnimationAdapter.setDuration(1000);
        scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
        scaleInAnimationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(scaleInAnimationAdapter);
    }

    public ArrayList<Song> getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri songURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songURI, null, null, null,null);
        ArrayList<Song> songs = new ArrayList<Song>();
        if (songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int isSong = songCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                int is_current_music = songCursor.getInt(isSong);
                String path = songCursor.getString(songData);
                int duration = songCursor.getInt(songDuration);
                if (is_current_music == 1){
                    Log.i(Tag, currentTitle + duration);
                    songs.add(new Song(currentTitle, currentArtist, duration, path));
                }

            }while (songCursor.moveToNext());
            songCursor.close();
        }
        return songs;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()){
            player.stop();
        }
        player.release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted! Enjoy Listening", Toast.LENGTH_SHORT).show();
                        fetch_songs();
                    }
                } else {
                    Toast.makeText(this, "No Permission Granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}