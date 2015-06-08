package com.socioboard.t_board_pro;

import com.flurry.android.FlurryAgent;

public class Application extends android.app.Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		// configure Flurry
		FlurryAgent.setLogEnabled(true);

		// init Flurry
		FlurryAgent.init(this, "JPQF3XP5QY7S9S3TKXFT");
	}

}
