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
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicify.adapter.PlayListAdapter;
import com.example.musicify.service.SensorService;
import com.example.musicify.util.Sensor;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public EditText song_text;
    private static final String Tag = "MainActivity";
    public ExoPlayer player;
    ConstraintLayout miniPlayer;
    TextView homeSongNameView;
    SeekBar seekBar;
    TextView playpausebtn, skipNextBtn, skipPrevBtn, repeatBtn, shuffleBtn;
    RecyclerView songRecyclerView;
    EditText add_playlist_txtbox;
    TextView show_playlist_btn, add_playlist_btn;
    int repeatMode = 1; //repeat all = 1, repeat one = 2,
    int shuffleMode = 1;
    boolean is_bound = false;
    boolean is_sensor_service_bound = false;
    boolean is_showing = false;
    Sensor sensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Background gradient
        ConstraintLayout constraintLayout = findViewById(R.id.main_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //sensor
        //sensor = new Sensor(MainActivity.this, (SensorManager) getSystemService(Context.SENSOR_SERVICE), (Vibrator) getSystemService(Context.VIBRATOR_SERVICE));

        //player = new ExoPlayer.Builder(this).build();
        homeSongNameView = findViewById(R.id.homeSongNameView);
        miniPlayer = findViewById(R.id.miniPlayer);
        seekBar = findViewById(R.id.seekbar);
        playpausebtn = findViewById(R.id.homePlayPauseBtn);
        skipNextBtn = findViewById(R.id.skipNextBtn);
        skipPrevBtn = findViewById(R.id.skipPrevBtn);
        repeatBtn = findViewById(R.id.repeat_btn);
        shuffleBtn = findViewById(R.id.shuffle_btn);
        songRecyclerView = findViewById(R.id.recyclerView);
        add_playlist_txtbox = findViewById(R.id.add_playlist_textbox);
        show_playlist_btn = findViewById(R.id.show_add_playlist_txtbox);
        add_playlist_btn = findViewById(R.id.add_playlist_btn);

        show_playlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_showing){
                    is_showing = false;
                    add_playlist_txtbox.setVisibility(View.GONE);
                    add_playlist_btn.setVisibility(View.GONE);
                }
                else{
                    is_showing = true;
                    add_playlist_txtbox.setVisibility(View.VISIBLE);
                    add_playlist_btn.setVisibility(View.VISIBLE);
                }
            }
        });


        //playwerControls();
        //bind to the player service
        doBindService();
    }

    public void requestPermission(){
        Log.i(Tag, String.valueOf(AppCompatDelegate.getDefaultNightMode()));
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
                }
                else{
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }
            else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
                }
                else{
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2);
                }
            }
            else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2);
                }
            }
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            initialize();
        }
    }

    public void doBindService(){
        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        bindService(playerServiceIntent, playerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void bindSensor(){
        Intent sensorServiceIntent = new Intent(this, SensorService.class);
        bindService(sensorServiceIntent, sensorServiceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection sensorServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SensorService.SensorBinder binder = (SensorService.SensorBinder) iBinder;
            sensor = binder.getSensor().sensor;
            is_sensor_service_bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(Tag, "Service is disconnected");
        }
    };

    ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //get the service instance
            PlayerService.ServiceBinder binder = (PlayerService.ServiceBinder) iBinder;
            player = binder.getPlayerService().player;
            is_bound = true;
            requestPermission();
            //ready to show the songs
            playwerControls();
            //Bind the sensor
            bindSensor();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(Tag, "Service is disconnected");
        }
    };

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
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying){
                    playpausebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0,0,0);
                }
                else{
                    playpausebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0,0,0);
                }
                Player.Listener.super.onIsPlayingChanged(isPlaying);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == ExoPlayer.STATE_READY){
                    homeSongNameView.setText(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.title);
                    seekBar.setMax((int) player.getDuration());
                    playpausebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0,0,0);
                    updatePlayerPositionProgress();
                }
                else{
                    playpausebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0,0,0);
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

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatMode == 1){
                    player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
                    repeatMode = 2;
                    repeatBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat_on, 0, 0,0);
                }
                else if (repeatMode == 2){
                    player.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
                    repeatMode = 1;
                    repeatBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat_off, 0, 0,0 );
                }
            }
        });

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleMode == 1){
                    player.setShuffleModeEnabled(true);
                    shuffleBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_shuffle_on, 0, 0,0);
                    shuffleMode = 2;
                } else if (shuffleMode == 2) {
                    player.setShuffleModeEnabled(false);
                    shuffleBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_shuffle_off, 0,0,0);
                    shuffleMode = 1;
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

    public void initialize(){
        Playlists all_song_playlist = new Playlists(-1,"All Songs");
        all_song_playlist.setSongs(getMusic());
        all_song_playlist.setIs_selected(true);
        ArrayList<Playlists> playlists = new ArrayList<Playlists>();
        playlists.add(all_song_playlist);
        //To get all the lists from the database
        playlists.addAll(new MusicDatabaseHelper(MainActivity.this).getPlaylists());
        RecyclerView recyclerView = findViewById(R.id.playlist_recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PlayListAdapter playListAdapter = new PlayListAdapter(getApplicationContext(), playlists, player, songRecyclerView);
        recyclerView.setAdapter(playListAdapter);
        //Set the player
        sensor.setPlayer(player);
        add_playlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                playlists.add(new Playlists(add_playlist_txtbox.getText().toString()));
//                Toast.makeText(getApplicationContext(), "Successfully created playlist " + add_playlist_txtbox.getText().toString(), Toast.LENGTH_SHORT).show();
//                playListAdapter.notifyItemInserted(playlists.size()-1);
                MusicDatabaseHelper musicDatabaseHelper = new MusicDatabaseHelper(MainActivity.this);
                int id = musicDatabaseHelper.addPlaylist(add_playlist_txtbox.getText().toString());
                boolean result = false;
                if (id != -1){
                    result = true;
                    playlists.add(new Playlists(id, add_playlist_txtbox.getText().toString()));
                    playListAdapter.notifyItemInserted(playlists.size()-1);
                }
                Toast.makeText(MainActivity.this, "Success: "+ result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayList<Song> getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri songURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songURI, null, null, null,null);
        ArrayList<Song> songs = new ArrayList<Song>();
        MusicDatabaseHelper databaseHelper = new MusicDatabaseHelper(MainActivity.this);
        if (songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int isSong = songCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            int idColumn = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentId = songCursor.getString(idColumn);
                int is_current_music = songCursor.getInt(isSong);
                String path = songCursor.getString(songData);
                int duration = songCursor.getInt(songDuration);
                if (is_current_music == 1){
                    Log.i("TEST", currentTitle + duration + " id:  " + currentId);
                    boolean result = databaseHelper.verifySong(currentId, currentTitle, currentArtist, duration, path);
                    if (result){
                        Toast.makeText(MainActivity.this, "Found New Song" + currentTitle, Toast.LENGTH_SHORT).show();
                    }
                    songs.add(new Song(currentId, currentTitle, currentArtist, duration, path));
                }

            }while (songCursor.moveToNext());
            songCursor.close();
        }
        return songs;
    }


    //Modify this function in the future for Recycler View


    @Override
    protected void onDestroy() {
        doUnbindService();
        stopService(new Intent(this, PlayerService.class));
        super.onDestroy();
//        if (player.isPlaying()){
//            player.stop();
//        }
//        player.release();

    }

    public void doUnbindService(){
        if (is_bound){
            Log.i(Tag, "I am here to clap you ");
            unbindService(playerServiceConnection);
            is_bound = false;
        }
        if (is_sensor_service_bound){
            unbindService(sensorServiceConnection);
            is_sensor_service_bound = false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted For Reading Media", Toast.LENGTH_SHORT).show();
                        initialize();
                    }
                } else {
                    Toast.makeText(this, "No Permission Granted For Reading Media!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission for notificaton granted", Toast.LENGTH_SHORT).show();
                        initialize();
                    }
                    else{
                        Toast.makeText(this, "No permission granted for notifications", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
        }
    }
}