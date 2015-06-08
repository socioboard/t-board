package com.socioboard.t_board_pro.util;

import java.util.Random;

public class SchTweetModel {

	String tweet;

	long tweettime;

	String userID;

	int tweetId;

	ModelUserDatas userDatas;

	public SchTweetModel() {

	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public ModelUserDatas getUserDatas() {
		return userDatas;
	}

	public void setUserDatas(ModelUserDatas userDatas) {

		System.out.println("setUserDatas " + userDatas);
		
		this.userDatas = userDatas;
	}

	public SchTweetModel(String tweet, long tweettime, String userID,
			int tweetId) {
		super();
		this.tweet = tweet;
		this.tweettime = tweettime;
		this.userID = userID;
		this.tweetId = tweetId;
	}

	public SchTweetModel(String userID, String tweet, long tweettime) {

		this.tweet = tweet;
		this.tweettime = tweettime;
		this.userID = userID;
		this.tweetId = new Random().nextInt();

		if (this.tweetId < 0) {

			this.tweetId = -this.tweetId;
		}
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public long getTweettime() {
		return tweettime;
	}

	public void setTweettime(long tweettime) {
		this.tweettime = tweettime;
	}

	public int getTweetId() {
		return tweetId;
	}

	public void setTweetId(int tweetId) {
		this.tweetId = tweetId;
	}

}
