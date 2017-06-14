package com.socioboard.t_board_pro.twitterapi;

import com.socioboard.t_board_pro.util.MainSingleTon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class TwitterSignIn {

	public TwitterSignIn() {

	}



	public String postForRequestToken() {

		String response = null;

		try {

			// perams
			String urlTimeline = MainSingleTon.reqTokenResourceURL;

			String authData = getAuthDAta(urlTimeline);

			myprint("url : " + urlTimeline);

			// myprint("authData : " + authData);

			URL obj = new URL(urlTimeline);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("POST");

			con.addRequestProperty("Authorization", authData);

			con.addRequestProperty("Host", "api.twitter.com");

			con.addRequestProperty("User-Agent", "twtboardpro");

			con.addRequestProperty("Accept", "*/*");

			response = readResponse(con);

			myprint("jsonString response = " + response);

		} catch (Exception e) {

			e.printStackTrace();

			myprint("Exception = =    " + e);

		}

		return response;

	}

	// Reads a response for a given connection and returns it as a string.
	public String readResponse(HttpsURLConnection connection) {

		try {

			myprint("readResponse getting   ");

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

			// myprint("readResponse jsonString   " + jsonString);

			return jsonString;

		} catch (IOException e) {

			// twitterRequestCallBack.onFailure(e);

			e.printStackTrace();

			myprint("readResponse IOExceptionException   " + e);

			return null;
		}

	}

	private String getAuthDAta(String urlTimeline) {

		AuthSignaturesGeneratorRequestToken oAuthSignaturesGenerator = new AuthSignaturesGeneratorRequestToken(
				MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "POST");

		oAuthSignaturesGenerator.setUrl(MainSingleTon.reqTokenResourceURL);

		String GeneratedPerams = null;

		try {

			GeneratedPerams = "OAuth "
					+ oAuthSignaturesGenerator.OAUTH_CALLBACK
					+ "=\""
					+ URLEncoder.encode(MainSingleTon.oauth_callbackURL,
							"ISO-8859-1")
					+ "\", "
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
					+ oAuthSignaturesGenerator.OAUTH_SIGNATURE
					+ "=\""
					+ URLEncoder.encode(
							oAuthSignaturesGenerator.getOauthSignature(),
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
					+ oAuthSignaturesGenerator.OAUTH_VERSION
					+ "=\""
					+ URLEncoder.encode(oAuthSignaturesGenerator.VERSION_1_0,
							"ISO-8859-1") + "\"";

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

			myprint("GeneratedPerams UnsupportedEncodingException " + e);

		}

		String authenticateString = GeneratedPerams;

		String authData = authenticateString;

		return authData;

	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

}
