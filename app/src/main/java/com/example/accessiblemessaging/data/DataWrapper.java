package com.example.accessiblemessaging.data;

import com.example.accessiblemessaging.NotificationWrapper;

import java.util.HashMap;

public class DataWrapper {

    private HashMap<String, NotificationWrapper> notifications;
    private PermissionsWrapper permissions;

    public DataWrapper() {}

    public void setNotifications(HashMap<String, NotificationWrapper> newNotificationsBranch) {
        notifications = newNotificationsBranch;
    }
    public HashMap<String, NotificationWrapper> getNotifications() {
        return notifications;
    }

    public void setPermissions(PermissionsWrapper newPermissionsBranch) {
        permissions = newPermissionsBranch;
    }
    public PermissionsWrapper getPermissions() {
        return permissions;
    }
}
