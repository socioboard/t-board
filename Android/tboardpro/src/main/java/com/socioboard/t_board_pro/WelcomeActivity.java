package com.socioboard.t_board_pro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.socioboard.t_board_pro.adapters.Viewpageradapter;
import com.socioboard.t_board_pro.twitterapi.TwitterAccessTokenPost;
import com.socioboard.t_board_pro.twitterapi.TwitterSignIn;
import com.socioboard.t_board_pro.util.IntroViewPagerModel;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;
import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;

public class WelcomeActivity extends Activity {


	ImageView loginButton;

	TboardproLocalData twiterManyLocalData;

	public String requestAccessToken, requestAccessSecret;

	boolean callBackConfirm = false;

	Dialog webDialog;

	WebView webView;

	ProgressDialog progressDialog;

	ProgressBar webViewProgress;

	PageIndicator indicator;

	Viewpageradapter viewpageradapter;

	ArrayList<IntroViewPagerModel> arrayList = new ArrayList<IntroViewPagerModel>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		progressDialog = new ProgressDialog(WelcomeActivity.this);

		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		progressDialog.setIndeterminate(true);

		progressDialog.setCancelable(false);

		ViewPager pager = (ViewPager) findViewById(R.id.pager);

		indicator = (PageIndicator) findViewById(R.id.indicator);

		loginButton = (ImageView) findViewById(R.id.loginbtn);

		IntroViewPagerModel model = new IntroViewPagerModel();
		model.setDrawable(R.drawable.intro_screen1);
		model.setIntro_text(MainSingleTon.introtext1);
		arrayList.add(model);

		IntroViewPagerModel model2 = new IntroViewPagerModel();
		model2.setDrawable(R.drawable.intro_screen2);
		model2.setIntro_text(MainSingleTon.introtext2);
		arrayList.add(model2);

		IntroViewPagerModel model3 = new IntroViewPagerModel();
		model3.setDrawable(R.drawable.intro_screen3);
		model3.setIntro_text(MainSingleTon.introtext3);
		arrayList.add(model3);

		IntroViewPagerModel model4 = new IntroViewPagerModel();
		model4.setDrawable(R.drawable.intro_screen4);
		model4.setIntro_text(MainSingleTon.introtext4);
		arrayList.add(model4);

		IntroViewPagerModel model5 = new IntroViewPagerModel();
		model5.setDrawable(R.drawable.intro_screen5);
		model5.setIntro_text(MainSingleTon.introtext5);
		arrayList.add(model5);

		IntroViewPagerModel model6 = new IntroViewPagerModel();
		model6.setDrawable(R.drawable.intro_screen6);
		model6.setIntro_text(MainSingleTon.introtext6);
		arrayList.add(model6);

		IntroViewPagerModel model7 = new IntroViewPagerModel();
		model7.setDrawable(R.drawable.intro_screen7);
		model7.setIntro_text(MainSingleTon.introtext7);
		arrayList.add(model7);

		viewpageradapter = new Viewpageradapter(getApplicationContext(), arrayList);

		pager.setAdapter(viewpageradapter);

		indicator.setViewPager(pager);

		twiterManyLocalData = new TboardproLocalData(getApplicationContext());

		myprint("onCreate  ");

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				progressDialog.setMessage("Signing in to Twitter..");

				progressDialog.show();

				new GetReqToken().execute();

			}
		});

		CookieSyncManager.createInstance(getApplicationContext());

		CookieManager cookieManager = CookieManager.getInstance();

		cookieManager.removeAllCookie();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		// Pass the activity result to the login button.

	}

	void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	public void setDetailsAccessTokens() {

		myprint("setDetailsAccessTokens");

		twiterManyLocalData.deleteThisUserData(MainSingleTon.currentUserModel.getUserid());

		twiterManyLocalData.addNewUserAccount(MainSingleTon.currentUserModel);

		SharedPreferences preferences = getSharedPreferences("twtboardpro", Context.MODE_PRIVATE);

		Editor editor = preferences.edit();

		editor.putString("userid", MainSingleTon.currentUserModel.getUserid());

		myprint("editor " + editor.commit());

		Intent in = new Intent(WelcomeActivity.this, SplashActivity.class);

		startActivity(in);

		WelcomeActivity.this.finish();

	}

	public class GetReqToken extends AsyncTask<Void, Void, String>
	{

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

			if (responseTokens == null) {

				myToastL("Sorry Unable to process");

				hideProgress();

			} else {

				extractBaseString(responseTokens);

			}

		}

	}

	public class GetAccessToken extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			progressDialog.setMessage("Almost completed..");

			showProgress();

			TwitterAccessTokenPost twitterSignIn = new TwitterAccessTokenPost();

			String responseTokens = twitterSignIn.postForAccessToken(params[0], params[1]);

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

	void extractBaseString(String baseString) {

		// Token
		int startInd = baseString.indexOf("=") + 1, endInd = baseString.indexOf("&");

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

		Editor editor = getSharedPreferences("twtboardpro", Context.MODE_PRIVATE).edit();

		editor.putString("oauth_token", requestAccessToken);

		editor.putString("oauth_token_secret", requestAccessSecret);

		myprint("editor " + editor.commit());

		loadSignInWebView();

	}

	void extractAccesTokenSecret(String baseString) {

		MainSingleTon.currentUserModel = new ModelUserDatas();

		// ..................................................

		String[] array1 = baseString.split("&");

		String[] arrayaccessToken = array1[0].split("=");
		String[] arrayTokenSecret = array1[1].split("=");
		String[] arrayUserID = array1[2].split("=");
		String[] arrayScreenName = array1[3].split("=");

		MainSingleTon.currentUserModel.setUserAcessToken(arrayaccessToken[1]);

		MainSingleTon.currentUserModel.setUsersecretKey(arrayTokenSecret[1]);

		MainSingleTon.currentUserModel.setUserid(arrayUserID[1]);

		MainSingleTon.currentUserModel.setUsername(arrayScreenName[1]);

		// .................................................

		myprint(MainSingleTon.currentUserModel);

		setDetailsAccessTokens();
	}


	public void loadSignInWebView() {

		hideProgress();

		new Handler().post(new Runnable() {

			@Override
			public void run() {

				webDialog = new Dialog(WelcomeActivity.this);

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

				String webLoadSignInUrl = MainSingleTon.signInRequestURL + requestAccessToken;

				myprint("webLoadSignInUrl = " + webLoadSignInUrl);

				webView = (WebView) webDialog.findViewById(R.id.dialogue_web_view);

				webViewProgress = (ProgressBar) webDialog.findViewById(R.id.progressBar1);

				webView.setWebViewClient(new MyWebClient());
				webView.setVerticalScrollBarEnabled(false);
				webView.setHorizontalScrollBarEnabled(false);
				webView.getSettings().setJavaScriptEnabled(true);
				webView.loadUrl(webLoadSignInUrl);
				webDialog.show();

			}
		});
	}

	@Override
	// Detect when the back button is pressed
	public void onBackPressed() {
		super.onBackPressed();

		if (webDialog != null) {

			if (webDialog.isShowing()) {

				webDialog.dismiss();

			}
		}

	}

	class MyWebClient extends WebViewClient
	{

		private String TAG = "tag";

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			Log.d(TAG, "Redirecting URL" + url);

			if (url.startsWith(MainSingleTon.oauth_callbackURL)) {

				myprint("final response to get tokens " + url);

				String url1 = url.replace(MainSingleTon.oauth_callbackURL + "?", "");

				String[] tokenarray = url1.split("&");

				myprint("After Replacement " + url1);

				if (tokenarray.length == 0) {

					webDialog.dismiss();

					myprint("tokenarray.length == 0 ");

					myprint("Failed!!!!!!!!");

				}

				try {

					String[] oauthtokenrray = tokenarray[0].split("=");

					String[] oauthverifier = tokenarray[1].split("=");

					System.out.println("@@@@@@@@@@@@@   " + oauthtokenrray[1] + "++++++++++++  " + oauthverifier[1]);

					new GetAccessToken().execute(oauthtokenrray[1], oauthverifier[1]);

				} catch (Exception exception) {

					System.out.println("ex" + exception);

				}

				webView.destroy();

				webDialog.dismiss();

				return true;
			}

			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

			Log.d(TAG, "Page error: " + description);

			super.onReceivedError(view, errorCode, description, failingUrl);

			webViewProgress.setVisibility(View.INVISIBLE);

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

			webViewProgress.setVisibility(View.VISIBLE);

			if (url.startsWith("https://twitter.com/login/error?")) {

				new AlertDialog.Builder(WelcomeActivity.this).setTitle("SignIn failed!")
						.setMessage(
								"The username and password you entered did not match our records. Please double-check and try again.")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

								webDialog.dismiss();
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show().setCancelable(false);

			} else {

			}

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			Log.d(TAG, "onPageFinished URL: " + url);

			webViewProgress.setVisibility(View.INVISIBLE);

			myprint("onPageFinished title " + view.getTitle());

		}

	}





	void myToastS(final String toastMsg) {

		Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
	}

	void myToastL(final String toastMsg) {

		Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
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
