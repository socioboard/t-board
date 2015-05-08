package com.socioboard.t_board_pro;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.socioboard.t_board_pro.twitterapi.TwitterAccessTokenPost;
import com.socioboard.t_board_pro.twitterapi.TwitterSignIn;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TwtboardproLocalData;
import com.socioboard.tboardpro.R;

public class WelcomeActivity extends Activity {

	ImageView loginButton;

	TwtboardproLocalData twiterManyLocalData;
	public String requestAccessToken, requestAccessSecret;
	boolean callBackConfirm = false;
	Dialog webDialog;
	WebView webView;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		progressDialog = new ProgressDialog(WelcomeActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		setContentView(R.layout.activity_welcome);

		loginButton = (ImageView) findViewById(R.id.login_button);

		twiterManyLocalData = new TwtboardproLocalData(getApplicationContext());

		myprint("onCreate  ");

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				progressDialog.setMessage("Twitter SignIn process..");

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

		twiterManyLocalData.deleteThisUserData(MainSingleTon.currentUserModel
				.getUserid());

		twiterManyLocalData.addNewUserAccount(MainSingleTon.currentUserModel);

		Editor editor = getSharedPreferences("twtboardpro",
				Context.MODE_PRIVATE).edit();

		editor.putString("userid", MainSingleTon.currentUserModel.getUserid());

		myprint("editor " + editor.commit());

		Intent in = new Intent(WelcomeActivity.this, MainActivity.class);
		startActivity(in);
		WelcomeActivity.this.finish();

	}

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

		Editor editor = getSharedPreferences("twtboardpro",
				Context.MODE_PRIVATE).edit();

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

				webDialog.setCancelable(false);

				String webLoadSignInUrl = MainSingleTon.signInRequestURL
						+ requestAccessToken;

				myprint("webLoadSignInUrl = " + webLoadSignInUrl);

				webView = (WebView) webDialog
						.findViewById(R.id.dialogue_web_view);

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
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			// Let the system handle the back button
			super.onBackPressed();
		}
	}

	class MyWebClient extends WebViewClient {

		private String TAG = "tag";

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			Log.d(TAG, "Redirecting URL " + url);

			if (url.startsWith(MainSingleTon.oauth_callbackURL)) {

				myprint("final response to get tokens " + url);

				String url1 = url.replace("http://globussoft.com/?", "");
				String[] tokenarray = url1.split("&");
				String[] oauthtokenrray = tokenarray[0].split("=");
				String[] oauthverifier = tokenarray[1].split("=");
				webDialog.dismiss();
				System.out.println("@@@@@@@@@@@@@   " + oauthtokenrray[1]
						+ "++++++++++++  " + oauthverifier[1]);
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

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			Log.d(TAG, "onPageFinished URL: " + url);

			myprint("onPageFinished title " + view.getTitle());

		}

	}

	public interface OAuthDialogListener {
		public abstract void onComplete(String accessToken);

		public abstract void onError(String error);
	}

	void myToastS(final String toastMsg) {

		Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG)
				.show();
	}

	void myToastL(final String toastMsg) {

		Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG)
				.show();
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
