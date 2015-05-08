package com.socioboard.t_board_pro.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * this class is used for get Json data from given web services url
 */

public class Utils {

	// get Json from given url

	public static String getHTTPResponse(String url, String method) {

		String jsonString = null;

		HttpURLConnection linkConnection = null;

		try {

			URL linkurl = new URL(url);

			linkConnection = (HttpURLConnection) linkurl.openConnection();

			linkConnection.setRequestMethod(method);

			int responseCode = linkConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {

				InputStream linkinStream = linkConnection.getInputStream();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				int j = 0;

				while ((j = linkinStream.read()) != -1) {

					baos.write(j);

				}

				byte[] data = baos.toByteArray();

				jsonString = new String(data);

			}

		} catch (Exception e) {

			e.printStackTrace();
		
			System.out.println("Exception = "+e);
		
		} finally {

			if (linkConnection != null) {
				linkConnection.disconnect();
			}

		}

		return jsonString;
	}

	// check whether network is availbale or not
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
