package com.socioboard.t_board_pro.twitterapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;

public class TwitterUserGETRequest {

	TwitterRequestCallBack twitterRequestCallBack;

	ModelUserDatas userDatas;

	OAuthSignaturesGeneratorSorted oAuthSignaturesGenerator;

	public TwitterUserGETRequest(ModelUserDatas userDatas,
			TwitterRequestCallBack twitterRequestCallBack) {

		this.twitterRequestCallBack = twitterRequestCallBack;

		this.userDatas = userDatas;

	}

	public void executeThisRequest(String url,
			List<BasicNameValuePair> peramPairs) {

		new RequestAsync().execute(url, peramPairs);

	}

	public class RequestAsync extends AsyncTask<Object, Void, Void> {

		@Override
		protected Void doInBackground(Object... params) {

			String url = params[0].toString();

			List<BasicNameValuePair> peramPairs = (List<BasicNameValuePair>) params[1];

			myDoInBackground(url, peramPairs);

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);

		}

	}

	public void myDoInBackground(String url, List<BasicNameValuePair> peramPairs) {

		oAuthSignaturesGenerator = new OAuthSignaturesGeneratorSorted(
				userDatas.getUserAcessToken(), userDatas.getUsersecretKey(),
				MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "GET");

		String jsonString = null;

		String urlMade = url;

		if (peramPairs.size() > 0) {

			for (int i = 0; i < peramPairs.size(); ++i) {

				if (i == 0) {

					urlMade = urlMade + "?" + peramPairs.get(0).getName() + "="
							+ URLEncoder.encode(peramPairs.get(0).getValue());

				} else {

					urlMade = urlMade + "&" + peramPairs.get(i).getName() + "="
							+ URLEncoder.encode(peramPairs.get(i).getValue());
				}

			}

		} else {

			urlMade = url;

		}

		try {

			// HOME

			String authData = getAuthDAta(url, peramPairs);

			System.out.println("url : " + url);

			System.out.println("urlMade : " + urlMade);

			// System.out.println("authData : " + authData);

			URL obj = new URL(urlMade);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// optional default is GET

			con.setRequestMethod("GET");

			con.addRequestProperty("Authorization", authData);

			con.addRequestProperty("Host", "api.twitter.com");

			con.addRequestProperty("User-Agent", "OAuth gem v0.4.4");

			con.addRequestProperty("X-Target-URI", "https://api.twitter.com");

			con.addRequestProperty("Connection", "Keep-Alive");

			jsonString = readResponse(con);

			if (jsonString == null) {

				twitterRequestCallBack.onFailure(null);

			} else if (jsonString.startsWith("429")) {

				twitterRequestCallBack.onFailure(new Exception("429"));

			} else {

				twitterRequestCallBack.onSuccess(jsonString);
			}

		} catch (Exception e) {

			e.printStackTrace();

			twitterRequestCallBack.onFailure(e);

			System.out.println("Exception = =    " + e);

		}

	}

	// Reads a response for a given connection and returns it as a string.
	public String readResponse(HttpsURLConnection connection) {

		try {

			int responseCode = connection.getResponseCode();

			myprint("readResponse connection.getResponseCode()   "
					+ responseCode);

			String jsonString = null;

			if (responseCode == 429) {
				
				jsonString = "429";
				
			} else if (responseCode == HttpURLConnection.HTTP_OK) {

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

		oAuthSignaturesGenerator.setUrl(url);

		String GeneratedPerams = null;

		try {

			GeneratedPerams = "OAuth "
					+ oAuthSignaturesGenerator.OAUTH_CONSUMER_KEY
					+ "=\""
					+ URLEncoder.encode(oAuthSignaturesGenerator.getcKey(),
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_NONCE
					+ "=\""
					+ URLEncoder.encode(oAuthSignaturesGenerator.currentOnonce,
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_SIGNATURE_METHOD
					+ "=\""
					+ URLEncoder.encode(oAuthSignaturesGenerator.HMAC_SHA1,
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_TIMESTAMP
					+ "=\""
					+ URLEncoder.encode(
							oAuthSignaturesGenerator.currentTimeStamp,
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_TOKEN
					+ "=\""
					+ URLEncoder.encode(
							oAuthSignaturesGenerator.getAccesToken(),
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_VERSION
					+ "=\""
					+ URLEncoder.encode(oAuthSignaturesGenerator.VERSION_1_0,
							"ISO-8859-1")
					+ "\", "
					+ oAuthSignaturesGenerator.OAUTH_SIGNATURE
					+ "=\""
					+ URLEncoder.encode(oAuthSignaturesGenerator
							.getOauthSignature(peramPairs), "ISO-8859-1")
					+ "\"";

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

			System.out.println("GeneratedPerams UnsupportedEncodingException "
					+ e);

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
