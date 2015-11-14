package com.zeshanaslam.headsup;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SensorListener implements SensorEventListener {

    WordHandler wordHandler;

    Activity activity;
    SensorManager sensorManager;
    Timer timer;
    TextView textStatus;

    long lastUpdate;
    int score = 0;
    int lastWord = 0;
    boolean currentStatus = true;
    boolean inCheck = true;
    boolean nextWord = true;

    public SensorListener(Activity activity) {
        this.activity = activity;
        textStatus = (TextView) activity.findViewById(R.id.textStatus);

        sensorManager = (SensorManager) activity.getSystemService((Context.SENSOR_SERVICE));
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        lastUpdate = System.currentTimeMillis();

        wordHandler = new WordHandler(activity);
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!currentStatus) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float[] values = event.values;

            // Movement
            float x = values[0];
            float y = values[1];
            float z = values[2];

            long actualTime = System.currentTimeMillis();
            if ((actualTime - lastUpdate) > 100) {
                lastUpdate = actualTime;

                // Right
                if (z < -5 ) {
                    if (inCheck) {
                        inCheck = false;
                        nextWord = true;
                        onCorrect();
                        return;
                    } else {
                        return;
                    }
                }

                // Wrong
                if (z > 5) {
                    if (inCheck) {
                        inCheck = false;
                        nextWord = true;
                        onWrong();
                        return;
                    } else {
                        return;
                    }
                }

                // Other
                if (nextWord) {
                    inCheck = true;
                    onDefault();
                    if (wordHandler.hasNext()) {
                        textStatus.setText(wordHandler.getNext());
                        nextWord = false;
                    } else {
                        onComplete();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void onCorrect() {
        lastWord = 1;
        score = score + 150;

        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.right);
        mediaPlayer.start();

        Integer colorFrom = activity.getResources().getColor(R.color.colorPrimary);
        Integer colorTo = activity.getResources().getColor(R.color.colorRight);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.mainLayout);

                relativeLayout.setBackgroundColor((Integer) animator.getAnimatedValue());

                textStatus.setText(activity.getResources().getString(R.string.motion_correct));
            }

        });
        colorAnimation.start();
    }

    private void onWrong() {
        lastWord = 2;
        score = score - 150;

        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.wrong);
        mediaPlayer.start();

        Integer colorFrom = activity.getResources().getColor(R.color.colorPrimary);
        Integer colorTo = activity.getResources().getColor(R.color.colorWrong);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.mainLayout);

                relativeLayout.setBackgroundColor((Integer) animator.getAnimatedValue());

                textStatus.setText(activity.getResources().getString(R.string.montion_wrong));
            }

        });
        colorAnimation.start();
    }

    private void onDefault() {
        if (lastWord == 0) {
            return;
        }

        Integer colorFrom = null;
        if (lastWord == 1) {
            colorFrom = activity.getResources().getColor(R.color.colorRight);
        } else {
            colorFrom = activity.getResources().getColor(R.color.colorWrong);
        }

        Integer colorTo = activity.getResources().getColor(R.color.colorPrimary);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.mainLayout);

                relativeLayout.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    private void onComplete() {
        Integer colorFrom = activity.getResources().getColor(R.color.colorRight);
        Integer colorTo = activity.getResources().getColor(R.color.colorPrimary);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.mainLayout);

                relativeLayout.setBackgroundColor((Integer) animator.getAnimatedValue());

                cancel();
                timer.cancel();

                TextView scoreView = (TextView) activity.findViewById(R.id.textTimer);
                scoreView.setText("Score: " + getScore());
                textStatus.setText(activity.getResources().getString(R.string.game_over));
            }

        });
        colorAnimation.start();
    }

    public void cancel() {
        currentStatus = false;
    }

    public int getScore() {
        return score;
    }
}
