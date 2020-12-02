package com.mana.accessiblemessaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    String[] languageOptions={"English","Spanish"};
    private Button button; //for the listener object
    private Button whatsappBttn;
    private Button messagesBttn;
    private Button fbmessengerBttn;
    private Button languageBttn;
    private DatabaseReference db;
    private Context context;
    private Setting setting;
    private String android_id;
    private HashMap <String, Boolean> defaultAppPermission;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private NaturalLanguageService nls; //to make announcements
    private NotificationWrapper nw;     //to make announcements, we use this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        context =  getApplicationContext();
        android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
         whatsappBttn = (Button) findViewById(R.id.whatsappButton);
         fbmessengerBttn = (Button) findViewById(R.id.facebookButton);
         messagesBttn = (Button) findViewById(R.id.messagesButton);
        languageBttn = (Button) findViewById(R.id.changeLanguageButton);

         whatsappBttn.setOnClickListener(listener);
         fbmessengerBttn.setOnClickListener(listener);
         messagesBttn.setOnClickListener(listener);
        languageBttn.setOnClickListener(langListener);
        nls = new NaturalLanguageService(NaturalLanguageService.OUTPUT_STATES.VOICE);
        nls.initialize(context);

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            button = (Button) v;
            String appName = button.getText().toString().toLowerCase();
            Setting.Application app;
            String announcement = "Announcement";
            final String appString;    //The name of the apps as used in this Notification system; DIFFERENT FROM THE BUTTON
            switch (appName){
                case "messages":
                    app = Setting.Application.MESSAGES;
                    appString = "messaging";
                    break;
                case "facebook messenger":
                    app = Setting.Application.MESSENGER;
                    appString = "messenger";
                    break;
                case "whatsapp":
                    app = Setting.Application.WHATSAPP;
                    appString =  "whatsapp";
                    break;
                default:
                    appString = "messaging";
                    app = Setting.Application.MESSAGES;
            }

            db = FirebaseDatabase.getInstance().getReference(mAuth.getUid() + "/" + android_id);
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean settingExist = false; // If the permissions are already there

                    Log.d("CHECK", "made it");
                    for (DataSnapshot snapshot1:dataSnapshot.getChildren()){
                        if (snapshot1.getKey().equals("setting")){
                            settingExist = true;
                            setting = (Setting)snapshot1.getValue(Setting.class);
                        }
                    }

                    //checking if the setting object already exsits in db, and if it does, just change the settings as needed
                    //TODO GABE NEEDS TO CREATE THIS DEFAULT when registering
                    if (settingExist == true){
                        setting.changePermission(app);
                        db.child("setting").setValue(setting);
                    } else{
                        defaultAppPermission = new HashMap<>();
                        defaultAppPermission.put("messenger", true);
                        defaultAppPermission.put("messaging", true);
                        defaultAppPermission.put("whatsapp", true);
                        setting = new Setting(defaultAppPermission, "en");
                        setting.changePermission(app);
                    }

                    Log.d("APPNAME", appString);
                    if (setting.getAppPermissions().get(appString) == true){
                        nw = new NotificationWrapper(appName, "Announcement", "turned on", "0", false);
                    } else{
                        nw = new NotificationWrapper(appName, "Announcement", "turned off", "0", false);
                    }

                    nls.switchLanguage(nls.translateLanguageCode(setting.getLanguage()));
                    nls.detectLanguageCode(nw);

                    //Set the value in the db, and pass the information to the service, starting it if need be
                    Intent intent = new Intent(context, NotificationService.class);
                    intent.putExtra("SETTING", setting);
                    Log.d("SERVICE_START", "starging service");
                    startService(intent);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    };


    View.OnClickListener langListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            db = FirebaseDatabase.getInstance().getReference(mAuth.getUid() + "/" + android_id);
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean settingExist = false; // If the permissions are already there

                    Log.d("CHECK", "made it");
                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                        if (snapshot1.getKey().equals("setting")) {
                            settingExist = true;
                            setting = snapshot1.getValue(Setting.class);
                        }
                    }

                    //checking if the setting object already exsits in db, and if it does, just change the settings as needed
                    //Push the change to firebase
                    //TODO GABE NEEDS TO CREATE THIS DEFAULT when registering
                    if (settingExist == true) {
                        setting.switchLanguage();
                        db.child("setting").setValue(setting);
                    } else {
                        defaultAppPermission = new HashMap<>();
                        defaultAppPermission.put("messenger", true);
                        defaultAppPermission.put("messaging", true);
                        defaultAppPermission.put("whatsapp", true);
                        setting = new Setting(defaultAppPermission, "en");
                        setting.switchLanguage();
                    }

                    if (setting.getLanguage().equals("es")){
                        nw = new NotificationWrapper("language", "Announcement", "Now Spanish", "0", false);
                    } else {
                        nw = new NotificationWrapper("language", "Announcement", "Now English", "0", false);
                    }

                    nls.switchLanguage(nls.translateLanguageCode(setting.getLanguage()));
                    nls.detectLanguageCode(nw);
                    Toast.makeText(context, "changed settings", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, NotificationService.class);
                    intent.putExtra("SETTING", setting);
                    Log.d("SERVICE_START", "starging service");
                    startService(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };
}