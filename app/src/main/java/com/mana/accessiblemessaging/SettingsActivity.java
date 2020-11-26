package com.mana.accessiblemessaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    String[] languageOptions={"English","Spanish"};
    Button button;
    DatabaseReference db;
    Context context = getApplicationContext();
    Setting setting;
    private String android_id;
    HashMap <String, Boolean> defaultAppPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
         android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);


        //TODO get the username stuff
        Intent intent = getIntent();
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

                db = FirebaseDatabase.getInstance().getReference(android_id);
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean settingExist = false; // If the permissions are already there
                        for (DataSnapshot snapshot1:dataSnapshot.getChildren()){
                                if (snapshot1.getKey().equals("setting")){
                                    settingExist = true;
                                    setting = (Setting)snapshot1.getValue();
                                }
                            }

                        //checking if the setting object already exsits, and if it does, just change the settings as needed
                        if (settingExist == true){
                            setting.changePermission(app);
                        } else{
                            defaultAppPermission = new HashMap<>();
                            defaultAppPermission.put("messenger", true);
                            defaultAppPermission.put("messaging", true);
                            defaultAppPermission.put("whatsapp", true);
                            setting = new Setting(defaultAppPermission, "en");
                        }

                        db.child("setting").setValue(setting);
                         Intent intent = new Intent(context, NotificationService.class);
                        //intent.putExtra("SETTING", )

                        //startService();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                if (intent.getExtras().getString())
//                db = FirebaseDatabase.getInstance().getReference()
//                if (appName.equals("Whatsapp")){
//
//                }
//
//                startService(context, )
            }
        };

    }
}