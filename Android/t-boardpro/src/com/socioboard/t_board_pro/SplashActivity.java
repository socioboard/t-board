package com.socioboard.t_board_pro;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.appnext.appnextsdk.AppnextTrack;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Encrypt;
import com.socioboard.t_board_pro.util.FollowersNotificationReceiver;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.MyBadPaddingException;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

public class SplashActivity extends Activity {

	// Local DataBase

	TboardproLocalData twiterManyLocalData;

	SharedPreferences preferences;

	MyBadPaddingException e = new MyBadPaddingException();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		// initialize Your Twitter Keys Here
		// .............................................

		initializeTwitterkeys("xxxxxxxxxxxxxxxxx", "xxxxxxxxxxxxxxxxxxxxxxxx");

		// .............................................
 		// AppnextTrack.track(this);

		twiterManyLocalData = new TboardproLocalData(getApplicationContext());

		twiterManyLocalData.CreateTable();

		MainSingleTon.resetSigleTon();

		MainSingleTon.allUserdetails = twiterManyLocalData.getAllUsersData();

		System.out.println();

		if (MainSingleTon.allUserdetails.size() == 0) {

			System.out.println(MainSingleTon.allUserdetails.size()
					+ " first time");

			Intent intent = new Intent(SplashActivity.this,
					WelcomeActivity.class);

			startActivity(intent);

			finish();

		} else {

			// last activated User

			preferences = getSharedPreferences("twtboardpro",
					Context.MODE_PRIVATE);

			Editor editor = getSharedPreferences("twtboardpro",
					Context.MODE_PRIVATE).edit();

			MainSingleTon.autodm = preferences.getBoolean("autodm", false);

			MainSingleTon.autoDmfirstime = preferences.getString(
					"autoDmfirstime", "yes");

			System.out.println("autodm  = " + MainSingleTon.autodm);

			System.out.println("autoDmfirstime  = "
					+ MainSingleTon.autoDmfirstime);

			String userId = preferences.getString("userid", null);

			if (userId != null) {

				MainSingleTon.currentUserModel = MainSingleTon.allUserdetails
						.get(userId);

				System.out.println(MainSingleTon.currentUserModel
						+ " currentUserModel");

				Intent in = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(in);
				SplashActivity.this.finish();

			} else {

				editor.putString("userid",
						MainSingleTon.currentUserModel.getUserid());

				editor.commit();

				Map.Entry<String, ModelUserDatas> entry = MainSingleTon.allUserdetails
						.entrySet().iterator().next();

				userId = entry.getKey();

				MainSingleTon.currentUserModel = MainSingleTon.allUserdetails
						.get(userId);

				Intent in = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(in);
				SplashActivity.this.finish();

			}

			String dateStr = preferences.getString("date", "***");

			System.out.println("dateStr " + dateStr); // 2014/08/06 15:59:48

			if (dateStr.contains("***")) {

				//
				System.out
						.println("*************** Schedulle it FirstTime ********* "
								+ dateStr);

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

				Intent myIntent = new Intent(SplashActivity.this,
						FollowersNotificationReceiver.class);

				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						SplashActivity.this, 465, myIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				Calendar calendar = Calendar.getInstance();

				calendar.add(Calendar.DATE, 1);

				calendar.set(Calendar.HOUR_OF_DAY, 9);

				calendar.set(Calendar.MINUTE, 0);

				calendar.set(Calendar.SECOND, 0);

				alarmManagers.setRepeating(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), 43200000, pendingIntent);

				System.out.println("Notification Schedulle Calender is "
						+ calendar);

				// **************************************

			} else {

				System.out.println(" Not dont set It second  time ");

			}

		}

	}

	private void initializeTwitterkeys(String consumerKey, String consumersecret) {

		MainSingleTon.TWITTER_KEY = consumerKey;

		MainSingleTon.TWITTER_SECRET = consumersecret;

	}

	void initUserProfile() {

		String myName = "BFEE7CD983AE97DCFEB9D3842184C9FB11F467FCAF7D8970D7AE56AF174221EB51278F50EABEAB4F348E29EB81884B9C";

		String myLastname = "38031C58B5E88505672EFC2239A50672C904277EA95FAC1AD20C1CC4FAC32E0EB4DE40D0F5B1D2D2065995E6D46D8190";

		String text1 = "Ym93aHVudGluZ3Bhc3N3b3JkMTIz";

		String myEncodedName;

		String myEncodedLastName;

		try {

			myEncodedName = Encrypt.decrypt(text1, myName);

		} catch (Exception ex) {

			e.printStackTraces();

		} finally {

		}

		try {

			myEncodedLastName = Encrypt.decrypt(text1, myLastname);

		} catch (Exception ex) {

			e.printStackraces();

		} finally {

		}

	}

}
