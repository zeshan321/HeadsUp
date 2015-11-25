package com.zeshanaslam.headsup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private String teamName = null;
    private String objectID = null;
    private boolean gameStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        // Make it fullscreen
        getSupportActionBar().hide();
        updateUI();

        showDialog();

        // Start game
        final TextView textStatus = (TextView) findViewById(R.id.textStatus);
        textStatus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView scoreView = (TextView) findViewById(R.id.textTimer);
                if (!gameStatus && !textStatus.getText().toString().equals(getResources().getString(R.string.loading))) {
                    if (teamName == null) {
                        return;
                    }

                    gameStatus = true;
                    scoreView.setVisibility(View.VISIBLE);

                    SensorListener sensorListener = new SensorListener((Activity) context, objectID);
                    Timer timer = new Timer((Activity) context, sensorListener, 600000, 1000);

                    sensorListener.timer = timer;
                    timer.start();
                }
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.dialog_teamname, null);

        // Get views
        final EditText teamText = (EditText) v.findViewById(R.id.edittext_name);
        final TextView textStatus = (TextView) findViewById(R.id.textStatus);

        builder.setView(v).setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        textStatus.setText(getResources().getString(R.string.loading));
                        teamName = teamText.getText().toString();

                        final ParseObject addTeam = new ParseObject("groups");
                        addTeam.put("groupName", teamName);
                        addTeam.put("score", 0);
                        addTeam.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                textStatus.setText(getResources().getString(R.string.start_text));
                                objectID = addTeam.getObjectId();
                            }
                        });

                        updateUI();
                    }
                }
        );

        builder.create();
        builder.show();
    }

    private void updateUI() {
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOptions);
    }
}
