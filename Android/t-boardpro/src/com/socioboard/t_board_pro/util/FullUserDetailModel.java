package com.socioboard.t_board_pro.util;


public class FullUserDetailModel {

 
	String tweeet_str;

	String userImagerUrl;
	
	String bannerUrl;

	String noTweets;

	String userName;
	
	String fullName;

 	public FullUserDetailModel( String tweeet_str,
			String userImagerUrl, String bannerUrl, String noTweets,
			String userName, String fullName, String noToFollowing, String id,
			String noFollowers, boolean followingStatus) {
		super();
 		this.tweeet_str = tweeet_str;
		this.userImagerUrl = userImagerUrl;
		this.bannerUrl = bannerUrl;
		this.noTweets = noTweets;
		this.userName = userName;
		this.fullName = fullName;
		this.noToFollowing = noToFollowing;
		this.id = id;
		this.noFollowers = noFollowers;
		this.followingStatus = followingStatus;
	}

	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	String noToFollowing;
	
	String id;

	String noFollowers;

	boolean followingStatus;
  	
 	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FullUserDetailModel() {
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
  
	@Override
	public String toString() {
		return "FullUserDetailModel [tweeet_str=" + tweeet_str
				+ ", userImagerUrl=" + userImagerUrl + ", bannerUrl="
				+ bannerUrl + ", noTweets=" + noTweets + ", userName="
				+ userName + ", fullName=" + fullName + ", noToFollowing="
				+ noToFollowing + ", id=" + id + ", noFollowers=" + noFollowers
				+ ", followingStatus=" + followingStatus + "]";
	}

	 
}
