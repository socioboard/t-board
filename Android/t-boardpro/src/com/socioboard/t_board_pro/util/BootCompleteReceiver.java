package com.socioboard.t_board_pro.util;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {

	TboardproLocalData tboardproLocalData;

	@Override
	public void onReceive(Context context, Intent intent) {

		System.out.println("*** socioboard BootCompleteReceiver ***");

		tboardproLocalData = new TboardproLocalData(context);

		// **************************************

		AlarmManager alarmManagers;

		alarmManagers = (AlarmManager) context
				.getSystemService(context.ALARM_SERVICE);

		Intent myIntent = new Intent(context,
				FollowersNotificationReceiver.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 465,
				myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.DATE, 1);

		calendar.set(Calendar.HOUR_OF_DAY, 9);

		calendar.set(Calendar.MINUTE, 0);

		calendar.set(Calendar.SECOND, 0);

		alarmManagers.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), 43200000, pendingIntent);

		System.out.println("Notification Schedulle Calender is " + calendar);

		// **************************************

	}

}
