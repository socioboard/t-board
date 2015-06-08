package com.socioboard.t_board_pro.util;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class TweetModel {

	Bitmap userimage;

	String tweeet_str;

	String userImagerUrl;

	String userName;

	String fullName;

	String userID;

	String tweetTime;

	String tweetId;

	long favCount;

	long retweetCount;

	boolean isfavourated;

	boolean isRetweeted;

	boolean isFollowing;

	public TweetModel(Bitmap userimage, String tweeet_str,
			String userImagerUrl, String userName, String fullName,
			String userID, String tweetTime, String tweetId, long favCount,
			long retweetCount, boolean isfavourated, boolean isRetweeted,
			boolean isFollowing) {
		super();
		this.userimage = userimage;
		this.tweeet_str = tweeet_str;
		this.userImagerUrl = userImagerUrl;
		this.userName = userName;
		this.fullName = fullName;
		this.userID = userID;
		this.tweetTime = tweetTime;
		this.tweetId = tweetId;
		this.favCount = favCount;
		this.retweetCount = retweetCount;
		this.isfavourated = isfavourated;
		this.isRetweeted = isRetweeted;
		this.isFollowing = isFollowing;
	}

	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getTweetTime() {
		return tweetTime;
	}

	public void setTweetTime(String tweetTime) {
		this.tweetTime = tweetTime;
	}

	public long getFavCount() {
		return favCount;
	}

	public void setFavCount(long favCount) {
		this.favCount = favCount;
	}

	public long getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(long retweetCount) {
		this.retweetCount = retweetCount;
	}

	public boolean isIsfavourated() {
		return isfavourated;
	}

	public void setIsfavourated(boolean isfavourated) {
		this.isfavourated = isfavourated;
	}

	public boolean isRetweeted() {
		return isRetweeted;
	}

	public void setRetweeted(boolean isRetweeted) {
		this.isRetweeted = isRetweeted;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public TweetModel() {
	}

	public String getUserImagerUrl() {
		return userImagerUrl;
	}

	@Override
	public String toString() {
		return "TweetModel [tweeet_str=" + tweeet_str + ", userImagerUrl="
				+ userImagerUrl + ", userName=" + userName + ", fullName="
				+ fullName + ", userID=" + userID + ", tweetTime=" + tweetTime
				+ ", tweetId=" + tweetId + ", favCount=" + favCount
				+ ", retweetCount=" + retweetCount + ", isfavourated="
				+ isfavourated + ", isRetweeted=" + isRetweeted
				+ ", isFollowing=" + isFollowing + "]";
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
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
