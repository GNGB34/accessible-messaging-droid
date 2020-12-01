package com.example.accessiblemessaging.data;

import java.util.HashMap;

public class PermissionsWrapper {

    private HashMap<String, Boolean> apps;
    private String language;

    PermissionsWrapper() { }

    public void setApps(HashMap<String, Boolean> newAppList) {
        apps = newAppList;
    }
    public HashMap<String, Boolean> getApps() {
        return apps;
    }

    public void setLanguage(String newLanguage) {
        language = newLanguage;
    }
    public String getLanguage() {
        return language;
    }
}
