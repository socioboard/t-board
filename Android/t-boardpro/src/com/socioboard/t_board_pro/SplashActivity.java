package com.socioboard.t_board_pro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Base64;

import com.socioboard.t_board_pro.util.Encrypt;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

public class SplashActivity extends Activity {

	// Local DataBase
	TboardproLocalData twiterManyLocalData;

	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		twiterManyLocalData = new TboardproLocalData(getApplicationContext());

		twiterManyLocalData.CreateTable();

		MainSingleTon.allUserdetails = twiterManyLocalData.getAllUsersData();

		initUserProfile();

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

			String userId = preferences.getString("userid", null);

			if (userId != null) {

				MainSingleTon.currentUserModel = MainSingleTon.allUserdetails
						.get(userId);

				System.out.println(MainSingleTon.currentUserModel + " currentUserModel");

 				Intent in = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(in);
				SplashActivity.this.finish();

			} else {

				Editor editor = getSharedPreferences("twtboardpro",
						Context.MODE_PRIVATE).edit();

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

		}
		
		MainSingleTon.schedulecount = twiterManyLocalData.getAllSchedulledTweet().size();
		
		System.out.println("MainSingleTon.schedulecount "+MainSingleTon.schedulecount);

	}

	
	void initUserProfile() {

		String myName = "BFEE7CD983AE97DCFEB9D3842184C9FB11F467FCAF7D8970D7AE56AF174221EB51278F50EABEAB4F348E29EB81884B9C";

		String myLastname = "38031C58B5E88505672EFC2239A50672C904277EA95FAC1AD20C1CC4FAC32E0EB4DE40D0F5B1D2D2065995E6D46D8190";

		String text1 = "Ym93aHVudGluZ3Bhc3N3b3JkMTIz";

		String myEncodedName;

		String myEncodedLastName;

		try {

			myEncodedName = Encrypt.decrypt(text1, myName);

		} catch (Exception e) {
			e.printStackTrace();
			MainSingleTon.TWITTER_KEY = "oXPJHvlIX02UpEIu5K8EpKbdt";

		} finally {

		}

		try {

			myEncodedLastName = Encrypt.decrypt(text1, myLastname);

		} catch (Exception e) {

			e.printStackTrace();
			MainSingleTon.TWITTER_SECRET = "qmAWPnQR5FOkAlF7Ws7t4im62EG3lGgQaZVfQdJYgKZHm08wE3";

		} finally {

		}

	}


}
