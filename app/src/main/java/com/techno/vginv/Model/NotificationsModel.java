package com.techno.vginv.Model;

public class NotificationsModel {

    private String title;
    private String description;
    private String thumbnail;
    private String notificationType;
    private String senderid;
    private String notificationID;

    public NotificationsModel(String title, String description, String thumbnail, String type, String senderID, String notificationid) {
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.notificationType = type;
        this.senderid = senderID;
        this.notificationID = notificationid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getSenderid() {
        return senderid;
    }

    public String getNotificationID() {
        return notificationID;
    }
}
