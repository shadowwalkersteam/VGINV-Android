package com.techno.vginv.Model;

public class Inbox {

    private String name;
    private String message;
    private String profilePicture;

    public Inbox(String title, String section, String thumbnail) {
        name = title;
        message = section;
        profilePicture = thumbnail;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

}
