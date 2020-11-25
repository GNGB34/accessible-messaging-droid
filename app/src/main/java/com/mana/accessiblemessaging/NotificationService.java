package com.mana.accessiblemessaging;
import android.content.ComponentName;
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

public class NotificationService extends NotificationListenerService  {

    static boolean runFlag = false; //Use this to manipulate functions of the service since this service cannot be stopped manually; only the OS can do that
    //Also used in case service gets disconnected by OS without his approval, request a rebind in the onServiceDisconnected()
    private static NaturalLanguageService.OUTPUT_STATES state;
    private static final NaturalLanguageService.OUTPUT_STATES on = NaturalLanguageService.OUTPUT_STATES.VOICE;
    private static final NaturalLanguageService.OUTPUT_STATES off = NaturalLanguageService.OUTPUT_STATES.STOP;
    private static final NaturalLanguageService.OUTPUT_STATES dnd = NaturalLanguageService.OUTPUT_STATES.DO_NOT_DISTURB;

    private DatabaseReference db;
    private ArrayList<String> arr; //The list of apps with permissions for; will need to keep this information with a user later

    @Override
    public void onCreate() {
        super.onCreate(); //Just default oncreate
        db = FirebaseDatabase.getInstance().getReference();
//        arr = new ArrayList<>();
//        arr.add("facebook");
//        arr.add("instagram");
//        arr.add("messaging");
//        arr.add("whatsapp");

        //TODO retrieve from the db the user's permissions, add them to a Setting object
    }


    //ALlow app to stay open, keep the service running to if randomly stopped by OS
    // Client can stop FUNCTIONALITY but not the service. This function can act as start AND stop for functionality
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        //Check if explicitly want things to stop, if not, will return previous intent (which is active)
        NaturalLanguageService.OUTPUT_STATES flag =(NaturalLanguageService.OUTPUT_STATES) intent.getSerializableExtra("START");
        if (flag == on){
            Log.d("STARTT","we start");
            state = on;
            return START_NOT_STICKY;  //Keep the service running;

            //Means the functionality (i.e read back to him) should stop cause EXPLICITLY stated by client
        } else if (flag == dnd){
            state = dnd;
            return START_NOT_STICKY;  //Keep the service running;
        } else {
            Log.d("STOP","we stop");
            state = off;
            //onDestroy();
            return START_NOT_STICKY;
        }
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
        NaturalLanguageService nls;
        NotificationWrapper nw;
        String title;
        String text;
        String package_name = sbn.getPackageName();
        boolean app = false;

        //TODO loop thru the user Permission object, retrieve the names of apps
//        for (String s: arr){
//            if (package_name.contains(s)){
//                app = true;
//            }
//        }

        if (state == on && checkScreen() && app == true){
            title =  sbn.getNotification().extras.getString("android.title");
            text = sbn.getNotification().extras.getString("android.text");

            Log.d("package name:", package_name + "outside of if");

            if (title != null && text != null && package_name != null){
                Log.d("Notification", title);
                Log.d("Notification:", text);
                Log.d("package name:", package_name + "this is package name");
                nw = new NotificationWrapper(package_name,title,text,false);
                //TODO add in NLS service here

            }
        } else if (state == dnd  && app == true){
            //TODO was meant to be done if notifications can be read from firebase back to user via activiating google assistant
        }
    }

//    public void setState(){
//
//
//    }

    //Need to create A FUNCTION THAT WILL READ THE PERMISSIONS SELECTED
    //    @Override
//    public void onDestroy(){
//        runFlag = false;
//        Log.d("STOP", "stopp");
//        super.onDestroy();
//
//    }

    //Screen has to allow for functionality; don't do things while screen is off
    private boolean checkScreen(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean result= Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT_WATCH&&powerManager.isInteractive()|| Build.VERSION.SDK_INT< Build.VERSION_CODES.KITKAT_WATCH&&powerManager.isScreenOn();
        return result;

    }

}
