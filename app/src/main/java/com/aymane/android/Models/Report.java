package com.aymane.android.Models;

import java.util.List;

public class Report {

    private String pid;
    private String displayNameUserPost;
    private String userId;
    private String postDescription;
    private double latitude;
    private double longitude;
    private MapLocation mapLocation;
    private List<String> needs;
    private List<String> ageOfHomeLess;
    private List<String> psychoState;
    private String publishedAt;
    private String profilePic;
    private String profilePicProvider;


    public MapLocation getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(MapLocation mapLocation) {
        this.mapLocation = mapLocation;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDisplayNameUserPost() {
        return displayNameUserPost;
    }

    public void setDisplayNameUserPost(String displayNameUserPost) {
        this.displayNameUserPost = displayNameUserPost;
    }

    public List<String> getAgeOfHomeLess() {
        return ageOfHomeLess;
    }

    public void setAgeOfHomeLess(List<String> ageOfHomeLess) {
        this.ageOfHomeLess = ageOfHomeLess;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public List<String> getNeeds() {
        return needs;
    }

    public void setNeeds(List<String> needs) {
        this.needs = needs;
    }

    public List<String> getPsychoState() {
        return psychoState;
    }

    public void setPsychoState(List<String> psychoState) {
        this.psychoState = psychoState;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getProfilePicProvider() {
        return profilePicProvider;
    }

    public void setProfilePicProvider(String profilePicProvider) {
        this.profilePicProvider = profilePicProvider;
    }
}
