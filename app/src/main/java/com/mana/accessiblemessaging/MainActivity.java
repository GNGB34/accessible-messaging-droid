package com.mana.accessiblemessaging;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
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
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                Log.i("Main Activity","We have a dynamic link!");
                Uri deepLink = null;
                if (pendingDynamicLinkData!=null){
                    deepLink = pendingDynamicLinkData.getLink();
                }
                if (deepLink!=null){
                    Log.i("MainActivity","Here's the deep link URL:\n"+deepLink.toString());
                    String currentPage = deepLink.getQueryParameter("Settings");
                    int currPage = Integer.parseInt(currentPage);

                }
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
        Intent intent=new Intent(this, SettingsActivity.class);
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

        if (intent.getAction() == intent.ACTION_VIEW) {
           handleDeepLink(intent.getData());
            Log.d("ACTION", "made it to action");
            Log.d("URI", intent.getData().toString());

        }
    }

    private void handleDeepLink(Uri data){

        List<String> arr = data.getPathSegments();
        if (arr.contains("start")){
            Log.d("START_URI", "Able to make it to deep link start");
            startListen();
        } else if (arr.contains("stop")){
            stopListen();
        }else{
            Log.d("NOTHING_ELSE", "the else ");
            for (String s: arr){
                Log.d("PATHS_URI", s);
            }
        }

    }
    private void navigatetoActivity(String featureType) {
        if (featureType == "Settings") {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
        }
    }
    }