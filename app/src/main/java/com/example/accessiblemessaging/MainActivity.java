package com.example.accessiblemessaging;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        Button stop = (Button) findViewById(R.id.stop);
        Button settings=(Button) findViewById(R.id.settings);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getApplicationContext(), NotificationService.class);
                service.putExtra("START", NaturalLanguageService.OUTPUT_STATES.VOICE);
                startService(service);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getApplicationContext(), NotificationService.class);
                service.putExtra("START", NaturalLanguageService.OUTPUT_STATES.STOP);
                startService(service);
            }
        });

        settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openSettings();

            }
        });
    }

    //Checks if the service is running; it basically always is unless the OS stops it
    private void isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("RUN", "Running");
            } else Log.d("RUN", "not Running");
        }
    }


    public void openSettings(){
        Intent intent=new Intent(this, Settings.class);
        startActivity(intent);
    }


}