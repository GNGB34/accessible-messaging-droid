package com.mana.accessiblemessaging;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static NaturalLanguageService.OUTPUT_STATES state;
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
                startListen();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopListen();
            }
        });

        settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openSettings();

            }
        });

        handleIntent();

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


    public void stopListen(){
        Intent service = new Intent(getApplicationContext(), NotificationService.class);
        service.putExtra("START", NaturalLanguageService.OUTPUT_STATES.STOP);
        startService(service);  //Using the startService, but passing the STOP state to stop functionality
    }

    public void startListen(){
        Intent service = new Intent(getApplicationContext(), NotificationService.class);
        service.putExtra("START", NaturalLanguageService.OUTPUT_STATES.VOICE);
        startService(service);
    }

    private void handleIntent(){
        //if the intent matches the ACTION_VIEW (meaning Google requested) then enter it)
        Intent intent = getIntent();

        if (intent.getAction().equals(intent.ACTION_VIEW)) {
           handleDeepLink(intent.getData());
            Log.d("ACTION", "made it to action");
            Log.d("URI", intent.getData().toString());

        }
    }

    private void handleDeepLink(Uri data){

        List<String> arr = data.getPathSegments();
        if (arr.contains("listener") || arr.contains("LISTENER")){
            Log.d("START_URI", "Able to make it to deep link start");
            startListen();
        } else if (arr.contains("removeListen")){
            stopListen();
        }else {
            Log.d("NOTHING_ELSE", "the else ");
            for (String s: arr){
                Log.d("PATHS_URI", s);
            }
        }

    }
}