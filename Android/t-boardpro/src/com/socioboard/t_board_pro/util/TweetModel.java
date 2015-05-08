package com.socioboard.t_board_pro.util;

import android.graphics.Bitmap;

public class TweetModel {

	Bitmap userimage;
	
	String tweeet_str;

	String userImagerUrl;
	
	String userName;
 	
  	 

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public TweetModel( ) { 
	}
	
	public TweetModel(Bitmap userimage, String tweeet_str,
			String userImagerUrl, String userName) {
		super();
		this.userimage = userimage;
		this.tweeet_str = tweeet_str;
		this.userImagerUrl = userImagerUrl;
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "TweetModel [userimage=" +  ", tweeet_str="
				+ tweeet_str + ", userImagerUrl=" + userImagerUrl
				+ ", userName=" + userName + "]";
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
	
	
 }
