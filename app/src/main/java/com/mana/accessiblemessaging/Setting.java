package com.mana.accessiblemessaging;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Setting implements Parcelable {

    public static enum Application{
        WHATSAPP, MESSENGER, MESSAGES
    }


    private String language;
    private HashMap<String, Boolean> appPermissions; //All the app permissions, with the name of the app, followed by a boolean value

    public Setting(){

    }

    public Setting(HashMap<String, Boolean > appPermissions, String language){
        this.language = language;
        this.appPermissions = appPermissions;
    }

    /*
    Parcelable here
     */

    public Setting(Parcel source) {
        this.appPermissions = (HashMap<String, Boolean>) source.readSerializable();
        this.language = source.readString();
    }

    public static final Parcelable.Creator<Setting> CREATOR = new Parcelable.Creator<Setting>() {
        @Override
        public Setting createFromParcel(Parcel source) {
            return new Setting(source);
        }

        @Override
        public Setting[] newArray(int size) {
            return new Setting[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.appPermissions);
        dest.writeString(language);
    }

    /*
    Getters here
     */

    public String getLanguage(){
        return language;
    }

    public HashMap<String, Boolean> getAppPermissions(){
        return appPermissions;
    }

    /*
    Setters here
     */
    public void setLanguage(String language){
        this.language = language;
    }

    public void setAppPermissions(HashMap<String, Boolean> appPermissions){
        this.appPermissions = appPermissions;
    }


    //-------------------Non default implementation--------------
    public void switchLanguage(){
        switch (language){
            case "es":
                language = "en";
                break;
            case "en":
                language = "es";
                break;
        }
    }

    //This will allow for less passing of strings in order to change permission
    public void changePermission(Application app){
        String appName;
        switch (app){
            case MESSENGER:
                appName = "messenger";
                break;
            case MESSAGES:
                appName = "messaging";
                break;
            case WHATSAPP:
                appName = "whatsapp";
                break;
            default:
                appName = "messaging";
        }

        if (appPermissions.get(appName) == true){
            appPermissions.put(appName, false);
        } else{
            appPermissions.put(appName, true);
        }

    }


}
