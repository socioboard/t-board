package com.socioboard.t_board_pro;


public class FirebaseModel
{

    String getTwitterAccessToken;
    String getUserId;
    String getFirebaseToken;
    String deviceId;
    long followingCount;
    long myfollowersCount;
    String userFullName;


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(long followingCount) {
        this.followingCount = followingCount;
    }

    public long getMyfollowersCount() {
        return myfollowersCount;
    }

    public void setMyfollowersCount(long myfollowersCount) {
        this.myfollowersCount = myfollowersCount;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    String userName;
    boolean alertOk;

    public boolean isAlertOk() {
        return alertOk;
    }

    public void setAlertOk(boolean alertOk) {
        this.alertOk = alertOk;
    }


    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }


    public String getGetTwitterAccessToken() {
        return getTwitterAccessToken;
    }

    public void setGetTwitterAccessToken(String getTwitterAccessToken) {
        this.getTwitterAccessToken = getTwitterAccessToken;
    }

    public String getGetUserId() {
        return getUserId;
    }

    public void setGetUserId(String getUserId) {
        this.getUserId = getUserId;
    }

    public String getGetFirebaseToken() {
        return getFirebaseToken;
    }

    public void setGetFirebaseToken(String getFirebaseToken) {
        this.getFirebaseToken = getFirebaseToken;
    }



    FirebaseModel(String getTwitterAccessToken, String getUserId, String getFirebaseToken, String deviceId, Long followingCount, Long myfollowersCount, String userFullName,String userName, boolean alertok)
    {
        this.getTwitterAccessToken = getTwitterAccessToken;
        this.getUserId = getUserId;
        this.getFirebaseToken = getFirebaseToken;
        this.deviceId = deviceId;
        this.followingCount = followingCount;
        this.myfollowersCount = myfollowersCount;
        this.userFullName = userFullName;
        this.userName = userName;
        this.alertOk = alertok;
    }
}
