package com.example.steps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("E yyyy.MM.dd 'и время' hh:mm:ss a zzz");

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        rostschet = (EditText) findViewById(R.id.rostschet);
        tvInfo = (TextView)findViewById(R.id.tvInfo);
        count = (TextView) findViewById(R.id.count);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        TextView timenow = (TextView) findViewById(R.id.timenow);
        timenow.setText("Текущая дата " + formatForDateNow.format(date));

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
        if (mSettings.contains(APP_PREFERENCES_ROST)) {
            tvInfo.setText(mSettings.getString(APP_PREFERENCES_ROST, ""));
           // countras.setText(mSettings.getString(APP_PREFERENCES_RAS,""));
           // rasstoyanie(countras);
        }


    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSave:
                String strNickName = rostschet.getText().toString();
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREFERENCES_ROST, strNickName);
                editor.apply();
                if (mSettings.contains(APP_PREFERENCES_ROST)) {
                    tvInfo.setText(mSettings.getString(APP_PREFERENCES_ROST, ""));
                //    countras.setText(mSettings.getString(APP_PREFERENCES_RAS,""));
                    //rasstoyanie(countras);
                }
                break;
        }
    }
  //  public class MyThread extends Thread {
    //    public void run() {
     //       rasstoyanie();
       // }
    //}
    public void rasstoyanie(TextView qwerty){
        double a;
        String s1=APP_PREFERENCES_ROST;
        double c;
        c= Double.parseDouble(count.getText().toString());
        a= Double.parseDouble(s1);
        a=((a/400)+0.37)*c;
        String b=String.format("%2f",a);
        APP_PREFERENCES_RAS=b;
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
}
