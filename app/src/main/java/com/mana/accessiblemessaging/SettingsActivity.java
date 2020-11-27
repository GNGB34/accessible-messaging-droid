package com.mana.accessiblemessaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    private DatabaseReference db;
    private Context context;
    private Setting setting;
    private String android_id;
    private HashMap <String, Boolean> defaultAppPermission;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

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

         whatsappBttn.setOnClickListener(listener);
         fbmessengerBttn.setOnClickListener(listener);
         messagesBttn.setOnClickListener(listener);


    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            button = (Button) v;
            String appName = button.getText().toString().toLowerCase();
            Setting.Application app;
            switch (appName){
                case "messages":
                    app = Setting.Application.MESSAGES;
                    break;
                case "facebook messenger":
                    app = Setting.Application.MESSENGER;
                    break;
                case "whatsapp":
                    app = Setting.Application.WHATSAPP;
                    break;
                default:
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
                    } else{
                        defaultAppPermission = new HashMap<>();
                        defaultAppPermission.put("messenger", true);
                        defaultAppPermission.put("messaging", true);
                        defaultAppPermission.put("whatsapp", true);
                        setting = new Setting(defaultAppPermission, "en");
                    }

                    //Set the value in the db, and pass the information to the service, starting it if need be
                    db.child("setting").setValue(setting);
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