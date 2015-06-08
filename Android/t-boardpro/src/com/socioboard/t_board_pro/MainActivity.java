package com.socioboard.t_board_pro;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.socioboard.t_board_pro.adapters.AccountAdapter;
import com.socioboard.t_board_pro.adapters.DrawerAdapter;
import com.socioboard.t_board_pro.fragments.FragmentCopyFollowers;
import com.socioboard.t_board_pro.fragments.FragmentFans;
import com.socioboard.t_board_pro.fragments.FragmentFavourites;
import com.socioboard.t_board_pro.fragments.FragmentHashTagSearch;
import com.socioboard.t_board_pro.fragments.FragmentIAMFollowingTo;
import com.socioboard.t_board_pro.fragments.FragmentMutualFollowers;
import com.socioboard.t_board_pro.fragments.FragmentNanFollowers;
import com.socioboard.t_board_pro.fragments.FragmentOverlappingFollowers;
import com.socioboard.t_board_pro.fragments.FragmentOverlappingFollowings;
import com.socioboard.t_board_pro.fragments.FragmentProfile;
import com.socioboard.t_board_pro.fragments.FragmentSchedule;
import com.socioboard.t_board_pro.fragments.FragmentSearch;
import com.socioboard.t_board_pro.fragments.FragmentSettingsRight;
import com.socioboard.t_board_pro.fragments.FragmentTimeLine;
import com.socioboard.t_board_pro.fragments.FragmentTweet;
import com.socioboard.t_board_pro.fragments.FragmentUsersFollowingToMe;
import com.socioboard.t_board_pro.twitterapi.OAuthSignaturesGenerator;
import com.socioboard.t_board_pro.twitterapi.TwitterAccessTokenPost;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterSignIn;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.twitterapi.TwitterUserShowRequest;
import com.socioboard.t_board_pro.ui.Items;
import com.socioboard.t_board_pro.util.ConnectionDetector;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.FullUserDetailModel;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.SearchDetailModel;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.TmpCallback;
import com.socioboard.t_board_pro.util.TweetDMScheduller;
import com.socioboard.t_board_pro.util.Utils;
import com.socioboard.tboardpro.R;

public class MainActivity extends ActionBarActivity {

 	private String[] mDrawerTitles;

	private TypedArray mDrawerIcons;

	private ArrayList<Items> drawerItems;

	private ArrayList<ModelUserDatas> accountList;

	private DrawerLayout mDrawerLayout;

	private ListView mDrawerList_Left, mDrawerList_Right;

	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;

	private CharSequence mTitle;

	TboardproLocalData twiterManyLocalData;

	OAuthSignaturesGenerator oAuthSignaturesGenerator;

	Bitmap userImage, userbannerImage;

	public String requestAccessToken, requestAccessSecret;

	boolean callBackConfirm = false, isFirstTimeCountsChecked = false;

	Dialog webDialog;

	WebView webView;

	TextView title_textview;

	ProgressDialog progressDialog;

	public static ProgressBar toolbarProgressBar;

	static Handler handler = new Handler();
	
	public static Menu yoyo;

	public ImageView imageViewSettings;

	FragmentManager fragmentManager;

	FragmentTransaction fragmentTransaction;

	TweetDMScheduller myReceiver;

	TextView textViewUserName;

	ImageView imageViewProfileImage, imageviewCoverTimeline;

	RelativeLayout relOutAdAccount, relOutSettingsRight, relOutFeedbackLeft,
			relOutHeader;

	Toolbar toolbar;

	Timer timer = new Timer(), timer2 = new Timer();

	DrawerAdapter drawerAdapter;

	private FragmentManager mManager;

	public static boolean isNeedToRefreshDrawer = false;

	public static boolean isNeedToSendbroadCast = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		myprint("onCreateMainActivity");

		progressDialog = new ProgressDialog(MainActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);

		fragmentManager = getSupportFragmentManager();

		setContentView(R.layout.activity_main);

		oAuthSignaturesGenerator = new OAuthSignaturesGenerator(
				MainSingleTon.currentUserModel.getUserAcessToken(),
				MainSingleTon.currentUserModel.getUsersecretKey(),
				MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "GET");

		toolbar = (Toolbar) findViewById(R.id.toolbar);

		ImageView img = (ImageView) toolbar.findViewById(R.id.img);

		title_textview = (TextView) toolbar.findViewById(R.id.title);

		toolbarProgressBar = (ProgressBar) toolbar
				.findViewById(R.id.toolbarProgressBar);

		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Right Side Menu

				onPrepareOptionsMenu(yoyo);

			}
		});

		if (toolbar != null)
			setSupportActionBar(toolbar);

		twiterManyLocalData = new TboardproLocalData(getApplicationContext());

		MainSingleTon.connectionDetector = new ConnectionDetector(
				getApplicationContext());

		initView();

		CookieSyncManager.createInstance(getApplicationContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();

		myReceiver = new TweetDMScheduller();

		IntentFilter intentFilter = new IntentFilter(
				MainSingleTon.broadcataction);

		MainActivity.this.registerReceiver(myReceiver, intentFilter);

	}

	private void loadTHisFragment() {

		fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.replace(R.id.main_content, new FragmentTimeLine());

		fragmentTransaction.commit();

		loadMyProfileImages();

		title_textview.setText(mDrawerTitles[2]);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		mDrawerToggle.onConfigurationChanged(newConfig);

		System.out.println(" + + + + +   onConfigurationChanged + + + + +");
	}

	// SELECT

	private void selectItemLeft(int position, View view) {

		myprint("selectItemLeft position " + position);

		Fragment fragment = null;

		switch (position) {

		case 0:
			fragment = new FragmentProfile();
			myprint("FragmentProfile");
			break;
		case 1:
			fragment = new FragmentTweet();  
			myprint("FragmentTweet");
			break;
		case 2:
			fragment = new FragmentTimeLine();
			myprint("FragmentTimeLine");
			break;
		case 3:
			fragment = new FragmentIAMFollowingTo();
			myprint("FragmentIAMFollowingTo");
			break;

		case 4:
			fragment = new FragmentUsersFollowingToMe();
			myprint("FragmentUsersFollowingToMe");
			break;

		case 5:
			fragment = new FragmentCopyFollowers();
			myprint("FragmentCopyFollowers");
			break;

		case 6:
			fragment = new FragmentFavourites();
			myprint("FragmentFavourites");
			break;

		case 7:
			fragment = new FragmentSearch();
			myprint("FragmentSearch");
			break;

		case 8:
			fragment = new FragmentHashTagSearch();
			myprint("FragmentHashTagSearch");
			break;

		case 9:
			
			if (MainSingleTon.fansCount == -1) {
				myToastS("Please wait");
				return;
			} else {
				fragment = new FragmentFans();
				myprint("FragmentFans");
			}
			break;

		case 10:
			
			if (MainSingleTon.mutualfansCount == -1) {
				myToastS("Please wait");
				return;
			} else {
				fragment = new FragmentMutualFollowers();
				myprint("FragmentMutualFans");
			}
			break;

		case 11:
	
			if (MainSingleTon.fansCount == -1) {
				myToastS("Please wait");
				return;
			} else {
				fragment = new FragmentNanFollowers();
				myprint("FragmentNanFollowers");
			}
			break;

		case 12:

			if (MainSingleTon.fansCount == -1) {
				myToastS("Please wait");
				return;
			} else {
				fragment = new FragmentOverlappingFollowers();
				myprint("FragmentOverlappingFollowers");
			}

			break;

		case 13:

			if (MainSingleTon.fansCount == -1) {
				myToastS("Please wait");
				return;
			} else {
				fragment = new FragmentOverlappingFollowings();
				myprint("FragmentOverlappingFollowings");
			}

			break;

		case 14:
			fragment = new FragmentSchedule();
			myprint("FragmentSchedule");
			break;

		default:
			myprint("default: ");
			break;
		
		}

		if (fragment != null) {

			// Insert the fragment by replacing any existing fragment

			fragmentTransaction = fragmentManager.beginTransaction();

			fragmentTransaction.replace(R.id.main_content, fragment);

			fragmentTransaction.commit();

		}

		// Highlight the selected item, update the title, and close the drawer

		if (mDrawerList_Left.isEnabled()) {

			mDrawerList_Left.setItemChecked(position, true);

			if (position != 0) {

				setTitle(mDrawerTitles[position]);

			}

			mDrawerLayout.closeDrawer(mDrawerList_Left);

		} else {

			mDrawerList_Right.setItemChecked(position, true);

			if (position != 0) {

				setTitle(mDrawerTitles[position]);

			}

			mDrawerLayout.closeDrawer(mDrawerList_Right);

		}

		title_textview.setText(mDrawerTitles[position]);

	}

	private void selectItemRight(int position, View view) {

		myprint("  selectItemRight position  " + position);

		myprint("  accountList.size()  " + accountList.size());

		// Highlight the selected item, update the title, and close the drawer

		if (mDrawerList_Left.isEnabled()) {

			mDrawerList_Left.setItemChecked(position, true);

			if (position != 0) {

				setTitle(mDrawerTitles[position]);

			}

			mDrawerLayout.closeDrawer(mDrawerList_Left);

		} else {

			mDrawerList_Right.setItemChecked(position, true);

			if (position != 0) {

				setTitle(mDrawerTitles[position]);

			}

			mDrawerLayout.closeDrawer(mDrawerList_Right);
		}

		MainSingleTon.currentUserModel = accountList.get(position - 1);

		setThisAsACurrentAccount(MainSingleTon.currentUserModel);

		setRightSideDrawer();

	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	@Override
	public void setTitle(CharSequence title) {

		myprint("setTitle " + title);
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		System.out.println("onCreateOptionsMenu");

		// Inflate the menu; this adds items to the action bar if it is present.

		// getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		yoyo = menu;

		System.out.println("onPrepareOptionsMenu");

		// If the nav drawer is open, hide action items related to the content

		if (mDrawerLayout.isDrawerOpen(mDrawerList_Right)) {
			mDrawerLayout.closeDrawer(mDrawerList_Right);
			// yoyo.findItem(R.id.action_settings).setVisible(true);
		} else {
			// yoyo.findItem(R.id.action_settings).setVisible(false);
			mDrawerLayout.openDrawer(mDrawerList_Right);

		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		System.out.println("onOptionsItemSelected");

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;

		}

		if (mDrawerLayout.isDrawerOpen(mDrawerList_Right)) {
			mDrawerLayout.closeDrawer(mDrawerList_Right);
		} else {
			mDrawerLayout.openDrawer(mDrawerList_Right);
		}

		return super.onOptionsItemSelected(item);

	}

	@SuppressLint("NewApi")
	public int calculateDrawerWidth() {

		// Calculate ActionBar height

		TypedValue tv = new TypedValue();

		int actionBarHeight = 0;

		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getResources().getDisplayMetrics());
		}

		Display display = getWindowManager().getDefaultDisplay();

		int width;

		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
		} else {
			width = display.getWidth(); // deprecatedf
		}

		return width - actionBarHeight;

	}

	private class DrawerItemClickListenerLeft implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItemLeft(position, view);
		}

	}

	private class DrawerItemClickListenerRight implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {

			if (accountList.size() == 0) {

			} else {

				selectItemRight(position, view);

			}
		}

	}

	private void initView() {

		mManager = getSupportFragmentManager();

		mDrawerTitles = getResources().getStringArray(R.array.drawer_titles);

		mDrawerIcons = getResources().obtainTypedArray(R.array.drawer_icons);

		drawerItems = new ArrayList<Items>();

		mDrawerList_Left = (ListView) findViewById(R.id.left_drawer);

		mDrawerList_Right = (ListView) findViewById(R.id.right_drawer);

		for (int i = 0; i < mDrawerTitles.length; i++) {
			drawerItems.add(new Items(mDrawerTitles[i], mDrawerIcons
					.getResourceId(i, -(i + 1))));
		}

		mTitle = mDrawerTitle = getTitle();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
				R.string.drawer_open, R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mTitle);
				// yoyo.findItem(R.id.action_settings).setVisible(true);
				// invalidateOptionsMenu();

			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(mDrawerTitle);
				// invalidateOptionsMenu();

			}
		};

		// Set the drawer toggle as the DrawerListener

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		LayoutInflater inflater = getLayoutInflater();

		final ViewGroup footer = (ViewGroup) inflater.inflate(
				R.layout.footer_left, mDrawerList_Left, false);

		final ViewGroup headerR = (ViewGroup) inflater.inflate(R.layout.header,
				mDrawerList_Right, false);

		final ViewGroup footerR = (ViewGroup) inflater.inflate(R.layout.footer,
				mDrawerList_Right, false);

		imageViewSettings = (ImageView) headerR
				.findViewById(R.id.imageView1headerSettings);

		imageViewSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				accountSettings();

			}
		});

		if (twiterManyLocalData.getAllIds().size() == 1) {

			imageViewSettings.setVisibility(View.VISIBLE);
		}

		relOutAdAccount = (RelativeLayout) footerR
				.findViewById(R.id.relAddAccount);

		relOutSettingsRight = (RelativeLayout) footerR
				.findViewById(R.id.relSettings);

		relOutFeedbackLeft = (RelativeLayout) footerR
				.findViewById(R.id.relFeddBack);

		mDrawerList_Left.addFooterView(footer, null, true); // true = clickable

		mDrawerList_Right.addHeaderView(headerR, null, true);

		mDrawerList_Right.addFooterView(footerR, null, true);

		// Set width of drawer
		DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) mDrawerList_Left
				.getLayoutParams();

		lp.width = calculateDrawerWidth();

		mDrawerList_Left.setLayoutParams(lp);

		// Set width of drawer

		DrawerLayout.LayoutParams lpR = (DrawerLayout.LayoutParams) mDrawerList_Right
				.getLayoutParams();

		lpR.width = calculateDrawerWidth();

		mDrawerList_Right.setLayoutParams(lpR);

		// Set the adapter for the list view
		drawerAdapter = new DrawerAdapter(getApplicationContext(), drawerItems);

		mDrawerList_Left.setAdapter(drawerAdapter);

		// Set the list's click listener

		mDrawerList_Left
				.setOnItemClickListener(new DrawerItemClickListenerLeft());

		mDrawerList_Right
				.setOnItemClickListener(new DrawerItemClickListenerRight());

		relOutAdAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("relOutAdAccount");

				CookieSyncManager.createInstance(getApplicationContext());
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.removeAllCookie();

				progressDialog.setMessage("Signing in to Twitter..");

				showProgress();

				startNewLogInProcess();

			}

		});

		relOutFeedbackLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("relOutFeedback");

				String url = "http://www.twtboardpro.com/";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);

			}
		});

		relOutSettingsRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("relOutSettingsRight");

				fragmentTransaction = fragmentManager.beginTransaction();

				fragmentTransaction.replace(R.id.main_content,
						new FragmentSettingsRight());

				fragmentTransaction.commit();

				title_textview.setText("Settings");

				mDrawerLayout.closeDrawer(mDrawerList_Right);

			}
		});

		// new GetProfileDetails().execute();

		relOutHeader = (RelativeLayout) headerR.findViewById(R.id.reOutHeader);

		relOutHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("relOutHeader");

			}
		});

		textViewUserName = (TextView) headerR.findViewById(R.id.username);

		imageViewProfileImage = (ImageView) headerR
				.findViewById(R.id.imageViewprofile);

		imageviewCoverTimeline = (ImageView) headerR
				.findViewById(R.id.timelinecover);

		setRightSideDrawer();

		setThisAsACurrentAccount(MainSingleTon.currentUserModel);

		timer2.schedule(new TimerTask() {

			@Override
			public void run() {

				if (isNeedToRefreshDrawer) {

					MainActivity.isNeedToRefreshDrawer = false;

					if (isFirstTimeCountsChecked) {

						determineEntitiesCounts();
					}

					handler.post(new Runnable() {

						@Override
						public void run() {

							int listCount = mDrawerList_Left.getCount();

							mDrawerList_Left.setAdapter(drawerAdapter);

							mDrawerList_Left.setScrollY(listCount);

						}
					});
				}
			}
		}, 2000, 1000);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		FlurryAgent.onStartSession(MainActivity.this);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		FlurryAgent.onEndSession(MainActivity.this);

	}

	protected void startNewLogInProcess() {

		// Step 1
		new GetReqToken().execute();

	}

	// class GetReqToken

	public class GetReqToken extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			TwitterSignIn twitterSignIn = new TwitterSignIn();

			String responseTokens = twitterSignIn.postForRequestToken();

			myprint("GetReqToken responseTokens =" + responseTokens);

			return responseTokens;

		}

		@Override
		protected void onPostExecute(String responseTokens) {
			super.onPostExecute(responseTokens);

			// Step 2

			if (responseTokens == null) {

				myToastL("Sorry Unable to process");

				hideProgress();

			} else {

				extractBaseString(responseTokens);

			}

		}

	}

	void extractBaseString(String baseString) {

		// Token
		int startInd = baseString.indexOf("=") + 1, endInd = baseString
				.indexOf("&");

		requestAccessToken = baseString.substring(startInd, endInd);

		myprint("requestAccessToken " + requestAccessToken);

		// Secret
		String tmp = baseString.substring(endInd + 2);

		startInd = tmp.indexOf("=") + 1;

		endInd = tmp.indexOf("&");

		requestAccessSecret = tmp.substring(startInd, endInd);

		myprint("requestAccessSecret " + requestAccessSecret);

		callBackConfirm = baseString.contains("=true");

		myprint("callBackConfirm " + callBackConfirm);

		// TIWitter will handle now
		loadSignInWebView();

	}

	public void loadSignInWebView() {

		hideProgress();

		new Handler().post(new Runnable() {

			@Override
			public void run() {

				webDialog = new Dialog(MainActivity.this);

				webDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

				webDialog.setCancelable(true);

				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

				Window window = webDialog.getWindow();

				lp.copyFrom(window.getAttributes());

				// This makes the dialog take up the full width

				lp.width = WindowManager.LayoutParams.MATCH_PARENT;

				lp.height = WindowManager.LayoutParams.MATCH_PARENT;

				window.setAttributes(lp);

				webDialog.setContentView(R.layout.signin_webview);

				webDialog.setCancelable(true);

				String webLoadSignInUrl = MainSingleTon.signInRequestURL
						+ requestAccessToken;

				myprint("webLoadSignInUrl = " + webLoadSignInUrl);

				webView = (WebView) webDialog
						.findViewById(R.id.dialogue_web_view);

				// webview listener.

				webView.setWebViewClient(new MyWebClient());

				// ................

				webView.setVerticalScrollBarEnabled(false);
				webView.setHorizontalScrollBarEnabled(false);
				webView.getSettings().setJavaScriptEnabled(true);

				webView.loadUrl(webLoadSignInUrl);

				webDialog.show();

			}
		});

	}

	// class MyWebClient

	class MyWebClient extends WebViewClient {

		private String TAG = "tag";

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			Log.d(TAG, "Redirecting URL " + url);

			// After successful name + password

			if (url.startsWith(MainSingleTon.oauth_callbackURL)) {

				myprint("final response to get tokens " + url);

				if (url.contains("denied")) {

					webView.destroy();

					webDialog.dismiss();

				}

				String url1 = url.replace(MainSingleTon.oauth_callbackURL, "");

				String[] tokenarray = url1.split("&");

				String[] oauthtokenrray = tokenarray[0].split("=");

				String[] oauthverifier = tokenarray[1].split("=");

				webView.destroy();

				webDialog.dismiss();

				myprint("oauthtokenrray  " + oauthtokenrray[1]
						+ "++++++++++++  " + oauthverifier[1]);

				// Now remaining our part start.

				new GetAccessToken().execute(oauthtokenrray[1],
						oauthverifier[1]);

				return true;
			}

			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			Log.d(TAG, "Page error: " + description);

			super.onReceivedError(view, errorCode, description, failingUrl);

			myprint("onReceivedError errorCode  " + errorCode);
			myprint("description description " + description);
			myprint("description failingUrl " + failingUrl);

			webDialog.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "Loading URL: " + url);

			super.onPageStarted(view, url, favicon);

			myprint("onPageStarted favicon " + favicon);

			// mSpinner.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			Log.d(TAG, "onPageFinished URL: " + url);

			myprint("onPageFinished title " + view.getTitle());

		}

	}

	public class GetAccessToken extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			// GetACcess tokens and USER details.
			progressDialog.setMessage("Almost completed..");

			showProgress();

			TwitterAccessTokenPost twitterSignIn = new TwitterAccessTokenPost();

			String responseTokens = twitterSignIn.postForAccessToken(params[0],
					params[1]);

			myprint("GetReqToken responseTokens =" + responseTokens);

			return responseTokens;
		}

		@Override
		protected void onPostExecute(String responseTokens) {

			super.onPostExecute(responseTokens);

			hideProgress();

			if (responseTokens == null) {

				myToastL("process failed!");

			} else {

				extractAccesTokenSecret(responseTokens);
			}
		}

	}

	void extractAccesTokenSecret(String baseString) {

		ModelUserDatas addNewAccountModel = new ModelUserDatas();

		// ..................................................

		String[] array1 = baseString.split("&");
		String[] arrayaccessToken = array1[0].split("=");
		String[] arrayTokenSecret = array1[1].split("=");
		String[] arrayUserID = array1[2].split("=");
		String[] arrayScreenName = array1[3].split("=");

		addNewAccountModel.setUserAcessToken(arrayaccessToken[1]);

		addNewAccountModel.setUsersecretKey(arrayTokenSecret[1]);

		addNewAccountModel.setUserid(arrayUserID[1]);

		addNewAccountModel.setUsername(arrayScreenName[1]);

		// .................................................

		myprint(addNewAccountModel);

		// saveDetailsAccessTokens

		myprint("setDetailsAccessTokens");

		if (twiterManyLocalData.getUserData(addNewAccountModel.getUserid()) != null) {

			myToastL("You are already Added");

		} else {

			twiterManyLocalData.addNewUserAccount(addNewAccountModel);

		}

		// Update UI

		setRightSideDrawer();

		TwitterUserShowRequest userShowRequest = new TwitterUserShowRequest(
				addNewAccountModel, new TwitterRequestCallBack() {

					@Override
					public void onSuccess(JSONObject jsonObject) {

						myprint("onSuccess " + jsonObject);

						parseJsonResultForAccountData(jsonObject);

					}

					@Override
					public void onSuccess(String jsonResult) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Exception e) {
						// TODO Auto-generated method stub
					}
				});

		userShowRequest.executeThisRequest(addNewAccountModel.getUsername());

	}

	void myToastS(final String toastMsg) {

		Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT)
				.show();
	}

	void myToastL(final String toastMsg) {

		Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		myprint("onResume");

	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void circleIn(View view) {

		// get the center for the clipping circle
		int cx = (view.getLeft() + view.getRight()) / 2;
		int cy = (view.getTop() + view.getBottom()) / 2;

		// get the final radius for the clipping circle
		int finalRadius = Math.max(view.getWidth(), view.getHeight());

		// create the animator for this view (the start radius is zero)
		Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
				0, finalRadius);

		// make the view visible and start the animation
		view.setVisibility(View.VISIBLE);
		anim.start();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void circleOut(final View view) {

		// get the center for the clipping circle
		int cx = (view.getLeft() + view.getRight()) / 2;
		int cy = (view.getTop() + view.getBottom()) / 2;

		// get the initial radius for the clipping circle
		int initialRadius = view.getWidth();

		// create the animation (the final radius is zero)
		Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
				initialRadius, 0);

		// make the view invisible when the animation is done
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				view.setVisibility(View.INVISIBLE);
			}
		});

		// start the animation
		anim.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

	}

	public void setRightSideDrawer() {

		MainSingleTon.allUserdetails = twiterManyLocalData.getAllUsersData();

		MainSingleTon.allUserIDs = twiterManyLocalData.getAllIds();

		// Now remove it from here

		MainSingleTon.allUserdetails.remove(MainSingleTon.currentUserModel
				.getUserid());

		MainSingleTon.allUserIDs.remove(MainSingleTon.currentUserModel
				.getUserid());

		accountList = new ArrayList<ModelUserDatas>();

		for (int i = 0; i < MainSingleTon.allUserIDs.size(); i++) {

			accountList.add(MainSingleTon.allUserdetails
					.get(MainSingleTon.allUserIDs.get(i)));

		}

		AccountAdapter temAadapter = new AccountAdapter(accountList,
				MainActivity.this);

		myprint("accountList " + accountList);

		mDrawerList_Right.setAdapter(temAadapter);

	}

	public void setThisAsACurrentAccount(ModelUserDatas userDatas) {

		MainSingleTon.currentUserModel = userDatas;

		myprint(MainSingleTon.currentUserModel);

		Editor editor = getSharedPreferences("twtboardpro",
				Context.MODE_PRIVATE).edit();

		editor.putString("userid", MainSingleTon.currentUserModel.getUserid());

		myprint("editor " + editor.commit());

		textViewUserName.setText(MainSingleTon.currentUserModel.getUsername());

		String userStringImage = MainSingleTon.currentUserModel.getUserimage();

		if (userStringImage != null) {

			Bitmap bitmap = Utils.decodeBase64(userStringImage);

			imageViewProfileImage.setImageBitmap(bitmap);

		}

		MainSingleTon.resetSigleTon();

		MainActivity.isNeedToSendbroadCast = true;

		loadTHisFragment();

	}

	void showProgress() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				progressDialog.show();

			}
		});
	}

	void hideProgress() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				progressDialog.cancel();

			}
		});
	}

	// PROFILE WORK

	class DownloadIamge extends AsyncTask<Object, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Object... params) {

			String urlImg = params[0].toString(), userId = params[1].toString();

			TmpCallback callback = (TmpCallback) params[2];

			URL url;

			Bitmap userBitImage = null;

			try {

				url = new URL(urlImg);

				userBitImage = BitmapFactory.decodeStream(url.openStream());

				if (userBitImage != null) {

					callback.onsuccess(userId, userBitImage);
				}

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}

			return userBitImage;
		}

	}

	void savingStringImage(String userId, Bitmap userBitImage) {

		String stringBitpmap = Utils.encodeTobase64(userBitImage);

		twiterManyLocalData.updateUserData(userId,
				TboardproLocalData.KEY_Userimage, stringBitpmap);

		myprint("SaVED");
	}

	void downloadAndSaveThisuserImage(FullUserDetailModel userDatas) {

		new DownloadIamge().execute(userDatas.getUserImagerUrl(),
				userDatas.getId(), new TmpCallback() {

					@Override
					public void onsuccess(Object... params) {

						String userID = params[0].toString();

						Bitmap userBitImage = (Bitmap) params[1];

						savingStringImage(userID, userBitImage);

						handler.post(new Runnable() {

							@Override
							public void run() {
								setRightSideDrawer();
							}
						});
					}

				});
	}

	protected void parseJsonResultForAccountData(JSONObject jsonResult) {

		myprint("parseJsonResult  ");

		try {
			myprint("jsonResult   = " + jsonResult);

			FullUserDetailModel fullUserDetailModel = new FullUserDetailModel();

			fullUserDetailModel.setFollowingStatus(jsonResult.getString(
					Const.following).contains("true"));

			fullUserDetailModel.setId(jsonResult.getString(Const.id_str));

			fullUserDetailModel.setNoFollowers(jsonResult
					.getString(Const.followers_count));

			fullUserDetailModel.setNoToFollowing(jsonResult
					.getString(Const.friends_count));

			fullUserDetailModel.setNoTweets(jsonResult
					.getString(Const.statuses_count));

			fullUserDetailModel.setUserImagerUrl(jsonResult
					.getString(Const.profile_image_url));

			if (jsonResult.has(Const.profile_banner_url)) {

				myprint("Const.profile_banner_url  <"
						+ Const.profile_banner_url + ">");

				fullUserDetailModel.setBannerUrl(jsonResult
						.getString(Const.profile_banner_url));
			}

			fullUserDetailModel.setUserName("@"
					+ jsonResult.getString(Const.screen_name));

			myprint(fullUserDetailModel);

			myprint(fullUserDetailModel);

			downloadAndSaveThisuserImage(fullUserDetailModel);

		} catch (JSONException e) {

			e.printStackTrace();

		}

	}

	void loadMyProfileImages() {

		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				TwitterUserShowRequest userShowRequest = new TwitterUserShowRequest(
						MainSingleTon.currentUserModel,
						new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {

								String bannewrUrl;

								try {

									myprint(jsonObject);

									if (jsonObject
											.has(Const.profile_banner_url)) {

										myprint("Const.profile_banner_url  <"
												+ Const.profile_banner_url
												+ ">");

										myprint("jsonObject .getString(Const.profile_banner_url)>"
												+ jsonObject
														.getString(Const.profile_banner_url)
												+ ">");

										bannewrUrl = jsonObject
												.getString(Const.profile_banner_url);

										myprint("bannewrUrl " + bannewrUrl);

										new DownloadIamgeBanner()
												.execute(bannewrUrl);

									}

									FullUserDetailModel fullUserDetailModel = new FullUserDetailModel();

									fullUserDetailModel
											.setFollowingStatus(jsonObject
													.getString(Const.following)
													.contains("true"));

									fullUserDetailModel
											.setFollowingStatus(jsonObject
													.getString(Const.following)
													.contains("true"));

									fullUserDetailModel.setId(jsonObject
											.getString(Const.id_str));

									fullUserDetailModel.setFullName(jsonObject
											.getString(Const.name));

									fullUserDetailModel.setNoFollowers(jsonObject
											.getString(Const.followers_count));

									fullUserDetailModel.setNoToFollowing(jsonObject
											.getString(Const.friends_count));

									fullUserDetailModel.setNoTweets(jsonObject
											.getString(Const.statuses_count));

									fullUserDetailModel.setUserImagerUrl(jsonObject
											.getString(Const.profile_image_url));

									if (jsonObject
											.has(Const.profile_banner_url)) {

										myprint("Const.profile_banner_url  <"
												+ Const.profile_banner_url
												+ ">");

										myprint("jsonResult .getString(Const.profile_banner_url)>"
												+ jsonObject
														.getString(Const.profile_banner_url)
												+ ">");

										fullUserDetailModel.setBannerUrl(jsonObject
												.getString(Const.profile_banner_url));

									}

									fullUserDetailModel.setUserName("@"
											+ jsonObject
													.getString(Const.screen_name));

									MainSingleTon.fullUserDetailModel = fullUserDetailModel;

									MainSingleTon.favoritesCount = jsonObject
											.getInt(Const.favourites_count);

									MainSingleTon.favoritesCount = jsonObject
											.getInt(Const.favourites_count);

									MainSingleTon.tweetsCount = jsonObject
											.getInt(Const.statuses_count);

									MainSingleTon.followingCount = jsonObject
											.getInt(Const.friends_count);

									MainSingleTon.myfollowersCount = jsonObject
											.getInt(Const.followers_count);

									isNeedToRefreshDrawer = true;

									new DownloadMineIamge().execute(jsonObject
											.getString(Const.profile_image_url));

									loadOtherEntity();

								} catch (JSONException e) {

								}

							}

							@Override
							public void onSuccess(String jsonResult) {

								myprint("onSuccess " + jsonResult);

							}

							@Override
							public void onFailure(Exception e) {

							}
						});

				userShowRequest
						.executeThisRequest(MainSingleTon.currentUserModel
								.getUsername());

			}
		}, 3000);

	}

	private void loadOtherEntity() {

		myprint("@@@@@@@ loadOtherEntity @@@@@@@@");

		TwitterUserGETRequest userGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

					@Override
					public void onSuccess(JSONObject jsonObject) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(String jsonResult) {

						myprint("jsonResult" + jsonResult);

						try {

							JSONObject jsonObject = new JSONObject(jsonResult);

							JSONArray jsonArray;

							try {

								jsonArray = new JSONArray(jsonObject
										.getString("ids"));

								for (int i = 0; i < jsonArray.length(); ++i) {

									MainSingleTon.listMyfollowersIDs
											.add(jsonArray.getString(i));

								}

							} catch (JSONException e) {

								e.printStackTrace();
							}

							loadfollowings();

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(Exception e) {
						// TODO Auto-generated method stub

					}
				});

		String url = MainSingleTon.users_following_to_me_Ids;

		List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

		peramPairs.add(new BasicNameValuePair(Const.cursor, "-1"));

		peramPairs.add(new BasicNameValuePair(Const.count, "5000"));

		userGETRequest.executeThisRequest(url, peramPairs);

	}

	protected void loadfollowings() {

		myprint("@@@@@@@ loadfollowings @@@@@@@@");

		TwitterUserGETRequest userGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

					@Override
					public void onSuccess(JSONObject jsonObject) {

					}

					@Override
					public void onSuccess(String jsonResult) {

						myprint("jsonResult" + jsonResult);

						JSONArray jsonArray;

						try {

							JSONObject jsonObject = new JSONObject(jsonResult);

							jsonArray = new JSONArray(jsonObject
									.getString("ids"));

							for (int i = 0; i < jsonArray.length(); ++i) {

								MainSingleTon.toFollowingModelsIDs
										.add(jsonArray.getString(i));
							}

						} catch (JSONException e) {

							e.printStackTrace();
						}

						determineEntitiesCounts();

						isFirstTimeCountsChecked = true;

						isNeedToRefreshDrawer = true;

					}

					@Override
					public void onFailure(Exception e) {
						// TODO Auto-generated method stub

					}

				});

		String url = MainSingleTon.i_am_following_to_ids;

		List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

		peramPairs.add(new BasicNameValuePair(Const.cursor, "-1"));

		peramPairs.add(new BasicNameValuePair(Const.count, "5000"));

		userGETRequest.executeThisRequest(url, peramPairs);

	}

	protected void determineEntitiesCounts() {

		// .....................................................................

		ArrayList<String> listMyfollowersIDs = (ArrayList<String>) MainSingleTon.listMyfollowersIDs
				.clone();

		ArrayList<String> toFollowingModelsIDs = (ArrayList<String>) MainSingleTon.toFollowingModelsIDs
				.clone();

		myprint("listMyfollowersIDs  **********  " + listMyfollowersIDs);

		myprint("toFollowingModelsIDs  **********  " + toFollowingModelsIDs);

		toFollowingModelsIDs.removeAll(listMyfollowersIDs);

		MainSingleTon.nonFollowersIds = toFollowingModelsIDs;

		myprint("MainSingleTon.nonFollowersIds  **********  "
				+ MainSingleTon.nonFollowersIds);

		MainSingleTon.NOnfollowersCount = MainSingleTon.nonFollowersIds.size();

		// NOn followers are here

		toFollowingModelsIDs = (ArrayList<String>) MainSingleTon.toFollowingModelsIDs
				.clone();

		toFollowingModelsIDs.removeAll(MainSingleTon.nonFollowersIds);

		MainSingleTon.mutualsIds = toFollowingModelsIDs;

		MainSingleTon.mutualfansCount = MainSingleTon.mutualsIds.size();
		// NOn followers are here

		toFollowingModelsIDs = MainSingleTon.toFollowingModelsIDs;

		listMyfollowersIDs.removeAll(toFollowingModelsIDs);

		MainSingleTon.fansIds = listMyfollowersIDs;

		// fans are here

		// .....................................................................

		MainSingleTon.fansCount = MainSingleTon.fansIds.size();

		myprint("MainSingleTon.fansCount  **********  "
				+ MainSingleTon.fansCount);

		myprint("MainSingleTon.mutualfansCount  **********  "
				+ MainSingleTon.mutualfansCount);

		myprint("MainSingleTon.NOnfollowersCount  **********  "
				+ MainSingleTon.NOnfollowersCount);

		Intent intent = new Intent(MainSingleTon.broadcataction);

		if (MainSingleTon.mutualsIds.size() > 0) {
		
			MainActivity.this.sendBroadcast(intent);
		
		}
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		if (webDialog != null) {

			if (webDialog.isShowing()) {

				webDialog.dismiss();

			}
		}

	}

	class DownloadIamgeBanner extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {

			String urlImg = params[0].toString();

			URL url;

			Bitmap userBitBanner = null;

			try {

				url = new URL(urlImg);

				userBitBanner = BitmapFactory.decodeStream(url.openStream());

				MainSingleTon.bitmapBanner = userBitBanner;

				myprint("Banner downloaded");

				runOnUiThread(new Runnable() {
					public void run() {

						imageviewCoverTimeline
								.setImageBitmap(MainSingleTon.bitmapBanner);

					}
				});

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}

			return userBitBanner;
		}
	}

	void accountSettings() {

		final Dialog dialog;

		dialog = new Dialog(MainActivity.this);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.account_dialog);

		dialog.setCancelable(true);

		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		ImageView imageView = (ImageView) dialog.findViewById(R.id.profile_pic);

		TextView textView = (TextView) dialog
				.findViewById(R.id.textViewAccount);

		if (MainSingleTon.currentUserModel.getUserimage() != null) {

			Bitmap bitmap = Utils.decodeBase64(MainSingleTon.currentUserModel
					.getUserimage());

			if (bitmap == null) {
			} else {
				imageView.setImageBitmap(bitmap);
			}

		}

		textView.setText(MainSingleTon.currentUserModel.getUsername());

		Button buttonRemove, buttonCancel;

		buttonRemove = (Button) dialog.findViewById(R.id.button1Remove);

		buttonCancel = (Button) dialog.findViewById(R.id.button2Cancel);

		buttonRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("buttonRemove");

				TboardproLocalData twiterManyLocalData = new TboardproLocalData(
						getApplicationContext());

				twiterManyLocalData
						.deleteThisUserData(MainSingleTon.currentUserModel
								.getUserid());

				dialog.dismiss();

				Editor editor = getSharedPreferences("twtboardpro",
						Context.MODE_PRIVATE).edit();

				editor.putString("userid", null);

				editor.clear();

				myprint("editor " + editor.commit());

				MainSingleTon.currentUserModel = null;

				MainSingleTon.searchDetailModel = new SearchDetailModel();

				MainSingleTon.listMyfollowers.clear();

				startActivity(new Intent(getApplicationContext(),
						WelcomeActivity.class));
				finish();

			}
		});

		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("buttonCancel");

				dialog.dismiss();
			}
		});

		new Handler().post(new Runnable() {

			@Override
			public void run() {

				dialog.show();

			}
		});
	}

	class DownloadMineIamge extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String urlImg = params[0].toString();

			URL url;

			Bitmap userBitImage = null;

			try {

				url = new URL(urlImg);

				userBitImage = BitmapFactory.decodeStream(url.openStream());

				myprint("Download cPOmpleteas");

				if (userBitImage != null) {

					savingStringImage(userBitImage);

				}

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}

			return null;
		}

	}

	void savingStringImage(final Bitmap userBitImage) {

		String stringBitpmap = Utils.encodeTobase64(userBitImage);

		myprint("converted");

		MainSingleTon.currentUserModel.setUserimage(stringBitpmap);

		twiterManyLocalData.updateUserData(MainSingleTon.currentUserModel);

		MainSingleTon.currentUserModel.setUserimage(stringBitpmap);

		handler.post(new Runnable() {

			@Override
			public void run() {

				imageViewProfileImage.setImageBitmap(userBitImage);

			}
		});

		myprint("Saved");
	}

	public static void showActionBarProgress() {

		handler.post(new Runnable() {

			@Override
			public void run() {
				toolbarProgressBar.setVisibility(View.VISIBLE);
			}
		});

	}

	public static void HideActionBarProgress() {

		handler.post(new Runnable() {

			@Override
			public void run() {
				toolbarProgressBar.setVisibility(View.INVISIBLE);
			}
		});

	}

}
