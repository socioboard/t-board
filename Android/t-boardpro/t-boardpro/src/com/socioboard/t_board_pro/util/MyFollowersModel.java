package com.socioboard.t_board_pro.util;

import android.graphics.Bitmap;

public class MyFollowersModel {

	Bitmap userimage;

	String userImagerUrl;

	String userName;

	String id;
  	
    boolean followingStatus;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
 	 
	 
	public MyFollowersModel( ) {
		 
	}

 
	@Override
	public String toString() {
		return "MyFollowersModel [userImagerUrl=" + userImagerUrl
				+ ", userName=" + userName + ", id=" + id
				+ ", followingStatus=" + followingStatus + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isFollowingStatus() {
		return followingStatus;
	}

	public void setFollowingStatus(boolean followingStatus) {
		this.followingStatus = followingStatus;
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

}
