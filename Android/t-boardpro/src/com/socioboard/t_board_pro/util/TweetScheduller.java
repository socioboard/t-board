package com.socioboard.t_board_pro.util;

 
import com.socioboard.t_board_pro.SplashActivity;
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

public class TweetScheduller extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		System.out.println("++++++++++++++++++++++++++++++++++  TweetScheduller  +++++++++++++++++++");

		Intent intent1 = new Intent(context, SplashActivity.class);

		intent1.setAction(Intent.ACTION_MAIN);

		intent1.addCategory(Intent.CATEGORY_LAUNCHER);

		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent1,0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

		mBuilder.setLargeIcon(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.ic_launcher));

		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		
		mBuilder.setAutoCancel(true);

		mBuilder.setTicker("Tweet composed");

		mBuilder.setContentIntent(pIntent);

		mBuilder.setContentTitle("Schedulled Tweet");

		mBuilder.setContentText("Status : dvswreer  ref refe er ere ree r");

		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		mBuilder.setSound(alarmSound);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(0, mBuilder.build());
		
	}
}
