package com.socioboard.t_board_pro.util;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSingleTon {

	public static boolean signedInStatus = false;

	public static ModelUserDatas currentUserModel;

	public static TwitterModel twitterModel;

	public static HashMap<String, ModelUserDatas> allUserdetails = new HashMap<String, ModelUserDatas>();

	public static ArrayList<ModelUserDatas> modelUserDatasList;

	public static ArrayList<String> allUserIDs = new ArrayList<String>();

	// + + + + + + + + + + URLS + + + + + + + + + + + + + + + TwtBoardPro

	public static String twitterUrls = "";

	public static String keyHash, bearerToken;

	public static final String TWITTER_KEY = "xxxxxxxxxxxxxxxx";

	public static final String TWITTER_SECRET = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

 	public static String reqTokenResourceURL = "https://api.twitter.com/oauth/request_token";

	public static String oauth_callbackURL = "http://www.t-boardpro.com/";
	
	public static String oauthResourceURL = "https://api.twitter.com/oauth/authenticate";
	
	public static String signInRequestURL = "https://api.twitter.com/oauth/authenticate?oauth_token=";
	
	public static String accessTokenPost =  "https://api.twitter.com/oauth/access_token";
	
	public static String userAccountData =  "https://api.twitter.com/1.1/users/show.json";
	
	public static String userTimeLine =  "https://api.twitter.com/1.1/statuses/home_timeline.json";
	
	public static String users_following_to_me =  "https://api.twitter.com/1.1/followers/list.json";
	
	public static String i_am_following_to =  "https://api.twitter.com/1.1/friends/list.json";
	
	public static String followUrl =  "https://api.twitter.com/1.1/friendships/create.json";
	 
	public static String unFollowUrl =  "https://api.twitter.com/1.1/friendships/destroy.json";
	
	public static String updateTweet =  "https://api.twitter.com/1.1/statuses/update.json";
		
}
