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

import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TwtboardproLocalData;
import com.socioboard.tboardpro.R;

public class SplashActivity extends Activity {

	// Local DataBase
	TwtboardproLocalData twiterManyLocalData;

	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);
   
 		twiterManyLocalData = new TwtboardproLocalData(getApplicationContext());

		twiterManyLocalData.CreateTable();

		MainSingleTon.allUserdetails = twiterManyLocalData.getAllUsersData();

		System.out.println();

		if (MainSingleTon.allUserdetails.size() == 0) 
		{
			

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

				System.out.println(MainSingleTon.currentUserModel
						+ " currentUserModel");

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

	}

	// Encodes the consumer key and secret to create the basic authorization key
	private static String encodeKeys(String consumerKey, String consumerSecret) {

		try {

			String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
			String encodedConsumerSecret = URLEncoder.encode(consumerSecret,
					"UTF-8");

			String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;

			byte[] encodedBytes = Base64.encode(fullKey.getBytes(),
					Base64.NO_WRAP);

			// byte[] encodedBytes = Base64.encode(fullKey.getBytes(),
			// Base64.DEFAULT);

			return new String(encodedBytes);
		} catch (UnsupportedEncodingException e) {

			System.out.println("Exception   " + e);

			return new String();
		}
	}

	

	// Writes a request to a connection
	public static boolean writeRequest(HttpsURLConnection connection,
			String textBody) {
		try {
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
					connection.getOutputStream()));
			wr.write(textBody);
			wr.flush();
			wr.close();

			return true;
		} catch (IOException e) {
			System.out.println("writeRequest IOException   " + e);

			return false;
		}
	}

	// Reads a response for a given connection and returns it as a string.
	public static String readResponse(HttpsURLConnection connection) {

		try {

			StringBuilder str = new StringBuilder();

			System.out.println("readResponse connection.getResponseCode()   "
					+ connection.getResponseCode());

			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			String line = "";

			while ((line = br.readLine()) != null) {
				str.append(line + System.getProperty("line.separator"));
			}
			return str.toString();

		} catch (IOException e) {

			System.out.println("readResponse IOExceptionException   " + e);

			e.printStackTrace();

			return new String();
		}
	}



}
