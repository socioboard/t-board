package com.socioboard.t_board_pro;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.appnext.appnextsdk.AppnextTrack;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserShowRequest;
import com.socioboard.t_board_pro.util.FollowersNotificationReceiver;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.MyBadPaddingException;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class SplashActivity extends Activity {

	// Local DataBase

	TboardproLocalData twiterManyLocalData;

	SharedPreferences preferences;

	MyBadPaddingException e = new MyBadPaddingException();

	Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		// initialize Your Twitter Keys Here
		// .............................................

		// .............................................

		AppnextTrack.track(this);

		twiterManyLocalData = new TboardproLocalData(getApplicationContext());

		twiterManyLocalData.CreateTable();

		MainSingleTon.resetSigleTon();

		MainSingleTon.allUserdetails = twiterManyLocalData.getAllUsersData();

		preferences = getSharedPreferences("twtboardpro", Context.MODE_PRIVATE);

		editor = getSharedPreferences("twtboardpro", Context.MODE_PRIVATE).edit();

		boolean isTwitterKeyAssigned = false;

		isTwitterKeyAssigned = preferences.getBoolean(MainSingleTon.isTwitterKeyAssigned, false);

		if (isTwitterKeyAssigned) {

			initializeTwitterkeys(preferences.getString(MainSingleTon.T_KEY, ""),
					preferences.getString(MainSingleTon.T_SECRET, ""),
					preferences.getString(MainSingleTon.T_oauth_callbackURL, ""));

			if (MainSingleTon.allUserdetails.size() == 0) {

				System.out.println(MainSingleTon.allUserdetails.size() + " first time");

				Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);

				startActivity(intent);

				finish();

			} else {

				// last activated User

				MainSingleTon.autodm = preferences.getBoolean("autodm", false);

				MainSingleTon.autoDmfirstime = preferences.getString("autoDmfirstime", "yes");

				System.out.println("autodm  = " + MainSingleTon.autodm);

				System.out.println("autoDmfirstime  = " + MainSingleTon.autoDmfirstime);

				String userId = preferences.getString("userid", null);

				if (userId != null) {

					MainSingleTon.currentUserModel = MainSingleTon.allUserdetails.get(userId);

					System.out.println(MainSingleTon.currentUserModel + " currentUserModel");
					twiterManyLocalData.getWhiteList(MainSingleTon.currentUserModel.getUserid());
					twiterManyLocalData.getBlackList(MainSingleTon.currentUserModel.getUserid());
					Intent in = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(in);
					SplashActivity.this.finish();

				} else {

					editor.putString("userid", MainSingleTon.currentUserModel.getUserid());

					editor.commit();

					Map.Entry<String, ModelUserDatas> entry = MainSingleTon.allUserdetails.entrySet().iterator().next();

					userId = entry.getKey();

					MainSingleTon.currentUserModel = MainSingleTon.allUserdetails.get(userId);

					Intent in = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(in);
					SplashActivity.this.finish();

				}

				String dateStr = preferences.getString("date", "***");

				System.out.println("dateStr " + dateStr); // 2014/08/06 15:59:48

				if (dateStr.contains("***")) {

					//
					System.out.println("*************** Schedulle it FirstTime ********* " + dateStr);

					// 2014/08/06

					// 15:59:48

					// **************************************

					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

					Date date = new Date();

					System.out.println(dateFormat.format(date)); // 2014/08/06
																	// 15:59:48
					editor.putString("date", dateFormat.format(date));

					editor.commit();

					AlarmManager alarmManagers;

					alarmManagers = (AlarmManager) getApplicationContext()
							.getSystemService(getApplicationContext().ALARM_SERVICE);

					Intent myIntent = new Intent(SplashActivity.this, FollowersNotificationReceiver.class);

					PendingIntent pendingIntent = PendingIntent.getBroadcast(SplashActivity.this, 465, myIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					Calendar calendar = Calendar.getInstance();

					calendar.add(Calendar.DATE, 1);

					calendar.set(Calendar.HOUR_OF_DAY, 9);

					calendar.set(Calendar.MINUTE, 0);

					calendar.set(Calendar.SECOND, 0);

					alarmManagers.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 43200000,
							pendingIntent);

					System.out.println("Notification Schedulle Calender is " + calendar);

					// **************************************

				} else {

					System.out.println(" Not dont set It second  time ");

				}

			}
		} else {

			Handler handler = new Handler();

			handler.postDelayed(new Runnable() {

				@Override
				public void run() {

					new android.app.AlertDialog.Builder(SplashActivity.this).setTitle("Application setup")
							.setMessage("You need to configure Twitter API keys in order to use all Functionalities.")
							.setPositiveButton("Next", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							initKeyDialog();
						}
					}).setIcon(R.drawable.ic_launcher).show();

				}
			}, 1000);

		}

	}

	private void initKeyDialog() {

		final int sdk = android.os.Build.VERSION.SDK_INT;

		final Dialog dialogIntkey = new Dialog(SplashActivity.this);

		dialogIntkey.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialogIntkey.setContentView(R.layout.apikeydialog);

		dialogIntkey.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

		Window window = dialogIntkey.getWindow();

		lp.copyFrom(window.getAttributes());

		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		window.setAttributes(lp);

		dialogIntkey.getWindow().setBackgroundDrawable(new ColorDrawable(0));

		dialogIntkey.setCancelable(false);

		final EditText editText1Key;

		final EditText editText1Secret;

		final EditText editTextCallbcak;

		editText1Key = (EditText) dialogIntkey.findViewById(R.id.editText1Key);

		editText1Secret = (EditText) dialogIntkey.findViewById(R.id.editText1Secret);

		editTextCallbcak = (EditText) dialogIntkey.findViewById(R.id.editTextCallbcak);

		Button buttonPaste1, buttonPaste2, buttonPaste3;

		ImageView imageView2Info = (ImageView) dialogIntkey.findViewById(R.id.imageView2Info);

		imageView2Info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				infoDialog();

			}
		});

		buttonPaste1 = (Button) dialogIntkey.findViewById(R.id.buttonPaste1);

		buttonPaste2 = (Button) dialogIntkey.findViewById(R.id.buttonPaste2);

		buttonPaste3 = (Button) dialogIntkey.findViewById(R.id.buttonPaste3);

		buttonPaste1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String pasteText;

				// TODO Auto-generated method stub

				if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {

					android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(
							Context.CLIPBOARD_SERVICE);

					pasteText = clipboard.getText().toString();

					editText1Key.append(pasteText);

				} else {

					ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

					if (clipboard.hasPrimaryClip() == true) {

						ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
						pasteText = item.getText().toString();
						editText1Key.append(pasteText);

					} else {

						Toast.makeText(getApplicationContext(), "Nothing to Paste", Toast.LENGTH_SHORT).show();

					}
				}

				// editText1Key.setText("JAb2y9lFdzwDqp5fLSOt6vXVY");

			}
		});

		buttonPaste2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String pasteText;

				if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {

					android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(
							Context.CLIPBOARD_SERVICE);

					pasteText = clipboard.getText().toString();

					editText1Secret.append(pasteText);

				} else {

					ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

					if (clipboard.hasPrimaryClip() == true) {

						ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
						pasteText = item.getText().toString();
						editText1Secret.append(pasteText);
					} else {

						Toast.makeText(getApplicationContext(), "Nothing to Paste", Toast.LENGTH_SHORT).show();
					}
				}
				// editText1Secret.setText("k5KcIkbwnILryjLZJ19Pis92PuykuiOSerHHtMtyelLarDiVZ7");

			}
		});

		buttonPaste3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String pasteText;

				if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {

					android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(
							Context.CLIPBOARD_SERVICE);

					pasteText = clipboard.getText().toString();

					editTextCallbcak.append(pasteText);

				} else {

					ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

					if (clipboard.hasPrimaryClip() == true) {

						ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
						pasteText = item.getText().toString();
						editTextCallbcak.append(pasteText);

					} else {

						Toast.makeText(getApplicationContext(), "Nothing to Paste", Toast.LENGTH_SHORT).show();

					}

				}


			}
		});

		RelativeLayout relativeLayout = (RelativeLayout) dialogIntkey.findViewById(R.id.reloutbottom);

		RelativeLayout reloutbottom2UseDefaults = (RelativeLayout) dialogIntkey
				.findViewById(R.id.reloutbottom2UseDefaults);

		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean isEverythingOk1 = true, isEverythingOk2 = true, isEverythingOk3 = true;

				if (editText1Key.getText().toString().isEmpty()) {

					editText1Key.setError("Cannot be Empty");

					isEverythingOk1 = false;
				}

				if (editText1Secret.getText().toString().isEmpty()) {

					editText1Secret.setError("Cannot be Empty");

					isEverythingOk2 = false;
				}

				if (editTextCallbcak.getText().toString().isEmpty()) {

					editTextCallbcak.setError("Cannot be Empty");

					isEverythingOk3 = false;
				}

				if (isEverythingOk1 && isEverythingOk2 && isEverythingOk3) {

					String apiKey = editText1Key.getText().toString();

					String apiSecret = editText1Secret.getText().toString();

					String CallbcakUrl = editTextCallbcak.getText().toString();

					if (isNetworkAvailable(getApplicationContext())) {

						verifyKeysAndSave(apiKey, apiSecret, CallbcakUrl,dialogIntkey);

					} else {
						myToastS("No Internet Connection");
						myToastS("Unable to verify Credentials");
					}

				}
			}

		});

		reloutbottom2UseDefaults.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {

				new android.app.AlertDialog.Builder(SplashActivity.this).setTitle("Warning!")
						.setMessage(
								"If you are using default keys, Sometimes you may get problem in getting Data from Twitter.")
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						editor.putBoolean(MainSingleTon.isTwitterKeyAssigned, true);

						editor.putString(MainSingleTon.T_KEY, "xxxxxxxxxxxxxxxxxxxxxxxxxxxx");

						editor.putString(MainSingleTon.T_SECRET, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

						editor.putString(MainSingleTon.T_oauth_callbackURL, "https://www.socioboard.com");

						editor.commit();

						startActivity(new Intent(getApplicationContext(), SplashActivity.class));

						finish();

					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				}).setIcon(R.drawable.ic_dialog_alert_holo_light).show();

			}
		});

		dialogIntkey.show();

	}

	void myToastL(final String toastMsg) {

		Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();

	}

	void myToastS(final String toastMsg) {

		Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
	}

	public boolean isNetworkAvailable(Context activity) {

		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity == null) {

			return false;

		} else {

			NetworkInfo[] info = connectivity.getAllNetworkInfo();

			if (info != null) {

				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}

		}

		return false;
	}

	Handler handler = new Handler();

	protected void verifyKeysAndSave(final String apiKey, final String apiSecret, final String CallbcakUrl, final Dialog dialogIntkey) {

		final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Verifying credentials...");
		progressDialog.show();

//		MainSingleTon.currentUserModel.setUserAcessToken("3902001134-0KHlNVQscxYxkgCSey5d5l0OzPnuqwvVDpnknDY");
//		MainSingleTon.currentUserModel.setUsersecretKey("278jYQWvgD4dfzVNMcOes9l9y403A3fBl97usgLNrcpIe");

		MainSingleTon.currentUserModel.setUserAcessToken("xxxxxxxxxx");
		MainSingleTon.currentUserModel.setUsersecretKey("xxxxxxxxxxxxx


		MainSingleTon.TWITTER_KEY = apiKey;
		MainSingleTon.TWITTER_SECRET = apiSecret;

		TwitterUserShowRequest userShowRequest = new TwitterUserShowRequest(MainSingleTon.currentUserModel,
				new TwitterRequestCallBack() {

					@Override
					public void onSuccess(JSONObject jsonObject) {

						System.out.println("jsonObject");
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.cancel();
								dialogIntkey.cancel();

							}
						});

						editor.putBoolean(MainSingleTon.isTwitterKeyAssigned, true);

						editor.putString(MainSingleTon.T_KEY, apiKey);

						editor.putString(MainSingleTon.T_SECRET, apiSecret);

						editor.putString(MainSingleTon.T_oauth_callbackURL, CallbcakUrl);

						editor.commit();

						startActivity(new Intent(getApplicationContext(), SplashActivity.class));

						finish();
					}

					@Override
					public void onSuccess(String jsonResult) {

						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.cancel();
								dialogIntkey.cancel();
							}
						});
						System.out.println("jsonObject");
						editor.putBoolean(MainSingleTon.isTwitterKeyAssigned, true);

						editor.putString(MainSingleTon.T_KEY, apiKey);

						editor.putString(MainSingleTon.T_SECRET, apiSecret);

						editor.putString(MainSingleTon.T_oauth_callbackURL, CallbcakUrl);

						editor.commit();

						startActivity(new Intent(getApplicationContext(), SplashActivity.class));

						finish();
					}

					@Override
					public void onFailure(Exception e) {

						System.out.println("Exception " + e);

						handler.post(new Runnable() {

							@Override
							public void run() {

								// TODO Auto-generated method stub

								progressDialog.cancel();

								new android.app.AlertDialog.Builder(SplashActivity.this).setTitle("Invalid Credentials")
										.setMessage("Please provide valid credentials")
										.setPositiveButton("Try Again!", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								}).setIcon(R.drawable.ic_dialog_alert_holo_light).show();

							}
						});
					}
				});

		userShowRequest.executeThisRequest("socioboard");

	}

	protected void infoDialog() {

		final Dialog dialogIntkey = new Dialog(SplashActivity.this);

		dialogIntkey.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialogIntkey.setContentView(R.layout.keyhelp);

		dialogIntkey.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

		Window window = dialogIntkey.getWindow();

		lp.copyFrom(window.getAttributes());

		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		window.setAttributes(lp);

		ImageView imageView2Info = (ImageView) dialogIntkey.findViewById(R.id.imageView1);

		imageView2Info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialogIntkey.cancel();
			}
		});

		dialogIntkey.getWindow().setBackgroundDrawable(new ColorDrawable(0));

		dialogIntkey.setCancelable(true);

		dialogIntkey.show();
	}

	private void initializeTwitterkeys(String consumerKey, String consumersecret, String CallbcakUrl) {

		MainSingleTon.TWITTER_KEY = consumerKey;

		MainSingleTon.TWITTER_SECRET = consumersecret;

		MainSingleTon.oauth_callbackURL = CallbcakUrl;

		System.out.println("MainSingleTon.TWITTER_KEY " + MainSingleTon.TWITTER_KEY);
		System.out.println("MainSingleTon.TWITTER_SECRET " + MainSingleTon.TWITTER_SECRET);
		System.out.println("MainSingleTon.oauth_callbackURL " + MainSingleTon.oauth_callbackURL);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		finish();
	}
}
