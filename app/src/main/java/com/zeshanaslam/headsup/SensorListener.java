package com.zeshanaslam.headsup;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class SensorListener implements SensorEventListener {

    Activity activity;
    SensorManager sensorManager;
    TextView textStatus;

    long lastUpdate;
    boolean currentStatus = true;

    public SensorListener(Activity activity) {
        this.activity = activity;
        textStatus = (TextView) activity.findViewById(R.id.textStatus);

        sensorManager = (SensorManager) activity.getSystemService((Context.SENSOR_SERVICE));
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
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
            if ((actualTime - lastUpdate) > 200) {
                lastUpdate = actualTime;

                // Down
                if (z < -5) {
                    textStatus.setText("Down");
                    return;
                }

                // Up
                if (z > 5) {
                    textStatus.setText("Up");
                    return;
                }

                // Other
                textStatus.setText("Default");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void cancel() {
        currentStatus = false;
    }
}
