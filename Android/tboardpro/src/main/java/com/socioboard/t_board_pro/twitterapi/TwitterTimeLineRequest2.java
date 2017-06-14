package com.socioboard.t_board_pro.twitterapi;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;

public class TwitterTimeLineRequest2 {

	TwitterRequestCallBack twitterRequestCallBack;
	ModelUserDatas userDatas;
	OAuthSignaturesGenerator2 oAuthSignaturesGenerator;

	public TwitterTimeLineRequest2(ModelUserDatas userDatas,
			TwitterRequestCallBack twitterRequestCallBack) {

		this.twitterRequestCallBack = twitterRequestCallBack;
		this.userDatas = userDatas;
	}

	public void doInBackground(String url) {
 
		oAuthSignaturesGenerator = new OAuthSignaturesGenerator2(
				userDatas.getUserAcessToken(), userDatas.getUsersecretKey(),
				MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "GET");
		
		String jsonString = null;
		
		try {
			
 			// HOME
 		 
			String authData = getAuthDAta(url);

			System.out.println("url : " + url);

			//System.out.println("authData : " + authData);

			URL obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			Log.d("cheching ...   ",authData);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.addRequestProperty("Authorization", authData);
			con.addRequestProperty("Host", "api.twitter.com");
			con.addRequestProperty("User-Agent", "OAuth gem v0.4.4");
			con.addRequestProperty("X-Target-URI", "https://api.twitter.com");
			con.addRequestProperty("Connection", "Keep-Alive");
 			jsonString = readResponse(con);
 			twitterRequestCallBack.onSuccess(jsonString);
 			
 			//twitterRequestCallBack.onSuccess(jsonString);

			//System.out.println("jsonString = " + jsonString);

		} catch (Exception e) {

			e.printStackTrace();
			
			twitterRequestCallBack.onFailure(e);

			System.out.println("Exception = =    " + e);

		}
 
	}

	// Reads a response for a given connection and returns it as a string.
	public   String readResponse(HttpsURLConnection connection) {

		try {
			
			int responseCode = connection.getResponseCode();

			myprint("readResponse connection.getResponseCode()   "
					+ responseCode);

			String jsonString = null;

			if (responseCode == HttpURLConnection.HTTP_OK) {

				InputStream linkinStream = connection.getInputStream();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				int j = 0;

				while ((j = linkinStream.read()) != -1) {

					baos.write(j);
					
				}

				byte[] data = baos.toByteArray();

				jsonString = new String(data);

			}

			//myprint("readResponse jsonString   " + jsonString);

			return jsonString.toString();

		} catch (IOException e) {
			
			twitterRequestCallBack.onFailure(e);

			e.printStackTrace();

			myprint("readResponse IOExceptionException   " + e);

			return new String();
		}
	}

	private String getAuthDAta(String urlTimeline) {

		oAuthSignaturesGenerator.setUrl(urlTimeline);

		String GeneratedPerams =  null;
		
		try {
			
			GeneratedPerams = 
					"OAuth " + oAuthSignaturesGenerator.OAUTH_CONSUMER_KEY  	        +"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.getcKey(),"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_NONCE     			    +"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.currentOnonce,"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_SIGNATURE_METHOD	  		+"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.HMAC_SHA1,"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_TIMESTAMP           	    +"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.currentTimeStamp,"ISO-8859-1") 
					+"\", "  + oAuthSignaturesGenerator.OAUTH_TOKEN               		+"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.getAccesToken(),"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_VERSION             		+"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.VERSION_1_0,"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_SIGNATURE                 +"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.getOauthSignature(),"ISO-8859-1")+"\"" ;

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

			System.out.println("GeneratedPerams UnsupportedEncodingException "+ e);
			
			twitterRequestCallBack.onFailure(e);

		}

		String authenticateString = GeneratedPerams;

		String authData = authenticateString;

		return authData;

	 }

	public static void myprint(Object msg) {

		System.out.println(msg.toString());

	}

}
