package com.socioboard.t_board_pro.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.socioboard.t_board_pro.SplashActivity;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.tboardpro.R;

public class FollowersNotificationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {

		System.out.println("++++++++++++++++++++++++++++++++++  FollowersNotificationReceiver  +++++++++++++++++++  ");

		final TboardproLocalData tboardproLocalData = new TboardproLocalData(
				context);

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"twtboardpro", Context.MODE_PRIVATE);

		final String userId = sharedPreferences.getString("userid", null);

		if (userId != null) {

			final ModelUserDatas userDatas = tboardproLocalData
					.getUserData(userId);

			myprint(userDatas);

			// Tweet this post

			TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
					userDatas, new TwitterRequestCallBack() {

						@Override
						public void onSuccess(JSONObject jsonObject) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(String jsonResult) {
							// TODO Auto-generated method stub

							myprint("onSuccess jsonResult");

							ArrayList<String> listMyfollowersIDs = new ArrayList<String>();

							try {

								JSONObject jsonObject = new JSONObject(
										jsonResult);

								JSONArray jsonArray;

								jsonArray = new JSONArray(jsonObject
										.getString("ids"));

								for (int i = 0; i < jsonArray.length(); ++i) {

									listMyfollowersIDs.add(jsonArray
											.getString(i));

								}

								ArrayList<String> oldIds = new ArrayList<String>();

								try {

									JSONObject jsonObjectTMp = new JSONObject(
											tboardproLocalData
													.getAllFollowersIDs(userDatas
															.getUserid()));

									JSONArray jsonArrayTmp;

									jsonArrayTmp = new JSONArray(jsonObjectTMp
											.getString("ids"));

									for (int i = 0; i < jsonArrayTmp.length(); ++i) {

										oldIds.add(jsonArrayTmp.getString(i));

									}

								} catch (JSONException e) {

									e.printStackTrace();
								}

								ArrayList<String> tmpIds = (ArrayList<String>) differenciate(
										listMyfollowersIDs, oldIds);

								if (tmpIds.size() > 0) {

									myprint("*******   RCENT FOLLoWERS *******"
											+ tmpIds.size());

									notifyIt("You Have \"" + tmpIds.size()
											+ "\" Recent Followers", context);

								} else {

									myprint("******* Not RCENT FOLLOWERS *******");
									
									notifyIt("No New Followers Today", context);

								}

								tboardproLocalData.deleteAllFollowers(userId);

								tboardproLocalData.addFollwersIds(jsonResult,
										userId);

							} catch (JSONException e) {

								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(Exception e) {

							// TODO Auto-generated method stub

							myprint("onFailure Exception " + e);

							if (e.toString().contains("429")) {

							} else {

								// * * * * * * ** * * * * *

								AlarmManager alarmManagers;

								alarmManagers = (AlarmManager) context
										.getSystemService(context.ALARM_SERVICE);

								Intent myIntent = new Intent(context,
										FollowersNotificationReceiver.class);

								PendingIntent pendingIntent = PendingIntent
										.getBroadcast(
												context,
												5,
												myIntent,
												PendingIntent.FLAG_UPDATE_CURRENT);

								alarmManagers.set(AlarmManager.RTC_WAKEUP,
										System.currentTimeMillis() + 3600000,
										pendingIntent);

								// * * * * * * ** * * * * *
							}
						}
					});

			String url = MainSingleTon.users_following_to_me_Ids;

			List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

			peramPairs.add(new BasicNameValuePair(Const.cursor, "-1"));

			peramPairs.add(new BasicNameValuePair(Const.count, "5000"));

			twitterUserGETRequest.executeThisRequest(url, peramPairs);

		} else {
 			
		}

	}

	public List<String> differenciate(List<String> a, List<String> b) {

		// difference a-b
		List<String> c = new ArrayList<String>(a.size());
		c.addAll(a);
		c.removeAll(b);

		return c;
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	void notifyIt(String notDetails, Context context) {

		Intent intent1 = new Intent(context, SplashActivity.class);

		intent1.setAction(Intent.ACTION_MAIN);

		intent1.addCategory(Intent.CATEGORY_LAUNCHER);

		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent1,
				0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context);

		mBuilder.setLargeIcon(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.ic_launcher));

		mBuilder.setSmallIcon(R.drawable.notiicon);

		mBuilder.setAutoCancel(true);

		mBuilder.setTicker("Hey! you got new Followers.");

		mBuilder.setContentIntent(pIntent);

		mBuilder.setContentTitle("Recent Followers");

		mBuilder.setContentText(notDetails);

		Uri alarmSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		mBuilder.setSound(alarmSound);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(0, mBuilder.build());

	}

}
