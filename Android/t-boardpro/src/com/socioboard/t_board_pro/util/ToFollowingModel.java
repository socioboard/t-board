package com.socioboard.t_board_pro.util;

import android.graphics.Bitmap;

public class ToFollowingModel {

	Bitmap userimage;

	String tweeet_str;

	String userImagerUrl;

	String noTweets;

	String userName;

 	String noToFollowing;
	
	String id;

	String noFollowers;

	boolean followingStatus;
  	
	public ToFollowingModel(Bitmap userimage, String tweeet_str,
			String userImagerUrl, String noTweets, String userName,
			String noToFollowing, String id, String noFollowers,
			boolean followingStatus) {
		super();
		this.userimage = userimage;
		this.tweeet_str = tweeet_str;
		this.userImagerUrl = userImagerUrl;
		this.noTweets = noTweets;
		this.userName = userName;
		this.noToFollowing = noToFollowing;
		this.id = id;
		this.noFollowers = noFollowers;
		this.followingStatus = followingStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ToFollowingModel() {
	}

 	public boolean isFollowingStatus() {
		return followingStatus;
	}

	public void setFollowingStatus(boolean followingStatus) {
		this.followingStatus = followingStatus;
	}

	public String getNoTweets() {
		return noTweets;
	}

	public void setNoTweets(String noTweets) {
		this.noTweets = noTweets;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNoToFollowing() {
		return noToFollowing;
	}

	public void setNoToFollowing(String noToFollowing) {
		this.noToFollowing = noToFollowing;
	}

	public String getNoFollowers() {
		return noFollowers;
	}

	public void setNoFollowers(String noFollowers) {
		this.noFollowers = noFollowers;
	}

	public String getUserImagerUrl() {
		return userImagerUrl;
	}

	public void setUserImagerUrl(String userImagerUrl) {
		this.userImagerUrl = userImagerUrl;
	}

	public Bitmap getUserimage() {
		return userimage;
	}

	public void setUserimage(Bitmap userimage) {
		this.userimage = userimage;
	}

	public String getTweeet_str() {
		return tweeet_str;
	}

	public void setTweeet_str(String tweeet_str) {
		this.tweeet_str = tweeet_str;
	}

	@Override
	public String toString() {
		return "ToFollowingModel [userimage=" +   ", tweeet_str="
				+ tweeet_str + ", userImagerUrl=" + userImagerUrl
				+ ", noTweets=" + noTweets + ", userName=" + userName
				+ ", noToFollowing=" + noToFollowing + ", id=" + id
				+ ", noFollowers=" + noFollowers + ", followingStatus="
				+ followingStatus + "]";
	}

	 
}
