// ==========================================================================
// NotificationWrapper
// ==========================================================================
// Wraps Content that will be pushed and pulled  to the database
//
// Creator: Gabriel Cordovado (14/11/20)
// Email:   gcord057@uottawa.ca
// ==========================================================================
package com.example.accessiblemessaging;

public class NotificationWrapper {

    private String application, sender, text;
    private boolean read;

    public NotificationWrapper() {
    }

    public NotificationWrapper(String application, String sender, String text, boolean read) {
        this.application = application;
        this.sender = sender;
        this.text = text;
        this.read = read;
    }

    public String getApplication() { return application; }

    public String getSender() { return sender; }

    public String getText() { return text; }

    public boolean isRead() { return read; }

    public void markRead(boolean yesOrNo) {
        this.read = read;
    }
}
