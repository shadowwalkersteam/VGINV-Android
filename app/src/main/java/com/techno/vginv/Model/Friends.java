package com.techno.vginv.Model;

public class Friends {
    private String name;
    private String designation;
    private String profilePicture;
    private String id;
    private String type;

    public Friends(String title, String section, String thumbnail, String friendID, String friendType) {
        name = title;
        designation = section;
        profilePicture = thumbnail;
        id = friendID;
        type = friendType;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
