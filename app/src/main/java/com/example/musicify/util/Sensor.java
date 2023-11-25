package com.example.musicify.util;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.widget.Toast;

import com.bumptech.glide.load.data.ExifOrientationStream;
import com.example.musicify.MainActivity;
import com.google.android.exoplayer2.ExoPlayer;

//accelerometer
public class Sensor implements SensorEventListener {
    private SensorManager sensorManager;
    private android.hardware.Sensor sensor;
    private Boolean isAvailable, isNotFirstTime = false;
    private float currentX, currentY, currentZ, lastX, lastY, lastZ;
    private float xDifference, yDifference, zDifference;
    private float shakeThreshold = 20f;
    private Vibrator vibrator;
    private Context context;
    private ExoPlayer player;
    public Sensor(Context context, SensorManager sM, Vibrator vb){
        this.sensorManager = sM;
        this.vibrator = vb;
        this.context = context;
        if (sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER) != null){
            sensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
            isAvailable = true;
        }
        else{
            Toast.makeText(context, "Accelerometer not available", Toast.LENGTH_SHORT).show();
            isAvailable = false;
        }
    }

    public void registerListener(){
        if (isAvailable)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener(){
        if (isAvailable)
            sensorManager.unregisterListener(this);
    }

    public void setPlayer(ExoPlayer player){
        this.player = player;
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentX = sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        if (isNotFirstTime){
            xDifference = Math.abs(lastX - currentX);
            yDifference = Math.abs(lastY - currentY);
            zDifference = Math.abs(lastZ - currentZ);

            if ((xDifference > shakeThreshold && yDifference > shakeThreshold) || (xDifference > shakeThreshold && zDifference > shakeThreshold) || (yDifference > shakeThreshold && xDifference > shakeThreshold)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                }
                else{
                    vibrator.vibrate(500);
                }
                if (player.isPlaying()){
                    if (player.hasNextMediaItem()){
                        player.seekToNext();
                    }
                }
            }

        }

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;

        isNotFirstTime = true;
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }


}
