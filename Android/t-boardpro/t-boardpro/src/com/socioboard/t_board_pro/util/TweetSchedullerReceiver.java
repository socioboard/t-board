package com.socioboard.t_board_pro.util;

import org.json.JSONObject;

import com.socioboard.t_board_pro.SplashActivity;
import com.socioboard.t_board_pro.fragments.FragmentSchedule;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestTweet;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.tboardpro.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class TweetSchedullerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		int getResponseCode;

		getResponseCode = intent.getIntExtra(Const.RES_CODE, 404);

		System.out
				.println("++++++++++++++++++++++++++++++++++  TweetScheduller  +++++++++++++++++++ getResponseCode"
						+ getResponseCode);

		TboardproLocalData tboardproLocalData = new TboardproLocalData(context);

		SchTweetModel schTweetModel = tboardproLocalData.getSchedulledTweet(""
				+ getResponseCode);

		ModelUserDatas userDatas = tboardproLocalData.getUserData(schTweetModel
				.getUserID());

		if (userDatas != null) {

			schTweetModel.setUserDatas(userDatas);

			if (schTweetModel != null) {

				myprint(schTweetModel);

				// Tweet this post

				TwitterPostRequestTweet postRequestTweet = new TwitterPostRequestTweet(
						schTweetModel.getUserDatas(),
						new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onSuccess(String jsonResult) {
								// TODO Auto-generated method stub
								myprint("onSuccess jsonResult");
							}

							@Override
							public void onFailure(Exception e) {
								// TODO Auto-generated method stub
								myprint("onFailure Exception " + e);
							}

						});

				postRequestTweet.executeThisRequest(schTweetModel.getTweet());

				// Notify It!!

				Intent intent1 = new Intent(context, SplashActivity.class);

				intent1.setAction(Intent.ACTION_MAIN);

				intent1.addCategory(Intent.CATEGORY_LAUNCHER);

				PendingIntent pIntent = PendingIntent.getActivity(context, 0,
						intent1, 0);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context);

				mBuilder.setLargeIcon(BitmapFactory.decodeResource(
						context.getResources(), R.drawable.ic_launcher));

				mBuilder.setSmallIcon(R.drawable.notiicon);

				mBuilder.setAutoCancel(true);

				mBuilder.setTicker("Tweet composed");

				mBuilder.setContentIntent(pIntent);

				mBuilder.setContentTitle("Scheduled Tweet composed");

				mBuilder.setContentText("Status:" + schTweetModel.getTweet());

				Uri alarmSound = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

				mBuilder.setSound(alarmSound);

				NotificationManager mNotificationManager = (NotificationManager) context
						.getSystemService(context.NOTIFICATION_SERVICE);

				mNotificationManager.notify(0, mBuilder.build());

				tboardproLocalData.deleteThisTweet(getResponseCode);

				FragmentSchedule.isNeedToUpdateUI = true;

			}

		}

	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}
}
