package com.mana.accessiblemessaging;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NotificationService extends NotificationListenerService  {


    private static NaturalLanguageService.OUTPUT_STATES state;
    private static final NaturalLanguageService.OUTPUT_STATES on = NaturalLanguageService.OUTPUT_STATES.VOICE;
    private static final NaturalLanguageService.OUTPUT_STATES off = NaturalLanguageService.OUTPUT_STATES.STOP;
    private static final NaturalLanguageService.OUTPUT_STATES dnd = NaturalLanguageService.OUTPUT_STATES.DO_NOT_DISTURB;

    private DatabaseReference db;
    private ArrayList<String> arr; //The list of apps with permissions for; will need to keep this information with a user later
    private HashMap<String, Boolean> appPerm;
    private String language;
    private Setting setting;
    private NaturalLanguageService nls;
    private Context context;
    NotificationWrapper nw;

    @Override
    public void onCreate() {
        super.onCreate(); //Just default oncreate
        db = FirebaseDatabase.getInstance().getReference();
        context = getApplicationContext();
//        arr = new ArrayList<>();
//        arr.add("facebook");
//        arr.add("instagram");
//        arr.add("messaging");
//        arr.add("whatsapp");
        nls = new NaturalLanguageService(on);
        nls.initialize(context);

    }


    //ALlow app to stay open, keep the service running to if randomly stopped by OS
    // Client can stop FUNCTIONALITY but not the service. This function can act as start AND stop for functionality
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        if (intent == null){
            Log.d("STARTT","we start");
            state = on;
            //Keep the service running;
            return START_STICKY;
        }

        //This is if the service is started from the intent, will get the settings needed
        if (intent.getParcelableExtra("SETTING")!= null){
            setting = intent.getParcelableExtra("SETTING");
            language = setting.getLanguage();
            appPerm = setting.getAppPermissions();
            state = on;
            return START_NOT_STICKY;
        } else if ((NaturalLanguageService.OUTPUT_STATES) intent.getSerializableExtra("START") != null && intent.getParcelableExtra("START_SETTING") != null){ //For when started from the MainActivity
            //Check if explicitly want things to stop, if not, will return previous intent (which is active)
            NaturalLanguageService.OUTPUT_STATES flag =(NaturalLanguageService.OUTPUT_STATES) intent.getSerializableExtra("START");
//            setting = intent.getParcelableExtra("START_SETTING");
//            language = setting.getLanguage();

            if (flag == on){
                Log.d("STARTT","we start");
//                nw = new NotificationWrapper("Accessible Messaging", "Announcement", "Hello! Turned on", "0", false);
//                nls.switchLanguage(nls.translateLanguageCode(setting.getLanguage()));
//                nls.detectLanguageCode(nw);
                state = on;
              //  return START_NOT_STICKY;  //Keep the service running;

                //TODO currently, no point in DND unless going to push to db messages in the future, and able to retrieve
                //Means the functionality (i.e read back to him) should stop cause EXPLICITLY stated by client
            }
            //else if (flag == dnd){
//                state = dnd;
//               // return START_NOT_STICKY;  //Keep the service running;
//            }
        }

        else if((NaturalLanguageService.OUTPUT_STATES) intent.getSerializableExtra("START") != null){ //If explictly clicked stop, there will be no settings object
            Log.d("STOP","we stop");
            nw = new NotificationWrapper("Accessible Messaging", "Announcement", "turned off. Goodbye!", "0", false);
            nls.switchLanguage(nls.translateLanguageCode(setting.getLanguage()));
            nls.detectLanguageCode(nw);
            state = off;
            onDestroy();
            //return START_NOT_STICKY;
        }

        else { //System is doing random things, keep it ON
            Log.d("STARTT","we start");
            state = on;
             //Keep the service running;
            return START_REDELIVER_INTENT;
        }
        return START_NOT_STICKY;
    }



    //TODO Not sure why this code does not state connected
    @Override
    public void onListenerConnected (){
        Log.d("CONNECTED", "We connected boyz");
        super.onListenerConnected();
    }

    //Should only disconnect if the service is explicitly stopped; if it's not, it will attempt to rebind
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onListenerDisconnected(){
        if(state != off){
            super.onListenerDisconnected();
            Log.d("DISCONNECTED", "We DISCONNECTED boyz");
            Log.d("NLService", "Notification listener DISCONNECTED from the notification service! Scheduling a reconnect...");
            requestRebind(new ComponentName(this.getPackageName(), this.getClass().getCanonicalName()));
        }
    }



    //When a notification is posted, if the app is set to run, can do needed operations
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void onNotificationPosted (StatusBarNotification sbn){
        //TODO PUT HERE OTHER FUNCTIONS IN IF STATEMENT SUCH AS CLOUD TRANSLATION
        String title;
        String text;
        String appName = null;
        String package_name = sbn.getPackageName();
        boolean app = false;

        //TODO loop thru the user Setting object, retrieve the names of apps
//        for (String s: arr){
//            if (package_name.contains(s)){
//                app = true;
//            }
//        }

        //If SOMEHOW, no setting object passed from MainActivity and SettingsActivity, intialize to default. Nothing will happen, but at least default so no crash
        //Redudency checks
       if (setting == null){
           HashMap<String, Boolean> defaultAppPermission = new HashMap<>();
           defaultAppPermission.put("messenger", true);
           defaultAppPermission.put("messaging", true);
           defaultAppPermission.put("whatsapp", true);
           setting = new Setting(defaultAppPermission, "en");
       }

        Set<String> apps = setting.getAppPermissions().keySet(); //the app names
        for (String s: apps){
            if (package_name.contains(s) && setting.getAppPermissions().get(s) == true){
                app = true;
                appName = s;
            }
        }

        //Need to do these checks in order to 1) Not crash when null on the getNotification, and 2) send proper info to speak
        if (state == on && checkScreen() && app == true) {
            title =  sbn.getNotification().extras.getString("android.title");
            text = sbn.getNotification().extras.getString("android.text");
            Log.d("Notification", title);
            Log.d("Notification:", text);
            Log.d("package name:", package_name + "this is package name");
            String time = Long.toString(System.currentTimeMillis());
            nw = new NotificationWrapper(appName,title,text,time,false);
            //TODO add in NLS service here
            nls.switchLanguage(nls.translateLanguageCode(setting.getLanguage()));
            nls.detectLanguageCode(nw);
            app = false;


        } else if (state == dnd  && app == true){
            //TODO was meant to be done if notifications can be read from firebase back to user via activiating google assistant
        }
    }

        @Override
    public void onDestroy(){
        state = off;
        Log.d("STOP", "stopp");
        super.onDestroy();

    }

    //Screen has to allow for functionality; don't do things while screen is off
    private boolean checkScreen(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean result= Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT_WATCH&&powerManager.isInteractive()|| Build.VERSION.SDK_INT< Build.VERSION_CODES.KITKAT_WATCH&&powerManager.isScreenOn();
        return result;

    }

}
