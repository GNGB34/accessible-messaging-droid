package com.mana.accessiblemessaging;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mana.accessiblemessaging.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static NaturalLanguageService.OUTPUT_STATES state;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private String android_id;
    private Setting setting;
    private HashMap<String, Boolean> defaultAppPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        mAuth = FirebaseAuth.getInstance(); //GC

        // -------------------- Buttons Connections -------------------------

        Button button = (Button) findViewById(R.id.button);
        Button stop = (Button) findViewById(R.id.stop);
        Button settings=(Button) findViewById(R.id.settings);
        Button login=(Button) findViewById(R.id.loginButton);
        Button logout=(Button) findViewById(R.id.logoutButton);


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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });
        
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {mAuth.getInstance().signOut();}
        });

        handleIntent();

    }

    // -------------------- Utility Functions -------------------------

    //Checks if the service is running; it basically always is unless the OS stops it
    private void isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("RUN", "Running");
            } else Log.d("RUN", "not Running");
        }
    }

    public void updateUI(FirebaseUser user) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        TextView loginOrRegister = (TextView) findViewById(R.id.loginOrRegister);
        loginOrRegister.setText(currentUser.getEmail());
    }

    // -------------------- Director Functions -------------------------

    public void directSettingsOrLogin(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            openLogin();
        }
        else {
            updateUI(currentUser);
            openSettings();
        }
    }

    public void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openSettings(){
        Intent intent=new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // -------------------- Runtime Notification Functions -------------------------

    @Override
    public void onStart() {
        super.onStart();

        Context mainActivityContext = this;

        //we only want the notification to post itself after about 30 seconds after the app has opened up, this gives enough time for the
        //user to edit settings quickly without being inturupted or if longer, the notification to use the MANA database
        Handler popupDelay = new Handler();
        popupDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivityContext);
                    builder.setMessage(R.string.dialog_request)
                            .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    openLogin();
                                }
                            })
                            .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.create();
                    builder.show();
                }
                else {
                    updateUI(currentUser);
                }
            }
        }, 30000); //load it after 30 seconds = 30,000 ms
    }


    public void stopListen(){
        Intent service = new Intent(getApplicationContext(), NotificationService.class);
        service.putExtra("START", NaturalLanguageService.OUTPUT_STATES.STOP);
        startService(service);  //Using the startService, but passing the STOP state to stop functionality
        stopService(service); //Not sure if this works
    }

    public void startListen(){
        Intent service = new Intent(getApplicationContext(), NotificationService.class);
        service.putExtra("START", NaturalLanguageService.OUTPUT_STATES.VOICE);
        db = FirebaseDatabase.getInstance().getReference(mAuth.getUid() + "/" + android_id);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean settingExist = false; // If the permissions are already there
                 setting = null;
                 //Checking in db if there is setting object for this user

                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    if (snapshot1.getKey().equals("setting")){
                        settingExist = true;
                        Log.d("SETTING", settingExist == false? "stiull null": "became true");
                        setting = (Setting)snapshot1.getValue(Setting.class);
                    }
                }

                //if setting does not exist when clicking start, will create it. otherwise, use what was pulled from db
                 if (settingExist == false){
                    defaultAppPermission = new HashMap<>();
                    defaultAppPermission.put("messenger", true);
                    defaultAppPermission.put("messaging", true);
                    defaultAppPermission.put("whatsapp", true);
                    setting = new Setting(defaultAppPermission, "en");
                     db.child("setting").setValue(setting);
                 }
                service.putExtra("START_SETTING", setting);
                startService(service);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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


}