package com.example.steps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.text.*;
import java.util.Objects;

import android.view.MenuItem;
import android.support.annotation.NonNull;

import com.example.steps.R;
import com.example.steps.fragments.betafr;
import com.example.steps.fragments.betafr2;


public class MainActivity extends FragmentActivity implements SensorEventListener{
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_ROST = "Rost";
    public static String APP_PREFERENCES_RAS="RAS";

    SharedPreferences mSettings;
    private SensorManager sensorManager;
    private TextView count;
    private TextView countras;
    private EditText rostschet;
    private boolean activityRunning;

    betafr frb = new betafr();
    betafr2 frb2= new betafr2();
    FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        rostschet = findViewById(R.id.rostschet);
        count = findViewById(R.id.count);
        countras = findViewById(R.id.countras);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.beta1:
                                getSupportFragmentManager().beginTransaction().replace(R.id.frgmCont, frb).commit();
                                break;
                            case R.id.beta2:
                                getSupportFragmentManager().beginTransaction().replace(R.id.frgmCont, frb2).commit();
                                break;
                            case R.id.beta3:
                                if(getSupportFragmentManager().findFragmentById(R.id.frgmCont) != null) {
                                    getSupportFragmentManager().beginTransaction().remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.frgmCont)))
                                            .commit();
                                    break;
                                }

                        }
                        return false;
                    }
                });


        final Handler handler = new Handler();
        if (mSettings.contains(APP_PREFERENCES_ROST)) {
            rostschet.setText(mSettings.getString(APP_PREFERENCES_ROST, ""));
        }
        if (TextUtils.isEmpty(rostschet.getText().toString())) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("oshibka");
            alert.setMessage("Vvedite rost");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    SharedPreferences.Editor editor=mSettings.edit();
                    editor.putString(APP_PREFERENCES_ROST,value);
                    editor.apply();
                    rostschet.setText(mSettings.getString(APP_PREFERENCES_ROST, ""));
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            alert.show();
        }
        else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rasstoyanie();
                    handler.postDelayed(this, 200);
                }
            }, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }
    @Override
    protected void onStop(){
        super.onStop();
        activityRunning=false;
    }
    @Override
    protected void onStart(){
        super.onStart();
        activityRunning=true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(activityRunning) {
            count.setText(String.valueOf(Math.round(event.values[0])));
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onRestart() {

        super.onRestart();
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSave:
                String strNickName = rostschet.getText().toString();
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREFERENCES_ROST, strNickName);
                editor.apply();
                if (mSettings.contains(APP_PREFERENCES_ROST)) {
                    rostschet.setText(mSettings.getString(APP_PREFERENCES_ROST, ""));
                }
                break;
            case R.id.refresh:
                refresh();
                break;
            case R.id.settings_button:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
        }
    }
    public void rasstoyanie(){
        double a;
        String s1=rostschet.getText().toString();
        double c;
        String s2=count.getText().toString();
        SharedPreferences.Editor editor=mSettings.edit();
        a= Double.parseDouble(s1);
        c= Double.parseDouble(s2);
        a=((a/400)+0.37)*c;
        int d=(int)Math.round(a);
        editor.putString(APP_PREFERENCES_RAS,Integer.toString(d));
        editor.apply();
        countras.setText(mSettings.getString(APP_PREFERENCES_RAS,""));
    }
    public void refresh(){
        onRestart();
    }
}
