package com.socioboard.t_board_pro.twitterapi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;

public class TwitterPostRequestFollow {

	TwitterRequestCallBack twitterRequestCallBack;

	ModelUserDatas userDatas;

	OAuthSignaturesGenerator3 authSignaturesGenerator3;

	public TwitterPostRequestFollow(ModelUserDatas userDatas,
			TwitterRequestCallBack twitterRequestCallBack) {

		this.twitterRequestCallBack = twitterRequestCallBack;

		this.userDatas = userDatas;
	}

	public void executeThisRequest(String id) {

		new RequestAsync().execute(id);

	}

	public class RequestAsync extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String id = params[0];

			myDoInBackground(id);

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);

		}

	}

	public void myDoInBackground(String id) {

		authSignaturesGenerator3 = new OAuthSignaturesGenerator3(
				userDatas.getUserAcessToken(), userDatas.getUsersecretKey(),
				MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "POST");

		String jsonString = null;

		String url = MainSingleTon.followUrl  ;

		List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

		peramPairs.add(new BasicNameValuePair("user_id", id));

		try {

			// HOME

			String authData = getAuthDAta(url, peramPairs);

			System.out.println("url : " + url);

			System.out.println("authData : " + authData);

 			URL obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("POST");

			con.addRequestProperty("Authorization", authData);

			con.addRequestProperty("Host", "api.twitter.com");

			con.addRequestProperty("User-Agent", "OAuth gem v0.4.4");

			con.addRequestProperty("X-Target-URI", "https://api.twitter.com");

			con.setDoOutput(true);

			boolean tmp;

			tmp = writeRequest(con, "user_id="+id);

			if (tmp) {

				jsonString = readResponse(con);

				if (jsonString == null) {

				} else {

					twitterRequestCallBack.onSuccess(jsonString);

				}
			}else{
				
				twitterRequestCallBack.onFailure(new Exception());

			}
			
		} catch (Exception e) {

			e.printStackTrace();

			twitterRequestCallBack.onFailure(e);

			System.out.println("Exception = =    " + e);

		}

	}

	boolean writeRequest(HttpsURLConnection con , String textBody) {

		try {
			 
			System.out.println("writeRequest     " + textBody);
			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(textBody);
			wr.flush();
			wr.close();
			
 			return true;
			
		} catch (IOException e) {
			
			System.out.println("writeRequest IOException   " + e);

			return false;
		}
	}

	// Reads a response for a given connection and returns it as a string.
	public String readResponse(HttpsURLConnection connection) {

		try {

			int responseCode = connection.getResponseCode();

			myprint("readResponse connection.getResponseCode()   "
					+ responseCode);

			String jsonString = null;

			if (responseCode == HttpURLConnection.HTTP_OK
					|| responseCode == 403) {

				InputStream linkinStream = connection.getInputStream();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				int j = 0;

				while ((j = linkinStream.read()) != -1) {

					baos.write(j);

				}

				byte[] data = baos.toByteArray();

				jsonString = new String(data);

			}

			// myprint("readResponse jsonString   " + jsonString);

			return jsonString.toString();

		} catch (IOException e) {

			twitterRequestCallBack.onFailure(e);

			e.printStackTrace();

			myprint("readResponse IOExceptionException   " + e);

			return null;
		}
		
	}

	private String getAuthDAta(String url, List<BasicNameValuePair> peramPairs) {

		authSignaturesGenerator3.setUrl(url);

		String GeneratedPerams = null;

		GeneratedPerams = "OAuth " + authSignaturesGenerator3.OAUTH_CONSUMER_KEY 	+ "=\"" + URLEncoder.encode(authSignaturesGenerator3.getcKey())
				+ "\", " + authSignaturesGenerator3.OAUTH_NONCE 					+ "=\"" + URLEncoder.encode(authSignaturesGenerator3.currentOnonce)
				+ "\", " + authSignaturesGenerator3.OAUTH_SIGNATURE_METHOD			+ "=\"" + URLEncoder.encode(authSignaturesGenerator3.HMAC_SHA1)
				+ "\", " + authSignaturesGenerator3.OAUTH_TIMESTAMP 			 	+ "=\"" + URLEncoder.encode(authSignaturesGenerator3.currentTimeStamp)
				+ "\", " + authSignaturesGenerator3.OAUTH_TOKEN 					+ "=\"" + URLEncoder.encode(authSignaturesGenerator3.getAccesToken())
				+ "\", " + authSignaturesGenerator3.OAUTH_VERSION 					+ "=\"" + URLEncoder.encode(authSignaturesGenerator3.VERSION_1_0)
				+ "\", " + authSignaturesGenerator3.OAUTH_SIGNATURE 				+ "=\"" + URLEncoder.encode(authSignaturesGenerator3 .getOauthSignature(peramPairs)) + "\"";

		String authenticateString = GeneratedPerams;

		String authData = authenticateString;

		return authData;

	}

	public static void myprint(Object msg) {

		System.out.println(msg.toString());

	}

}
