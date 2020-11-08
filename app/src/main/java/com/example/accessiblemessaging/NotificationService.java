package com.example.accessiblemessaging;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends NotificationListenerService  {


    public void startNotificationListener() {
        //start's a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                //fetching notifications from server
                //if there is notifications then call this method
            }
        }).start();
    }

    @Override
    public void onCreate()
    {
        startNotificationListener();
        super.onCreate();
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("STARTT","we start");
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onListenerConnected (){
        Log.d("CONNECTED", "We connected boyz");
        Toast.makeText(getApplicationContext(),"it works", Toast.LENGTH_LONG);

    }
    @Override
    public void onListenerDisconnected(){
        Log.d("DISCONNECTED", "We DISCONNECTED boyz");

    }

    public void onNotificationPosted (StatusBarNotification sbn){

        String title = sbn.getNotification().extras.getString("android.title");
        String text = sbn.getNotification().extras.getString("android.text");
        String package_name = sbn.getPackageName();
        Log.v("Notification title is:", title);
        Log.v("Notification text is:", text);
    }

}
