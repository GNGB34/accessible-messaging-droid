package com.mana.accessiblemessaging;

import java.util.HashMap;

public class Setting {

    public static enum Application{
        WHATSAPP, FACEBOOK, MESSAGES
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

   // public void addPermission()

    public void changePermission(Application app, boolean val){
        String appName;
        switch (app){
            case FACEBOOK:
                appName = "facebook";
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

        appPermissions.put(appName, val);

    }


}
