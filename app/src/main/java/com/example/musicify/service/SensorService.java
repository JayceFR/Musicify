package com.example.musicify.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;

import com.example.musicify.util.Sensor;

import java.security.Provider;

public class SensorService extends Service {

    private final IBinder sensorBinder = new SensorBinder();
    public Sensor sensor;

    public class SensorBinder extends Binder{
        public SensorService getSensor(){
            return SensorService.this;
        }
    }

    public SensorService(){
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensor = new Sensor(this, (SensorManager) getSystemService(Context.SENSOR_SERVICE), (Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        sensor.registerListener();
    }

    @Override
    public void onDestroy() {
        sensor.unregisterListener();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sensorBinder;
    }
}
