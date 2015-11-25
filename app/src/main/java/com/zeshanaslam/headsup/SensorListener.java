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
import android.support.v4.content.ContextCompat;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class SensorListener implements SensorEventListener {

    private WordHandler wordHandler;
    private Activity activity;
    private SensorManager sensorManager;
    private TextView textStatus;
    private long lastUpdate;
    private int lastWord = 0;
    private boolean currentStatus = true;
    private boolean inCheck = true;
    private boolean nextWord = true;
    private String objectID;

    public Timer timer = null;
    public int score = 0;

    public SensorListener(Activity activity, String objectID) {
        this.activity = activity;
        this.objectID = objectID;

        textStatus = (TextView) activity.findViewById(R.id.textStatus);

        sensorManager = (SensorManager) activity.getSystemService((Context.SENSOR_SERVICE));
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        lastUpdate = System.currentTimeMillis();

        wordHandler = new WordHandler(activity);

        // Set first word
        textStatus.setText(wordHandler.getNext());
        nextWord = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!currentStatus) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float[] values = event.values;

            // Movement
            float z = values[2];

            long actualTime = System.currentTimeMillis();
            if ((actualTime - lastUpdate) > 150) {
                lastUpdate = actualTime;

                // Right
                if (z < -6) {
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
                if (z > 6) {
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
                if (nextWord && z > -3 && z < 3) {
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void onCorrect() {
        lastWord = 1;
        score = score + 150;
        updateScore();

        textStatus.setText(activity.getResources().getString(R.string.motion_correct));

        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.right);
        mediaPlayer.start();

        Integer colorFrom = ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimary);
        Integer colorTo = ContextCompat.getColor(activity.getApplicationContext(), R.color.colorRight);

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

    private void onWrong() {
        lastWord = 2;
        score = score - 150;
        updateScore();

        textStatus.setText(activity.getResources().getString(R.string.motion_wrong));

        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.wrong);
        mediaPlayer.start();

        Integer colorFrom = ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimary);
        Integer colorTo = ContextCompat.getColor(activity.getApplicationContext(), R.color.colorWrong);

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

    private void onDefault() {
        if (lastWord == 0) {
            return;
        }

        Integer colorFrom;
        if (lastWord == 1) {
            colorFrom = ContextCompat.getColor(activity.getApplicationContext(), R.color.colorRight);
        } else {
            colorFrom = ContextCompat.getColor(activity.getApplicationContext(), R.color.colorWrong);
        }

        Integer colorTo = ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimary);

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
        Integer colorFrom = ContextCompat.getColor(activity.getApplicationContext(), R.color.colorRight);
        Integer colorTo = ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimary);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.mainLayout);

                relativeLayout.setBackgroundColor((Integer) animator.getAnimatedValue());

                cancel();
                timer.cancel();

                TextView scoreView = (TextView) activity.findViewById(R.id.textTimer);
                scoreView.setText(String.format(activity.getString(R.string.finished_score), score));
                textStatus.setText(activity.getResources().getString(R.string.game_over));
            }

        });
        colorAnimation.start();
    }

    private void updateScore() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("groups");

        query.getInBackground(objectID, new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
                if (e == null) {
                    gameScore.put("score", score);
                    gameScore.saveInBackground();
                }
            }
        });
    }

    public void cancel() {
        currentStatus = false;
        sensorManager.unregisterListener(this);
    }
}
