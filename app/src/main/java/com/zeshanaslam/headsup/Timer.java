package com.zeshanaslam.headsup;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Timer extends CountDownTimer {

    Activity activity;
    TextView scoreView;
    SensorListener sensorListener;

    public Timer(Activity activity, SensorListener sensorListener, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);

        this.activity = activity;
        this.sensorListener = sensorListener;

        scoreView = (TextView) activity.findViewById(R.id.textTimer);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long minutes = (millisUntilFinished / (1000 * 60)) % 60;
        long seconds = (millisUntilFinished / 1000) % 60;

        scoreView.setText(String.format("%d:%02d", minutes, seconds));
    }

    @Override
    public void onFinish() {
        sensorListener.cancel();

        Integer colorFrom = activity.getResources().getColor(R.color.colorPrimary);
        Integer colorTo = activity.getResources().getColor(R.color.colorWrong);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.mainLayout);
                TextView textStatus = (TextView)activity.findViewById(R.id.textStatus);

                relativeLayout.setBackgroundColor((Integer) animator.getAnimatedValue());

                scoreView.setVisibility(View.INVISIBLE);
                textStatus.setText(activity.getResources().getString(R.string.times_up));
            }

        });
        colorAnimation.start();
    }
}
