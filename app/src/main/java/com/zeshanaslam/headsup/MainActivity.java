package com.zeshanaslam.headsup;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Context context;
    boolean gameStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        // Make it fullscreen
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOptions);

        // Start game
        final TextView textStatus = (TextView) findViewById(R.id.textStatus);
        textStatus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView scoreView = (TextView) findViewById(R.id.textTimer);
                if (!gameStatus) {
                    gameStatus = true;
                    scoreView.setVisibility(View.VISIBLE);

                    SensorListener sensorListener = new SensorListener((Activity) context);
                    Timer timer =  new Timer((Activity) context, sensorListener, 600000, 1000);

                    sensorListener.setTimer(timer);
                    timer.start();
                }
            }
        });
    }
}
