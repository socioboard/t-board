package com.socioboard.t_board_pro.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

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

			System.out.println("Exception = " + e);

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

	public static String GetLocalDateStringFromUTCString(String utcLongDateTime) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		String localDateString = null;
		
		long when = 0;
		
		try {
			
			when = dateFormat.parse(utcLongDateTime).getTime();
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			
		}
		
		localDateString = dateFormat.format(new Date(when
				+ TimeZone.getDefault().getRawOffset()
				+ (TimeZone.getDefault().inDaylightTime(new Date()) ? TimeZone
						.getDefault().getDSTSavings() : 0)));
		
	 	System.out.println("when     : " + when);

	    //	System.out.println("In MILLIES     : " + when);
 	 	
		Calendar date = Calendar.getInstance();
		
		Date d = date.getTime();
		
		long different = d.getTime() - when;
		
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;
		long weeksInMilli = daysInMilli * 7;
		long monthsInMilli = weeksInMilli * 4;
		long year = monthsInMilli * 12;
		
		long elapsedYears = different / year;
		long elapsedMonths = different / monthsInMilli;
		long elspsedWeeks = different / weeksInMilli;
		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;
		long elapsedHours = different / hoursInMilli;
		long elapsedMinute = different / minutesInMilli;
		String timeago;
		
		if (elapsedYears > 0) {
			if (elapsedYears == 1)
				timeago = "" + elapsedYears + " Year Ago";
			else
				timeago = "" + elapsedYears + " Years Ago";
		} else if (elapsedMonths > 0) {
			if (elapsedMonths == 1)
				timeago = "" + elapsedMonths + " Month Ago";
			else
				timeago = "" + elapsedMonths + " Months Ago";
		} else if (elspsedWeeks > 0) {
			if (elspsedWeeks == 1)
				timeago = "" + elspsedWeeks + " Week Ago";
			else
				timeago = "" + elspsedWeeks + " Weeks Ago";
		} else if (elapsedDays > 0) {
			if (elapsedDays == 1)
				timeago = "" + elapsedDays + " Day Ago";
			else
				timeago = "" + elapsedDays + " Days Ago";
		} else if (elapsedHours > 0) {
			if (elapsedHours == 1)
				timeago = "" + elapsedHours + " Hour Ago";
			else
				timeago = "" + elapsedHours + " Hours Ago";
		} else {
			if (elapsedMinute == 1)
				timeago = "" + elapsedMinute + " Minute Ago";
			else
				timeago = "" + elapsedMinute + " Minutes Ago";
			if(elapsedMinute==0){
				timeago = "Just Now" ;
			}
		}
		
		//System.out.println("created     : " + timeago);
		
		return timeago;
		
	}

	public static int monthInt(String month) {

		System.out.print("Enter month's number: ");

		int monthNumber;

		switch (month.toLowerCase()) {
		case "january":
			monthNumber = 1;
			break;
		case "february":
			monthNumber = 2;
			break;
		case "march":
			monthNumber = 3;
			break;
		case "april":
			monthNumber = 4;
			break;
		case "may":
			monthNumber = 5;
			break;
		case "june":
			monthNumber = 6;
			break;
		case "july":
			monthNumber = 7;
			break;
		case "august":
			monthNumber = 8;
			break;
		case "september":
			monthNumber = 9;
			break;
		case "october":
			monthNumber = 10;
			break;
		case "november":
			monthNumber = 11;
			break;
		case "december":
			monthNumber = 12;
			break;
		default:
			monthNumber = 0;
			break;
		}
		return monthNumber;
	}

	  public static String encodeTobase64(Bitmap image)
	{
	    Bitmap immagex=image;
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    byte[] b = baos.toByteArray();
	    String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

	  //  Log.e("LOOK", imageEncoded);
	    
	    return imageEncoded;
	}
	
	public static Bitmap decodeBase64(String input) 
	{
	    byte[] decodedByte = Base64.decode(input, 0);
	    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
	}

}
