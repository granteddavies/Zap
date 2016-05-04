package com.zap.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zap.R;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.zap_sound_long);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    mp.start();
                    sleep(3000);
                    mp.stop();
                    mp.release();
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
