package com.example.messengertestlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    private final String TAG = "MyTag";
    Timer timer;
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);


        MotionLayout motionLayout = findViewById(R.id.splashScreenTaxi);
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                progressBar.setProgress(0);

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                Log.d(TAG, "onTransitionChange: " + progress);

                progressBar.setProgress((int) (progress * 100));

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                goToMain(500);

            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

    }

    private void goToMain(int i) {
        if (timer == null)
            timer = new Timer();

        if (timerTask != null)
            timerTask.cancel();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(timerTask, i);
    }
}