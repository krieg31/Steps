package com.example.steps;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.text.*;

import com.example.steps.R;


public class MainActivity extends Activity implements SensorEventListener{

    private SensorManager sensorManager;
    private TextView count;
    private TextView countras;
    boolean activityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Date date = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("E yyyy.MM.dd 'и время' hh:mm:ss a zzz");
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
    }
    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
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
