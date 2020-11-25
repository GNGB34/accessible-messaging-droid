package com.mana.accessiblemessaging;

import java.util.HashMap;
import java.util.Locale;

public class Setting {

    public static enum Application{
        INSTAGRAM, FACEBOOK, MESSAGES
    }


    private Locale language;
    private HashMap<String, Boolean> appPermissions; //All the app permissions, with the name of the app, followed by a boolean value

    public Setting(){

    }

    public Setting(HashMap<String, Boolean > appPermissions, String language){
        this.language = ;
        this.appPermissions = appPermissions;
    }

    /*
    Getters here
     */

    public String getLanguage(){
        switch (language){
            case Locale.US:
                "en";
        }
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
            case "Spanish":
                language = "English";
                break;
            case "English":
                language = "Spanish";
                break;
        }
    }


}
