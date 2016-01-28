package com.socioboard.t_board_pro;

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

import android.content.Context;

import com.socioboard.t_board_pro.twitterapi.OAuthSignaturesGenerator3;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TboardproLocalData;

public class StartDM {

	Context context;

	TboardproLocalData tboardproLocalData;

	ArrayList<String> targetIds = new ArrayList<String>();

	ModelUserDatas userDatas;

	OAuthSignaturesGenerator3 authSignaturesGeneratorSorted;

	String messagtext;

	ArrayList<String> mutualsIds;

	Thread thread;

	public StartDM(Context context, ModelUserDatas modelUserDatas,
			ArrayList<String> mutualsIds) {

		super();

		this.mutualsIds = mutualsIds;

		this.context = context;

		String middlemessagtext = context.getSharedPreferences("twtboardpro", Context.MODE_PRIVATE).getString("autodmtext", "");

		messagtext = "Thanks for Following me :) " + middlemessagtext + " \n-via @socioboard";

		tboardproLocalData = new TboardproLocalData(context);

		userDatas = modelUserDatas;

		authSignaturesGeneratorSorted = new OAuthSignaturesGenerator3(
				modelUserDatas.getUserAcessToken(),
				modelUserDatas.getUsersecretKey(), MainSingleTon.TWITTER_KEY,
				MainSingleTon.TWITTER_SECRET, "POST");

	}

	public void analyseTarget() {

		ArrayList<String> listIDs = tboardproLocalData
				.getAllSendingIDs(userDatas.getUserid());

		// these are the ids which need to be sent DM.

		// upto here i know i have followed these ids

		myprint("getAllSendingIDs  " + listIDs);

		for (int i = 0; i < mutualsIds.size(); ++i) {

			if (listIDs.contains(mutualsIds.get(i))) {

				// got confirmed that i dint sent DM to this Mutual id

				// is the id which following me back.

				targetIds.add(mutualsIds.get(i));

				myprint("targetIds.add(mutualsIds.get(i)); "
						+ mutualsIds.get(i));

			} else {

				myprint("rejected ID  " + mutualsIds.get(i));

			}

		}

		myprint("targetIds = " + targetIds);

		myprint("@@@@@@@@@@@@@@@@@@@ startSendingMessages @@@@@@@@@@@@@@@@@@");

		if (targetIds.size() > 0) {

			thread = new Thread(new Runnable() {

				@Override
				public void run() {

					startSendingMessages(0);

				}
			});

			thread.start();

			try {

				thread.join();

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

		} else {

			myprint("No targets TO strat ");
		}

	}

	public void startSendingMessages(final int i) {

		myprint("********** startSendingMessages(final int i) " + i);

		String url = MainSingleTon.createMessage;

		List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

		peramPairs.add(new BasicNameValuePair("text", messagtext));

		peramPairs.add(new BasicNameValuePair(Const.user_id, targetIds.get(i)));

		myDoInBackground(url, peramPairs);

		int tmp_i = i;

		tmp_i++;

		if (tmp_i < (targetIds.size() - 1)) {

			try {

				Thread.sleep(5000);

				myprint("********** Thread.sleep(7200000);");

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

			startSendingMessages(tmp_i);

		} else {

			myprint("********** ENd of sending DM");

		}

	}

	public void myDoInBackground(String reqUrl,
			List<BasicNameValuePair> peramPairs) {

		myprint("@@@@@@@@@@@@@@@@@@@ myDoInBackground @@@@@@@@@@@@@@@@@@");

		authSignaturesGeneratorSorted = new OAuthSignaturesGenerator3(
				userDatas.getUserAcessToken(), userDatas.getUsersecretKey(),
				MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "POST");

		String jsonString = null;

		try {

			// HOME

			String authData = getAuthDAta(reqUrl, peramPairs);

			System.out.println("url : " + reqUrl);

			System.out.println("authData : " + authData);

			URL obj = new URL(reqUrl);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("POST");

			con.addRequestProperty("Authorization", authData);

			con.addRequestProperty("Host", "api.twitter.com");

			con.addRequestProperty("User-Agent", "OAuth gem v0.4.4");

			con.addRequestProperty("X-Target-URI", "https://api.twitter.com");

			con.setDoOutput(true);

			boolean tmp = false;

			String data = peramPairs.get(0).getName()
					+ "="
					+ URLEncoder.encode(peramPairs.get(0).getValue()).replace(
							"+", "%20");

			for (int i = 1; i < peramPairs.size(); ++i) {

				data = data
						+ "&"
						+ peramPairs.get(i).getName()
						+ "="
						+ URLEncoder.encode(peramPairs.get(i).getValue())
								.replace("+", "%20");

			}

			myprint("data " + data);

			tmp = writeRequest(con, data);

			if (tmp) {

				jsonString = readResponse(con);

				myprint(jsonString);

				if (jsonString == null) {

				} else {

					tboardproLocalData.addNewDMsentId(peramPairs.get(1)
							.getValue(), userDatas.getUserid());

					tboardproLocalData.deleteThisDMSendingId(peramPairs.get(1)
							.getValue(), userDatas.getUserid());

				}

			} else {

			}

		} catch (Exception e) {

			e.printStackTrace();

			System.out.println("Exception = =    " + e);

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

			} else {

				System.out
						.println("***********************************Need to stop sending Due to invalid responses********************");

				try {

					thread.interrupt();

				} catch (Exception exception) {

				}

			}

			// myprint("readResponse jsonString   " + jsonString);

			myprint("eeeeeeeeeeeee " + connection.getErrorStream());

			return jsonString;

		} catch (IOException e) {

			// twitterRequestCallBack.onFailure(e);

			e.printStackTrace();

			myprint("readResponse IOExceptionException   " + e);

			return null;
		}

	}

	private String getAuthDAta(String url, List<BasicNameValuePair> peramPairs) {

		authSignaturesGeneratorSorted.setUrl(url);

		String GeneratedPerams = null;

		GeneratedPerams = "OAuth "
				+ authSignaturesGeneratorSorted.OAUTH_CONSUMER_KEY
				+ "=\""
				+ URLEncoder.encode(authSignaturesGeneratorSorted.getcKey())
				+ "\", "
				+ authSignaturesGeneratorSorted.OAUTH_NONCE
				+ "=\""
				+ URLEncoder
						.encode(authSignaturesGeneratorSorted.currentOnonce)
				+ "\", "
				+ authSignaturesGeneratorSorted.OAUTH_SIGNATURE_METHOD
				+ "=\""
				+ URLEncoder.encode(authSignaturesGeneratorSorted.HMAC_SHA1)
				+ "\", "
				+ authSignaturesGeneratorSorted.OAUTH_TIMESTAMP
				+ "=\""
				+ URLEncoder
						.encode(authSignaturesGeneratorSorted.currentTimeStamp)
				+ "\", "
				+ authSignaturesGeneratorSorted.OAUTH_TOKEN
				+ "=\""
				+ URLEncoder.encode(authSignaturesGeneratorSorted
						.getAccesToken())
				+ "\", "
				+ authSignaturesGeneratorSorted.OAUTH_VERSION
				+ "=\""
				+ URLEncoder.encode(authSignaturesGeneratorSorted.VERSION_1_0)
				+ "\", "
				+ authSignaturesGeneratorSorted.OAUTH_SIGNATURE
				+ "=\""
				+ URLEncoder.encode(authSignaturesGeneratorSorted
						.getOauthSignature(peramPairs)) + "\"";

		String authenticateString = GeneratedPerams;

		String authData = authenticateString;

		return authData;

	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}
}
