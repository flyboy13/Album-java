package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MusicService extends Service {
    public static boolean boolIsServiceCreated = false;
    MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "SlideShow", Toast.LENGTH_LONG).show();
        Log.e("MusicService", "onCreate");
        boolIsServiceCreated = true;
        player = MediaPlayer.create(
                getApplicationContext(),
                R.raw.kamen_rider_ooo
        );
        player.start();
    }

    @Override
    public void onDestroy() {
        // Toast.makeText(this, "MyService4 Stopped", Toast.LENGTH_LONG).show();
        Log.e("MusicService", "onDestroy");
        player.stop();
        player.release();
        player = null;
    }

    @Override
    public void onStart(Intent intent, int startid) {
        if (player.isPlaying()) {
//            Toast.makeText(
//                    this, "MyService4 Already Started " + startid,
//                    Toast.LENGTH_LONG
//            ).show();
        }
        else {
//        Toast.makeText(
//                this, "MyService4 Started " + startid,
//                Toast.LENGTH_LONG
//        ).show();
        }

        Log.e("MusicService", "onStart");
        player.start();
    }
}