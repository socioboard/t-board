package com.socioboard.t_board_pro;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.adapters.AccountAdapter;
import com.socioboard.t_board_pro.adapters.DrawerAdapter;
import com.socioboard.t_board_pro.dialog.Multi_Dialog;
import com.socioboard.t_board_pro.dialog.Radio_Dialog;
import com.socioboard.t_board_pro.dialog.Single_Dialog;
import com.socioboard.t_board_pro.dialog.Standard_Dialog;
import com.socioboard.t_board_pro.fragments.FragmentIAMFollowingTo;
import com.socioboard.t_board_pro.fragments.FragmentProfile;
import com.socioboard.t_board_pro.fragments.FragmentSchedule;
import com.socioboard.t_board_pro.fragments.FragmentSearch;
import com.socioboard.t_board_pro.fragments.FragmentTimeLine;
import com.socioboard.t_board_pro.fragments.FragmentTweet;
import com.socioboard.t_board_pro.fragments.FragmentUsersFollowingToMe;
import com.socioboard.t_board_pro.twitterapi.OAuthSignaturesGenerator;
import com.socioboard.t_board_pro.twitterapi.TwitterAccessTokenPost;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterSignIn;
import com.socioboard.t_board_pro.twitterapi.TwitterTimeLineRequest2;
import com.socioboard.t_board_pro.ui.Items;
import com.socioboard.t_board_pro.ui.MultiSwipeRefreshLayout;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TwtboardproLocalData;
import com.socioboard.tboardpro.R;

public class MainActivity extends ActionBarActivity implements
		MultiSwipeRefreshLayout.CanChildScrollUpCallback {

 	public static Menu yoyo;
	private String[] mDrawerTitles;
	private TypedArray mDrawerIcons;
	private ArrayList<Items> drawerItems;
	private ArrayList<ModelUserDatas> accountList;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList_Left, mDrawerList_Right;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	TwtboardproLocalData twiterManyLocalData;
	OAuthSignaturesGenerator oAuthSignaturesGenerator;
	Bitmap userImage, userbannerImage;
	public String requestAccessToken, requestAccessSecret;
	boolean callBackConfirm = false;
	Dialog webDialog;
	WebView webView;
	ProgressDialog progressDialog;

	FragmentManager fragmentManager;

	FragmentTransaction fragmentTransaction;

	TextView textViewUserName;

	ImageView imageViewProfileImage, imageviewCoverTimeline;

	RelativeLayout relOutAdAccount, relOutSettingsRight, relOutSettingsLeft,
			relOutFeedbackLeft, relOutHeader;

	private static FragmentManager mManager;

	Toolbar toolbar;

	private MultiSwipeRefreshLayout mSwipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		myprint("onCreate");

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

		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Right Side Menu

				onPrepareOptionsMenu(yoyo);

			}
		});

		if (toolbar != null)
			setSupportActionBar(toolbar);

		twiterManyLocalData = new TwtboardproLocalData(getApplicationContext());

		initView();

		loadTHisFragment();

		CookieSyncManager.createInstance(getApplicationContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();

	}

	private void loadTHisFragment() {

		// new UserAccountData().execute();

		fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.replace(R.id.main_content, new FragmentTimeLine());

		fragmentTransaction.commit();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();

		trySetupSwipeRefresh();

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
			fragment = new FragmentSearch();
			myprint("FragmentSearch");
			break;

		case 6:
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

		MainSingleTon.currentUserModel = accountList.get(position-1);

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

	@Override
	public boolean canSwipeRefreshChildScrollUp() {
		return false;
	}

	private void trySetupSwipeRefresh() {

		mSwipeRefreshLayout = (MultiSwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

		if (mSwipeRefreshLayout != null) {

			mSwipeRefreshLayout.setColorSchemeResources(
					R.color.refresh_progress_1, R.color.refresh_progress_2,
					R.color.refresh_progress_3);

			mSwipeRefreshLayout
					.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						@Override
						public void onRefresh() {
							Toast.makeText(getApplication(), "Refresh!",
									Toast.LENGTH_LONG).show();
						}
					});

			if (mSwipeRefreshLayout instanceof MultiSwipeRefreshLayout) {

				MultiSwipeRefreshLayout mswrl = (MultiSwipeRefreshLayout) mSwipeRefreshLayout;

				mswrl.setCanChildScrollUpCallback(this);
			}

		}

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
			selectItemRight(position, view);
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

		relOutAdAccount = (RelativeLayout) footerR
				.findViewById(R.id.relAddAccount);

		relOutSettingsRight = (RelativeLayout) footerR
				.findViewById(R.id.relSettings);

		relOutFeedbackLeft = (RelativeLayout) footer
				.findViewById(R.id.relfeedback);

		relOutSettingsLeft = (RelativeLayout) footer
				.findViewById(R.id.relSettings);

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

		mDrawerList_Left.setAdapter(new DrawerAdapter(getApplicationContext(),
				drawerItems));

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

				progressDialog.setMessage("Twitter SignIn process..");

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

		relOutSettingsLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("relOutSettingsLeft");

			}
		});

		relOutSettingsRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("relOutSettingsRight");

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

				webDialog.setCancelable(false);

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

				String url1 = url.replace("http://globussoft.com/?", "");

				String[] tokenarray = url1.split("&");

				String[] oauthtokenrray = tokenarray[0].split("=");

				String[] oauthverifier = tokenarray[1].split("=");

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

			myToastL("Fucker You are already Added");

		} else {

			twiterManyLocalData.addNewUserAccount(addNewAccountModel);
		}

		// Update UI

		setRightSideDrawer();
	}

	void myToastS(final String toastMsg) {

		Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG)
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

	public static void showMyDialog(String title, String message,
			String negativeButton, String positiveButton) {
		Standard_Dialog newDialog = Standard_Dialog.newInstance(title, message,
				negativeButton, positiveButton);
		newDialog.show(mManager, "dialog");
	}

	public static void showMySingleDialog(String title,
			ArrayList<String> dialogItems, String negativeButton,
			String positiveButton) {
		Single_Dialog newDialog = Single_Dialog.newInstance(title, dialogItems,
				negativeButton, positiveButton);
		newDialog.show(mManager, "dialog");
	}

	public static void showMyRadioDialog(String title,
			ArrayList<String> dialogItems, String negativeButton,
			String positiveButton) {
		Radio_Dialog newDialog = Radio_Dialog.newInstance(title, dialogItems,
				negativeButton, positiveButton);
		newDialog.show(mManager, "dialog");
	}

	public static void showMyMultiDialog(String title,
			ArrayList<String> dialogItems, String negativeButton,
			String positiveButton) {
		Multi_Dialog newDialog = Multi_Dialog.newInstance(title, dialogItems,
				negativeButton, positiveButton);
		newDialog.show(mManager, "dialog");
	}

	// class UserAccountData

	public class UserAccountData extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			TwitterTimeLineRequest2 twitterTimeLineRequest = new TwitterTimeLineRequest2(
					MainSingleTon.currentUserModel,
					new TwitterRequestCallBack() {

						@Override
						public void onSuccess(String jsonResult) {

							myprint("onSuccess jsonResult " + jsonResult);

						}

						@Override
						public void onFailure(Exception e) {

							myprint("onFailure e " + e);
						}

						@Override
						public void onSuccess(JSONObject jsonObject) {
							// TODO Auto-generated method stub

						}
					});

			String userShowUrl = MainSingleTon.userAccountData + "?"
					+ "user_id=" + MainSingleTon.currentUserModel.getUserid();

			twitterTimeLineRequest.doInBackground(userShowUrl);

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);

			myprint("onProgressUpdate " + values);

		}

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

		AccountAdapter temAadapter = new AccountAdapter(  accountList,MainActivity.this);

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

		loadTHisFragment();

	}

	void loadMyProfiePicture(final String profile_image_url) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {

					userImage = BitmapFactory.decodeStream(new URL(
							profile_image_url).openStream());

					imageViewProfileImage.setImageBitmap(userImage);

				} catch (MalformedURLException e) {

					e.printStackTrace();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}
		}).start();

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

	 
}
