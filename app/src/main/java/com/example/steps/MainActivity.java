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
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.text.*;
import com.example.steps.R;



public class MainActivity extends Activity implements SensorEventListener{
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_ROST = "Rost";
    public static String APP_PREFERENCES_RAS="RAS";

    SharedPreferences mSettings;
    TextView tvInfo;
    private SensorManager sensorManager;
    private TextView count;
    private TextView countras;
    private EditText rostschet;
    private boolean activityRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Date date = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss");

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        rostschet = findViewById(R.id.rostschet);
        count = findViewById(R.id.count);
        countras = findViewById(R.id.countras);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        TextView timenow = findViewById(R.id.timenow);
        timenow.setText("Текущее время " + formatForDateNow.format(date));

        if (mSettings.contains(APP_PREFERENCES_ROST)) {
            rostschet.setText(mSettings.getString(APP_PREFERENCES_ROST, ""));
        }
        final Handler handler = new Handler();
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
            count.setText(String.valueOf(event.values[0]));
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
        editor.putString(APP_PREFERENCES_RAS,Double.toString(a));
        editor.apply();
        countras.setText(mSettings.getString(APP_PREFERENCES_RAS,""));
    }
    public void refresh(){
        onRestart();
    }

}
