package com.socioboard.t_board_pro.util;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;

public class MainSingleTon {

	public static long myfollowersCount = -1;

	public static long followingCount = -1;

	public static long tweetsCount = -1;

	public static long favoritesCount = -1;

	public static long notificationInterval = 3600000;

	public static long recentsFollowersCount = -1;

	public static boolean signedInStatus = false;

	public static boolean primaryCountLoaded = false;

	public static boolean isNeedTOstopFollowing = false;

	public static boolean isNeedTOstopFollowersIdRequest = false;

	public static boolean isNeedTOstopGetRequest = false;

	public static boolean secondaryCountLoaded = false;

	public static boolean autodm = false;

	public static String autoDmfirstime;

	public static ModelUserDatas currentUserModel = new ModelUserDatas();

	public static ConnectionDetector connectionDetector;

	public static ArrayList<TweetModel> loadedtweets = new ArrayList<TweetModel>();

	public static ArrayList<String> listMyfollowersIDs = new ArrayList<String>();

	public static ArrayList<ToFollowingModel> toFollowingModels = new ArrayList<ToFollowingModel>();

	public static ArrayList<String> toFollowingModelsIDs = new ArrayList<String>();

	public static ArrayList<ToFollowingModel> myFollowers = new ArrayList<ToFollowingModel>();

	public static ArrayList<String> fansIds = new ArrayList<String>();

	public static ArrayList<String> nonFollowersIds = new ArrayList<String>();

	public static ArrayList<String> mutualsIds = new ArrayList<String>();

	public static String followingNextCursor = "-1";

	public static String myfollowersNextCursor = "-1";

	public static String searchPage = "";

	public static ModelUserDatas SelectedUser;

	public static SearchDetailModel searchDetailModel = new SearchDetailModel();

	public static FullUserDetailModel fullUserDetailModel;

	public static Bitmap bitmapBanner;

	public static HashMap<String, ModelUserDatas> allUserdetails = new HashMap<String, ModelUserDatas>();

	public static ArrayList<ModelUserDatas> modelUserDatasList;

	public static ArrayList<String> allUserIDs = new ArrayList<String>();

	// + + + + + + + + + +

	public static String insertedText = "";

	public static String in_reply_to_status_id = "";

	public static String retweet_to_status_id = "";

	// + + + + + + + + + + URLS + + + + + + + + + + + + + + + TwtBoardPro

	public static String twitterUrls = "";

	public static String isTwitterKeyAssigned = "isTwitterKeyAssigned";

	public static String keyHash, bearerToken;

	public static String TWITTER_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxx";

	public static String TWITTER_SECRET = "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy";

	public static String oauth_callbackURL = "http://www.twtboardpro.com/";

	public static String reqTokenResourceURL = "https://api.twitter.com/oauth/request_token";

	public static String T_KEY = "T_KEY";

	public static String T_SECRET = "T_SECRET";

	public static String T_oauth_callbackURL = "T_oauth_callbackURL";

	public static String oauthResourceURL = "https://api.twitter.com/oauth/authenticate";

	public static String signInRequestURL = "https://api.twitter.com/oauth/authenticate?oauth_token=";

	public static String accessTokenPost = "https://api.twitter.com/oauth/access_token";

	public static String userAccountData = "https://api.twitter.com/1.1/users/show.json";

	public static String userShowIds = "https://api.twitter.com/1.1/users/lookup.json";

	public static String userTimeLine = "https://api.twitter.com/1.1/statuses/home_timeline.json";

	public static String users_following_to_me = "https://api.twitter.com/1.1/followers/list.json";

	public static String users_following_to_me_Ids = "https://api.twitter.com/1.1/followers/ids.json";

	public static String i_am_following_to = "https://api.twitter.com/1.1/friends/list.json";

	public static String i_am_following_to_ids = "https://api.twitter.com/1.1/friends/ids.json";

	public static String followUrl = "https://api.twitter.com/1.1/friendships/create.json";

	public static String unFollowUrl = "https://api.twitter.com/1.1/friendships/destroy.json";

	public static String updateTweet = "https://api.twitter.com/1.1/statuses/update.json";

	public static String userSearch = "https://api.twitter.com/1.1/users/search.json";

	public static String twtFavourites = "https://api.twitter.com/1.1/favorites/list.json";

	public static String reTweeting = "https://api.twitter.com/1.1/statuses/retweet/";

	public static String favouritesCreate = "https://api.twitter.com/1.1/favorites/create.json";

	public static String favouritesDestroy = "https://api.twitter.com/1.1/favorites/destroy.json";

	public static String tweetsSearch = "https://api.twitter.com/1.1/search/tweets.json";

	public static String update_with_media = "https://api.twitter.com/1.1/statuses/update_with_media.json";

	public static String uploadMedia = "https://upload.twitter.com/1.1/media/upload.json";

	public static String createMessage = "https://api.twitter.com/1.1/direct_messages/new.json";

	public static String mentions_timeline = "https://api.twitter.com/1.1/statuses/mentions_timeline.json";

	public static String user_timeline = "https://api.twitter.com/1.1/statuses/user_timeline.json";

	public static String direct_messages = "https://api.twitter.com/1.1/direct_messages/new.json";

	public static String broadcataction = "com.socioboard.action";

	public static void resetSigleTon() {

		fullUserDetailModel = new FullUserDetailModel();

		loadedtweets.clear();

		listMyfollowersIDs.clear();

		toFollowingModels.clear();

		toFollowingModelsIDs.clear();

		myFollowers.clear();

		fansIds.clear();

		nonFollowersIds.clear();

		mutualsIds.clear();

		myfollowersCount = -1;

		followingCount = -1;

		tweetsCount = -1;

		favoritesCount = -1;

		searchDetailModel = new SearchDetailModel();

		followingNextCursor = "-1";

		myfollowersNextCursor = "-1";

		primaryCountLoaded = false;

		secondaryCountLoaded = false;

		recentsFollowersCount = -1;

	}

	public static String introtext1 = "Manage your twitter accounts & Grow your Twitter followers.";
	public static String introtext2 = "Start Following searched user's Followers";
	public static String introtext3 = "Check Overlapping Followers/Followings with any user.";
	public static String introtext4 = "Send DM to your FollowBack users";
	public static String introtext5 = "Analyse your daily followers. Including all kind of respective followers";
	public static String introtext6 = "You can schedule your post";
	public static String introtext7 = "Get instant feeds";

}
