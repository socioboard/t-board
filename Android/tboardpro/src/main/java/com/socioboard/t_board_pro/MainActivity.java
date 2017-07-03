package com.socioboard.t_board_pro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.socioboard.t_board_pro.adapters.AccountAdapter;
import com.socioboard.t_board_pro.adapters.DrawerAdapter;
import com.socioboard.t_board_pro.fragments.FragmentBlackList;
import com.socioboard.t_board_pro.fragments.FragmentCombinedOverlappings;
import com.socioboard.t_board_pro.fragments.FragmentCombinedSearch;
import com.socioboard.t_board_pro.fragments.FragmentCombinedTimelines;
import com.socioboard.t_board_pro.fragments.FragmentCompareList;
import com.socioboard.t_board_pro.fragments.FragmentCopyFollowers;
import com.socioboard.t_board_pro.fragments.FragmentFans;
import com.socioboard.t_board_pro.fragments.FragmentFavourites;
import com.socioboard.t_board_pro.fragments.FragmentHashKeywords;
import com.socioboard.t_board_pro.fragments.FragmentIAMFollowingTo;
import com.socioboard.t_board_pro.fragments.FragmentMutualFollowers;
import com.socioboard.t_board_pro.fragments.FragmentNonFollowers;
import com.socioboard.t_board_pro.fragments.FragmentProfile;
import com.socioboard.t_board_pro.fragments.FragmentRecentFollowers;
import com.socioboard.t_board_pro.fragments.FragmentRecentUnFollowers;
import com.socioboard.t_board_pro.fragments.FragmentSchedule;
import com.socioboard.t_board_pro.fragments.FragmentSettingsRight;
import com.socioboard.t_board_pro.fragments.FragmentTweet;
import com.socioboard.t_board_pro.fragments.FragmentUnfollowUsers;
import com.socioboard.t_board_pro.fragments.FragmentUsersFollowingToMe;
import com.socioboard.t_board_pro.fragments.FragmentWhiteList;
import com.socioboard.t_board_pro.fragments.FragmentsStatistics;
import com.socioboard.t_board_pro.twitterapi.OAuthSignaturesGenerator;
import com.socioboard.t_board_pro.twitterapi.TwitterAccessTokenPost;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterSignIn;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.twitterapi.TwitterUserShowRequest;
import com.socioboard.t_board_pro.ui.Items;
import com.socioboard.t_board_pro.util.ConnectionDetector;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.EntityModel;
import com.socioboard.t_board_pro.util.FullUserDetailModel;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.TmpCallback;
import com.socioboard.t_board_pro.util.TweetDMScheduller;
import com.socioboard.t_board_pro.util.Utils;
import com.socioboard.tboardpro.R;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.appindexing.Thing;
//import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends ActionBarActivity{

	FirebaseDatabase mFirebaseInstance;

	DatabaseReference mFirebaseDatabase;

	public static boolean alertok=false;

	private String[] mDrawerTitles;

	private TypedArray mDrawerIcons;

	private ArrayList<Items> drawerItems;

	private ArrayList<ModelUserDatas> accountList;

	private DrawerLayout mDrawerLayout;

	private ListView mDrawerList_Left, mDrawerList_Right;

	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;

	private CharSequence mTitle;

	int backCount = 0;

	TboardproLocalData twiterManyLocalData;

	OAuthSignaturesGenerator oAuthSignaturesGenerator;

	Bitmap userImage, userbannerImage;

	public String requestAccessToken, requestAccessSecret;

	boolean callBackConfirm = false, isFirstTimeCountsChecked = false;

	Dialog webDialog;

	WebView webView;

	TextView title_textview;

	ProgressDialog progressDialog;

	public static ProgressBar toolbarProgressBar, webViewProgress;

	static Handler handler = new Handler();

	public Menu yoyo;

	public ImageView imageViewSettings;

	FragmentManager fragmentManager;

	FragmentTransaction fragmentTransaction;

	TweetDMScheduller myReceiver;

	TextView textViewUserName;

	ImageView imageViewProfileImage, imageviewCoverTimeline;

	RelativeLayout relOutAdAccount, relOutSettingsRight, relOutFeedbackLeft,
			relOutHeader;

	Toolbar toolbar;

	SwitchCompat switchCompat1;

	Timer timer = new Timer(), timer2 = new Timer();

	DrawerAdapter drawerAdapter;

	private FragmentManager mManager;

	public static boolean isNeedToRefreshDrawer = false;

	public static boolean isNeedToSendbroadCast = true;

	public String getTwitterAccessToken, getUserId, getFirebaseToken, deviceId, userFullName;

	long followingCount, myfollowersCount;

	String userID, message;
	String username;

	String Title = "Notification";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		mFirebaseInstance = FirebaseDatabase.getInstance();
		mFirebaseDatabase = mFirebaseInstance.getReferenceFromUrl("https://tboardpro-4a5f9.firebaseio.com/");


		myprint("onCreateMainActivity");


		if (MainSingleTon.currentUserModel == null) {
			startActivity(new Intent(getApplicationContext(),
					SplashActivity.class));
			finish();
			return;
		}

		if (MainSingleTon.currentUserModel.getUserid() == null) {

			startActivity(new Intent(getApplicationContext(),
					SplashActivity.class));
			finish();
			return;

		}


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

		if (MainActivity.this != null) {

			MainActivity.this.registerReceiver(myReceiver, intentFilter);
		}





	}



	public void getdata()
	{
		mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				for (DataSnapshot player : dataSnapshot.getChildren())
				{
					for(DataSnapshot snapshot : player.getChildren())
					{
						String  id = (String) snapshot.child("userFullName").getValue();
						Toast.makeText(getApplicationContext(),"Hello"+id,Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}



	public void Registration(String getTwitterAccessToken, String getUserId, String getFirebaseToken, String deviceId, long followingCount, long myfollowersCount, String userFullName,String username, boolean alert) {
		userID = mFirebaseDatabase.push().getKey();
		FirebaseModel firebaseModel = new FirebaseModel(getTwitterAccessToken, getUserId, getFirebaseToken, deviceId, followingCount, myfollowersCount, userFullName,username, alert);
		mFirebaseDatabase.child("Tboardpro").child(getUserId).setValue(firebaseModel);
	}

	private void loadTHisFragment() {

		fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.replace(R.id.main_content,
				new FragmentCombinedTimelines());

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
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentProfile();
				myprint("FragmentProfile");
				break;

			case 1:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentTweet();
				myprint("FragmentTweet");
				break;

			case 2:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentCombinedTimelines();
				myprint("FragmentCombinedTimelines");
				break;

			case 3:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentIAMFollowingTo();
				myprint("FragmentIAMFollowingTo");
				break;

			case 4:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentUsersFollowingToMe();
				myprint("FragmentUsersFollowingToMe");
				break;

			case 5:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				if (!MainSingleTon.secondaryCountLoaded) {
					myToastS("Please wait");
					return;
				} else {
					fragment = new FragmentRecentFollowers();
					myprint("FragmentRecentFollowers");
				}
				break;

			case 6:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				if (!MainSingleTon.secondaryCountLoaded) {
					myToastS("Please wait");
					return;
				} else {
					fragment = new FragmentCopyFollowers();
					myprint("FragmentCopyFollowers");
				}
				break;
			case 7:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				if (!MainSingleTon.secondaryCountLoaded) {
					myToastS("Please wait");
					return;
				} else {
					fragment = new FragmentUnfollowUsers();
					myprint("FragmentUnfollowUsers");
				}
				break;

			case 8:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentFavourites();
				myprint("FragmentFavourites");
				break;

			case 9:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentCombinedSearch();
				myprint("FragmentCombinedSearch");
				break;

			case 10:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				if (!MainSingleTon.secondaryCountLoaded) {

					myToastS("Please wait");

					return;

				} else {

					fragment = new FragmentFans();

					myprint("FragmentFans");
				}

				break;

			case 11:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				if (!MainSingleTon.secondaryCountLoaded) {

					myToastS("Please wait");

					return;

				} else {

					fragment = new FragmentMutualFollowers();

					myprint("FragmentMutualFans");
				}

				break;

			case 12:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				if (!MainSingleTon.secondaryCountLoaded) {

					myToastS("Please wait");

					return;

				} else {

					fragment = new FragmentNonFollowers();

					myprint("FragmentNanFollowers");

				}

				break;

			case 13:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				if (!MainSingleTon.secondaryCountLoaded) {
					myToastS("Please wait");
					return;
				} else {
					fragment = new FragmentCombinedOverlappings();
					myprint("FragmentCombinedOverlappings");
				}

				break;

			case 14:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentSchedule();
				myprint("FragmentSchedule");
				break;

			case 15:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentsStatistics();
				myprint("FragmentsStatistics");
				break;


			case 16:
				fragment = new FragmentRecentUnFollowers();
				myprint("FragmentRecentFollowers");
				break;
			case 17:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentWhiteList();
				myprint("FragmentsWhiteList");
				break;
			case 18:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentBlackList();
				myprint("FragmentsBlclList");
				break;
			case 19:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentCompareList();
				myprint("FragmentCompareList");
				break;
			case 20:
				MainSingleTon.fragment_no=position;
				System.out.println("....."+position);
				fragment = new FragmentHashKeywords();
				myprint("FragmentCompareList");
				break;
//			case 21:
//				MainSingleTon.fragment_no=position;
//				System.out.println("......"+position);
//				fragment = new FragmentInactiveFollowing();
//				myprint("Fragment Inactive Following");
//				break;
			default:
				myprint("default: ");
				break;

		}

		if (fragment != null) {

			// Insert the fragment by replacing any existing fragment

			fragmentTransaction = fragmentManager.beginTransaction();

			fragmentTransaction.replace(R.id.main_content, fragment);

			fragmentTransaction.commit();

			// Highlight the selected item, update the title, and close the
			// drawer

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

		if (Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
		} else {
			width = display.getWidth(); // deprecatedf
		}

		return width - actionBarHeight;

	}

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
//	public Action getIndexApiAction() {
//		Thing object = new Thing.Builder()
//				.setName("Main Page") // TODO: Define a title for the content shown.
//				// TODO: Make sure this auto-generated URL is correct.
//				.setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//				.build();
//		return new Action.Builder(Action.TYPE_VIEW)
//				.setObject(object)
//				.setActionStatus(Action.STATUS_TYPE_COMPLETED)
//				.build();
//	}

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

	private void initView()
	{

		SharedPreferences sharedPreferences = getSharedPreferences("data1",Context.MODE_PRIVATE);
		final Editor editor = sharedPreferences.edit();

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

		switchCompat1 = (SwitchCompat) footerR.findViewById(R.id.switchButton1);

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


		boolean val = sharedPreferences.getBoolean("c",true);
		System.out.println("value======"+val);

		if(val)
		{
			System.out.println("valueok=="+"okkk");
			switchCompat1.setChecked(true);
		}
		else {
			switchCompat1.setChecked(false);
		}

		switchCompat1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isCheack) {
				if(isCheack)
				{
					alertok = true;
					editor.putBoolean("c",true);
					editor.commit();
					determineEntitiesCounts();
				}
				else {
					alertok = false;
					editor.remove("c");
					editor.putBoolean("c",false);
					editor.commit();
					determineEntitiesCounts();
				}
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

				webViewProgress = (ProgressBar) webDialog
						.findViewById(R.id.progressBar1);

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

			webViewProgress.setVisibility(View.INVISIBLE);

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

			webViewProgress.setVisibility(View.VISIBLE);

			if (url.startsWith("https://twitter.com/login/error?")) {

				new AlertDialog.Builder(MainActivity.this)
						.setTitle("SignIn failed!")
						.setMessage(
								"The username and password you entered did not match our records. Please double-check and try again.")
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {

										webDialog.dismiss();
									}
								}).setIcon(android.R.drawable.ic_dialog_alert)
						.show().setCancelable(false);

			} else {

			}

			myprint("onPageStarted favicon " + favicon);

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			Log.d(TAG, "onPageFinished URL: " + url);

			myprint("onPageFinished title " + view.getTitle());

			webViewProgress.setVisibility(View.INVISIBLE);

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

			myToastL("Account is already Added");

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

		Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
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

		if (twiterManyLocalData.getAllIds().size() == 1) {

			imageViewSettings.setVisibility(View.VISIBLE);

		} else {

			imageViewSettings.setVisibility(View.INVISIBLE);

		}

		AccountAdapter temAadapter = new AccountAdapter(accountList,
				MainActivity.this);

		myprint("accountList " + accountList);

		mDrawerList_Right.setAdapter(temAadapter);


	}

	public void setThisAsACurrentAccount(ModelUserDatas userDatas) {

		MainSingleTon.currentUserModel = userDatas;

		myprint(MainSingleTon.currentUserModel);

		//

		Editor editor = getSharedPreferences("twtboardpro",
				Context.MODE_PRIVATE).edit();

		editor.putString("userid", MainSingleTon.currentUserModel.getUserid());

		editor.putString("autoDmfirstime", "yes");

		editor.commit();

		//

		myprint("editor " + editor.commit());

		textViewUserName.setText("@"
				+ MainSingleTon.currentUserModel.getUsername());

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

					@Override
					public void onsuccess() {
						// TODO Auto-generated method stub

					}

				});
	}

	protected void parseJsonResultForAccountData(JSONObject jsonResult) {

		myprint("parseJsonResult MainActivity ");

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

									MainSingleTon.primaryCountLoaded = true;

									isNeedToRefreshDrawer = true;

									new DownloadMineIamge().execute(jsonObject
											.getString(Const.profile_image_url));

									loadusers_following_to_me_Ids();

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

		}, 2000);

	}

	public void loadusers_following_to_me_Ids()
	{

		myprint("@@@@@@@ loadOtherEntity users_following_to_me_Ids @@@@@@@@");

		TwitterUserGETRequest userGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String jsonResult)
			{

				MainSingleTon.listMyfollowersIDs.clear();

				myprint("MainSingleTon.listMyfollowersIDs.size"
						+ MainSingleTon.listMyfollowersIDs.size());

				myprint("jsonResult" + jsonResult);

				try {

					JSONObject jsonObject = new JSONObject(jsonResult);

					JSONArray jsonArray;

					try {

						jsonArray = new JSONArray(jsonObject
								.getString("ids"));

						for (int i = 0; i < jsonArray.length(); ++i) {

							MainSingleTon.listMyfollowersIDs
									.add(jsonArray.getString(i).trim());

						}

					} catch (JSONException e) {

						e.printStackTrace();
					}

					myprint("MainSingleTon.listMyfollowersIDs.size"
							+ MainSingleTon.listMyfollowersIDs.size());


					//updataFollowerTable(jsonResult,MainActivity.this);//13/06/2017

					loadfollowings();

					SharedPreferences prefs = getSharedPreferences(
							"twtboardpro", Context.MODE_PRIVATE);

					boolean isThisUsersFollowersLoaded = prefs
							.getBoolean(MainSingleTon.currentUserModel
									.getUserid(), false);

					if (isThisUsersFollowersLoaded) {

						myprint("*************** YES ThisUsersFollowersLoaded "
								+ MainSingleTon.currentUserModel
								.getUsername());

					} else {

						myprint("Loading the followers of this User "
								+ MainSingleTon.currentUserModel
								.getUsername());

						Editor editor = prefs.edit();

						editor.putBoolean(
								MainSingleTon.currentUserModel
										.getUserid(), true);

						editor.commit();

						for (int i = 0; i < MainSingleTon.listMyfollowersIDs
								.size(); i++) {

							twiterManyLocalData.addNewDMsentId(
									MainSingleTon.currentUserModel
											.getUserid(),
									MainSingleTon.listMyfollowersIDs
											.get(i));

						}

					}

					// * * * * * * * * * * * * * * Recent

					String jsonDBResult = twiterManyLocalData
							.getAllFollowersIDs(MainSingleTon.currentUserModel
									.getUserid());

					if (jsonDBResult != null) {

						myprint("*********** jsonDBResult Recent jsonDBResult != null "
								+ jsonDBResult);

						JSONObject jsonObjectTMp = new JSONObject(
								jsonDBResult);

						JSONArray jsonArrayTmp;

						ArrayList<String> oldIds = new ArrayList<String>();

						try {

							jsonArrayTmp = new JSONArray(jsonObjectTMp
									.getString("ids"));

							for (int i = 0; i < jsonArrayTmp.length(); ++i) {

								oldIds.add(jsonArrayTmp.getString(i));

							}

						} catch (JSONException e) {

							e.printStackTrace();
						}

						ArrayList<String> tmpIds = (ArrayList<String>) differenciate(
								MainSingleTon.listMyfollowersIDs,
								oldIds);

						MainSingleTon.recentsFollowersCount = tmpIds
								.size();

					} else {

						myprint("*********** jsonDBResult Recent jsonDBResult ===== null "
								+ jsonDBResult);

						twiterManyLocalData.addFollwersIds(jsonResult,
								MainSingleTon.currentUserModel
										.getUserid());

						MainSingleTon.recentsFollowersCount = 0;

					}

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

	protected void saveDailyDetails() {

		SharedPreferences sharedPreferences = getSharedPreferences(
				"twtboardpro", Context.MODE_PRIVATE);

		Editor editor = sharedPreferences.edit();

		String strDateToday = sharedPreferences.getString("dateToday"
				+ MainSingleTon.currentUserModel.getUserid(), "****");

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

		Date date = new Date();

		System.out.println(dateFormat.format(date)); // 2014/08/06 15:59:48

		System.out.println("strDateToday == " + strDateToday); // 2014/08/06

		if (dateFormat.format(date).contains(strDateToday)) {

			System.out
					.println("*****************Todays Entity  data completed"); // 2014/08/06

		} else {

			System.out.println("*****************Saving todays Entity  data"); // 2014/08/06

			EntityModel entityModel = new EntityModel();

			entityModel.setFollowers(MainSingleTon.myfollowersCount);

			entityModel.setFollowings(MainSingleTon.followingCount);

			entityModel.setMutuals(MainSingleTon.mutualsIds.size());

			entityModel.setNonfollwers(MainSingleTon.nonFollowersIds.size());

			entityModel.setMillis(System.currentTimeMillis());

			twiterManyLocalData.addUserEntity(entityModel, MainSingleTon.currentUserModel.getUserid());

			editor.putString(
					"dateToday" + MainSingleTon.currentUserModel.getUserid(),
					dateFormat.format(date));

			editor.commit();

		}

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

				MainSingleTon.toFollowingModelsIDs.clear();

				myprint("MainSingleTon.toFollowingModelsIDs.size"
						+ MainSingleTon.toFollowingModelsIDs.size());

				try {

					JSONObject jsonObject = new JSONObject(jsonResult);

					jsonArray = new JSONArray(jsonObject
							.getString("ids"));

					for (int i = 0; i < jsonArray.length(); ++i) {

						MainSingleTon.toFollowingModelsIDs
								.add(jsonArray.getString(i).trim());

					}

				} catch (JSONException e) {

					e.printStackTrace();
				}

				myprint("MainSingleTon.toFollowingModelsIDs.size"
						+ MainSingleTon.toFollowingModelsIDs.size());

				MainSingleTon.secondaryCountLoaded = true;

				determineEntitiesCounts();

				isFirstTimeCountsChecked = true;

				isNeedToRefreshDrawer = true;

				// ...................................................

				Intent intent = new Intent(MainSingleTon.broadcataction);

				if (MainSingleTon.mutualsIds.size() > 0
						&& MainSingleTon.autodm) {

					myprint(" ********** AutoDM is ON ************ ");

					MainActivity.this.sendBroadcast(intent);

				} else {

					myprint(" ********** AutoDM is OFF ************ ");

				}

				// ..................................................

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

		MainSingleTon.mutualsIds = (ArrayList<String>) intersection(
				MainSingleTon.listMyfollowersIDs,
				MainSingleTon.toFollowingModelsIDs);

		// .......................................................................................

		myprint("recentsFollowersCount  **********  "
				+ MainSingleTon.recentsFollowersCount);



		//.........................................................................................13/06/2017

		String data = twiterManyLocalData.getUnFollowersIDs(MainSingleTon.currentUserModel.getUserid());
		if(data!=null)
		{
			try {

				ArrayList<String> unfollowerId = new ArrayList<>();

				JSONArray jsonArrayTmp = new JSONArray(twiterManyLocalData.getUnFollowersIDs(MainSingleTon.currentUserModel
						.getUserid()));

				for (int i = 0; i < jsonArrayTmp.length(); i++) {

					unfollowerId.add(jsonArrayTmp.getString(i));

				}

				System.out.println("unfollow user inside deterministic=="+unfollowerId);

				MainSingleTon.recentsUnFollowersCount = unfollowerId.size();

			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
//		else if(data.length()==2)
//		{
//			System.out.println("unfollow user is empty");
//			MainSingleTon.recentsUnFollowersCount = 0;
//		}
		else {
			Toast.makeText(getApplicationContext(),"No Recent UnFollower",Toast.LENGTH_SHORT).show();
			System.out.println("unfollow user is null==");
			MainSingleTon.recentsUnFollowersCount = 0;
		}

		//.........................................................................................

		myprint("recentsFollowersCount  **********  "
				+ MainSingleTon.recentsUnFollowersCount);






		myprint("mutualsIds  **********  " + MainSingleTon.mutualsIds.size());

		myprint("listMyfollowersIDs  **********  "
				+ MainSingleTon.listMyfollowersIDs.size());

		myprint("toFollowingModelsIDs  **********  "
				+ MainSingleTon.toFollowingModelsIDs.size());

		// .......................................................................................

		MainSingleTon.nonFollowersIds = (ArrayList<String>) differenciate(
				MainSingleTon.toFollowingModelsIDs,
				MainSingleTon.listMyfollowersIDs);

		myprint("nonFollowersIds  **********  "
				+ MainSingleTon.nonFollowersIds.size());

		myprint("listMyfollowersIDs  **********  "
				+ MainSingleTon.listMyfollowersIDs.size());

		myprint("toFollowingModelsIDs  **********  "
				+ MainSingleTon.toFollowingModelsIDs.size());

		// .......................................................................................

		MainSingleTon.fansIds = (ArrayList<String>) differenciate(
				MainSingleTon.listMyfollowersIDs,
				MainSingleTon.toFollowingModelsIDs);

		// .......................................................................................

		MainSingleTon.followingCount = MainSingleTon.toFollowingModelsIDs
				.size();

		MainSingleTon.myfollowersCount = MainSingleTon.listMyfollowersIDs
				.size();

		myprint("fansIds  **********  " + MainSingleTon.fansIds.size());

		myprint("followingCounts  **********  " + MainSingleTon.followingCount);


		myprint("myfollowersCount  **********  "
				+ MainSingleTon.myfollowersCount);


		System.out.println("UserName" + MainSingleTon.fullUserDetailModel.getFullName());

		System.out.println("UserName" + MainSingleTon.fullUserDetailModel.getUserName());

		username = MainSingleTon.fullUserDetailModel.getUserName();
		System.out.println("username=" + getFirebaseToken);//TO get firebase access Token

		getFirebaseToken = FirebaseInstanceId.getInstance().getToken();
		System.out.println("Token=" + getFirebaseToken);//TO get firebase access Token

		getTwitterAccessToken = MainSingleTon.currentUserModel.getUserAcessToken();
		System.out.println("TwitterAccessToken==" + getTwitterAccessToken);

		getUserId = MainSingleTon.currentUserModel.getUserid();
		System.out.println("TwitterUerID=" + getUserId);


		deviceId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);//To get anderoid device ID
		System.out.println("Android deviceID====" + deviceId);

		followingCount = MainSingleTon.followingCount;
		System.out.println("Followercount==" + followingCount);

		myfollowersCount = MainSingleTon.myfollowersCount;
		System.out.println("Followercount==" + myfollowersCount);

		userFullName = MainSingleTon.fullUserDetailModel.getFullName();
		System.out.println("userFullName==" + userFullName);

		System.out.println("alertOk==" + alertok);


		Registration(getTwitterAccessToken, getUserId, getFirebaseToken, deviceId, followingCount, myfollowersCount, userFullName,username, alertok);

		saveDailyDetails();
	}

	public List<String> differenciate(List<String> a, List<String> b) {

		// difference a-b
		List<String> c = new ArrayList<String>(a.size());
		c.addAll(a);
		c.removeAll(b);

		return c;
	}

	public <T> List<T> intersection(List<T> list1, List<T> list2) {

		List<T> list = new ArrayList<T>();

		for (T t : list1) {

			if (list2.contains(t)) {

				list.add(t);

			}

		}

		return list;
	}

	@Override
	public void onBackPressed() {

		// super.onBackPressed();

		if (webDialog != null) {

			if (webDialog.isShowing()) {

				webDialog.dismiss();

			}

		} else {

			backCount++;

			if (backCount == 2) {

				System.out.println(" EXIT backCount " + backCount);

				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);

			} else {
				myToastS("Press again to exit");
			}

			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					backCount = 0;
				}
			}, 2000);

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
				new ColorDrawable(Color.TRANSPARENT));

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

				MainSingleTon.resetSigleTon();

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

	public void loadMoreFollowers() {
	}

	public void loadMoreFollowings() {

		myprint("@@@@@@@ loadMoreFollowings @@@@@@@@");

		TwitterUserGETRequest userGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

			@Override
			public void onSuccess(JSONObject jsonObject) {

			}

			@Override
			public void onSuccess(String jsonResult) {

				myprint("jsonResult" + jsonResult);

				JSONArray jsonArray;

				MainSingleTon.toFollowingModelsIDs.clear();

				myprint("MainSingleTon.toFollowingModelsIDs.size"
						+ MainSingleTon.toFollowingModelsIDs.size());

				try {

					JSONObject jsonObject = new JSONObject(jsonResult);

					jsonArray = new JSONArray(jsonObject
							.getString("ids"));

					for (int i = 0; i < jsonArray.length(); ++i) {

						MainSingleTon.toFollowingModelsIDs
								.add(jsonArray.getString(i).trim());

					}

				} catch (JSONException e) {

					e.printStackTrace();
				}

				myprint("MainSingleTon.toFollowingModelsIDs.size"
						+ MainSingleTon.toFollowingModelsIDs.size());

				MainSingleTon.secondaryCountLoaded = true;

				determineEntitiesCounts();

				isFirstTimeCountsChecked = true;

				isNeedToRefreshDrawer = true;

				// ...................................................

				Intent intent = new Intent(MainSingleTon.broadcataction);

				if (MainSingleTon.mutualsIds.size() > 0
						&& MainSingleTon.autodm) {

					myprint(" ********** AutoDM is ON ************ ");

					MainActivity.this.sendBroadcast(intent);

				} else {

					myprint(" ********** AutoDM is OFF ************ ");

				}

				// ..................................................

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

}
