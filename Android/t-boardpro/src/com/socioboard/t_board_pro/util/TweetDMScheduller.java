package com.socioboard.t_board_pro.util;

import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.StartDM;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TweetDMScheduller extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {

		if (MainActivity.isNeedToSendbroadCast) {

			MainActivity.isNeedToSendbroadCast = false;

			myprint("***********  TweetDMScheduller **************");

			new Thread(new Runnable() {

				@Override
				public void run() {

					myprint("********** StartDM ");

					StartDM startDM = new StartDM(context,
							MainSingleTon.currentUserModel);

					startDM.analyseTarget();

					startDM.startSendingMessages();

				}

			}).start();

		} else {

			myprint("No Dont second timeeeeeeeeeeeeeeee");
		}
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

}
