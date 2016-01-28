package com.socioboard.t_board_pro.twitterapi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;

public class TwitterPostRequestPerams {

	TwitterRequestCallBack twitterRequestCallBack;

	ModelUserDatas userDatas;

	OAuthSignaturesGeneratorSorted authSignaturesGenerator3;

 	public TwitterPostRequestPerams(ModelUserDatas userDatas,
			TwitterRequestCallBack twitterRequestCallBack) {

		this.twitterRequestCallBack = twitterRequestCallBack;

		this.userDatas = userDatas;
		
	}
 	
	public void executeThisRequest(String reqUrl,List<BasicNameValuePair> peramPairs) {

		new RequestAsync().execute(reqUrl,peramPairs);

	}
	
	public class RequestAsync extends AsyncTask<Object, Void, Void> {

		@Override
		protected Void doInBackground(Object... params) {

			String url = (String) params[0];
		
			List<BasicNameValuePair> peramPairs = (List<BasicNameValuePair>) params[1];
		
			myDoInBackground(url,peramPairs);

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			
			super.onProgressUpdate(values);

		}

	}

	
	public void myDoInBackground(String reqUrl,List<BasicNameValuePair> peramPairs) {

		authSignaturesGenerator3 = new OAuthSignaturesGeneratorSorted(
				userDatas.getUserAcessToken(), userDatas.getUsersecretKey(),
				MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "POST");

		String jsonString = null;
  
 		try {

			// HOME

			String authData = getAuthDAta(reqUrl, peramPairs);

			System.out.println("url : " + reqUrl);

			//System.out.println("authData : " + authData);

			URL obj = new URL(reqUrl);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("POST");

			con.addRequestProperty("Authorization", authData);

			con.addRequestProperty("Host", "api.twitter.com");

			con.addRequestProperty("User-Agent", "OAuth gem v0.4.4");

			con.addRequestProperty("X-Target-URI", "https://api.twitter.com");

			con.setDoOutput(true);

			boolean tmp;

			String data = peramPairs.get(0).getName()+"=" + URLEncoder.encode(peramPairs.get(0).getValue()).replace("+", "%20");

			for(int i=1;i<peramPairs.size();++i){
				
				data = data +"&"+ peramPairs.get(i).getName()+"=" + URLEncoder.encode(peramPairs.get(i).getValue()).replace("+", "%20");
				
			}
			
			myprint("data " + data);

			tmp = writeRequest(con, data);

			if (tmp) {

				jsonString = readResponse(con);

				if (jsonString == null) {
					
					twitterRequestCallBack.onFailure(new Exception());

				} else {

					twitterRequestCallBack.onSuccess(jsonString);

				}
				
			} else {

				twitterRequestCallBack.onFailure(new Exception());

			}

		} catch (Exception e) {

			e.printStackTrace();

			twitterRequestCallBack.onFailure(e);

			//System.out.println("Exception = =    " + e);

		}

	}

	boolean writeRequest(HttpsURLConnection con, String textBody) {

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

			myprint("readResponse connection.getResponseCode()   " + responseCode);

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

			// myprint("readResponse jsonString   " + jsonString);
			
			return jsonString;

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

		GeneratedPerams = "OAuth "
				+ authSignaturesGenerator3.OAUTH_CONSUMER_KEY
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.getcKey())
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_NONCE
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.currentOnonce)
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_SIGNATURE_METHOD
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.HMAC_SHA1)
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_TIMESTAMP
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.currentTimeStamp)
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_TOKEN
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.getAccesToken())
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_VERSION
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.VERSION_1_0)
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_SIGNATURE
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3
						.getOauthSignature(peramPairs)) + "\"";

		String authenticateString = GeneratedPerams;

		String authData = authenticateString;

		return authData;

	}

	public static void myprint(Object msg) {

		System.out.println(msg.toString());

	}

}
