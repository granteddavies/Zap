package com.zap.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zap.R;

public class SplashActivity extends AppCompatActivity {
    // Duration to show the splash screen
    private final static int SPLASH_MILLISECONDS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.zap_sound_long);

        // Display the splash screen for 3 seconds
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    mp.start();
                    sleep(SPLASH_MILLISECONDS);
                    mp.stop();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timerThread.start();
    }
}
