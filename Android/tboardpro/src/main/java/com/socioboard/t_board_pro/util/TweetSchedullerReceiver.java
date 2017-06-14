package com.socioboard.t_board_pro.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.socioboard.t_board_pro.SplashActivity;
import com.socioboard.t_board_pro.fragments.FragmentSchedule;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestPerams;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.tboardpro.R;

public class TweetSchedullerReceiver extends BroadcastReceiver {

	MyBadPaddingException e = new MyBadPaddingException();

	@Override
	public void onReceive(Context context, Intent intent) {

		int getResponseCode;

		initUserProfile();

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

				TwitterPostRequestPerams postRequestPerams = new TwitterPostRequestPerams(
						schTweetModel.getUserDatas(),
						new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {

							}

							@Override
							public void onSuccess(String jsonResult) {

								myprint("onSuccess jsonResult");

							}

							@Override
							public void onFailure(Exception e) {

								myprint("onFailure Exception " + e);

							}
						});

				String url = MainSingleTon.updateTweet;

				String NotifyTweet = "";

				List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

				if (schTweetModel.getTweet().startsWith(
						"in_reply_to_status_id=@@")) {

					String original = schTweetModel.getTweet(), finals, tmp;

					tmp = original.split("in_reply_to_status_id=@@")[1];

					System.out.println("tmp String " + tmp);

					int last_index = tmp.indexOf("@@");

					finals = tmp.substring(0, last_index);

					System.out.println("status id " + finals);

					String replyStatus = tmp.substring(last_index + 2);

					System.out.println("replyStatus   " + replyStatus);

					peramPairs.add(new BasicNameValuePair(Const.status, ""
							+ replyStatus));

					peramPairs.add(new BasicNameValuePair(
							Const.in_reply_to_status_id, finals));

					NotifyTweet = replyStatus;

				} else if (schTweetModel.getTweet().startsWith(
						"retweet_to_status_id=@@")) {

					String original = schTweetModel.getTweet(), finals, tmp;

					tmp = original.split("retweet_to_status_id=@@")[1];

					System.out.println("tmp String " + tmp);

					int last_index = tmp.indexOf("@@");

					finals = tmp.substring(0, last_index);

					System.out.println("Final status id " + finals);

					url = MainSingleTon.reTweeting + finals + ".json";

					peramPairs.add(new BasicNameValuePair("id", "" + finals));
				
					String retweetedString = tmp.substring(last_index + 2);

					System.out.println("retweetedString   " + retweetedString);

					NotifyTweet = retweetedString;

				} else {

					peramPairs.add(new BasicNameValuePair(Const.status,
							schTweetModel.getTweet()));

					NotifyTweet = schTweetModel.getTweet();

				}

				postRequestPerams.executeThisRequest(url, peramPairs);

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

				mBuilder.setContentText("Status: " + NotifyTweet);

				Uri alarmSound = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

				mBuilder.setSound(alarmSound);

				NotificationManager mNotificationManager = (NotificationManager) context
						.getSystemService(context.NOTIFICATION_SERVICE);

				mNotificationManager.notify(0, mBuilder.build());

				tboardproLocalData.deleteThisTweet(getResponseCode);

				FragmentSchedule.isNeedToUpdateUI = true;

			}

		} else {

			tboardproLocalData.deleteThisTweet(getResponseCode);

			FragmentSchedule.isNeedToUpdateUI = true;

		}

	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

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
