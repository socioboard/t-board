package com.socioboard.t_board_pro.twitterapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import com.socioboard.t_board_pro.util.MainSingleTon;

public class TwitterGeneralRequest {
 	
	TwitterRequestCallBack twitterRequestCallBack;
	
	public TwitterGeneralRequest( TwitterRequestCallBack twitterRequestCallBack) {

 		this.twitterRequestCallBack =twitterRequestCallBack;

	}

 	public void doInBackground(String url) { 

 		String response = null;
 		
 		try {
 			 
 			myprint("url : " + url);
			
			String authData = getAuthDAta(url);

			myprint("authData : " + authData);

			URL obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			
			con.addRequestProperty("Authorization", authData);

			con.addRequestProperty("Host", "api.twitter.com");
			
			con.addRequestProperty("X-Target-URI", "https://api.twitter.com");

			con.addRequestProperty("Connection", "Keep-Alive");

			String tmp = readResponse(con);

			response = tmp.substring(1, tmp.length()-2);
			
			twitterRequestCallBack.onSuccess(response);
			
			myprint("jsonString response = " + response);

		} catch (Exception e) {

			e.printStackTrace();

			myprint("Exception = =    " + e);
			
			twitterRequestCallBack.onFailure(e);
			
		}

 	}
 	
	// Reads a response for a given connection and returns it as a string.
	public  static  String readResponse(HttpsURLConnection connection) {

		try {
			int responseCode = connection.getResponseCode();
			
			myprint("readResponse connection.getResponseCode()   "
					+responseCode );

			String  jsonString = null  ;

			 
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
			
			myprint("readResponse jsonString   " + jsonString);

			return jsonString ;

		} catch (IOException e) {

			e.printStackTrace();

			myprint("readResponse IOExceptionException   " + e);

			return null;
		}
	}

	private String getAuthDAta(String urlTimeline) {

		SignaturesGeneratorGeneral signaturesGeneratorForTimeline = new SignaturesGeneratorGeneral(MainSingleTon.currentUserModel, MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "GET");
	
		signaturesGeneratorForTimeline.setUrl(urlTimeline);

		String GeneratedPerams =  null;
		
		try {
			
			// UNSORTED
			
//			GeneratedPerams = 
//					"OAuth " + signaturesGeneratorForTimeline.OAUTH_CONSUMER_KEY  	        +"=\""	  + URLEncoder.encode(signaturesGeneratorForTimeline.getcKey(),"ISO-8859-1")
// 					+"\", "  + signaturesGeneratorForTimeline.OAUTH_SIGNATURE_METHOD	  	+"=\""	  + URLEncoder.encode(signaturesGeneratorForTimeline.HMAC_SHA1,"ISO-8859-1")
//					+"\", "  + signaturesGeneratorForTimeline.OAUTH_TIMESTAMP           	+"=\""    + URLEncoder.encode(signaturesGeneratorForTimeline.currentTimeStamp,"ISO-8859-1") 
//					+"\", "  + signaturesGeneratorForTimeline.OAUTH_NONCE     			    +"=\""	  + URLEncoder.encode(signaturesGeneratorForTimeline.currentOnonce,"ISO-8859-1")
//					+"\", "  + signaturesGeneratorForTimeline.OAUTH_VERSION             	+"=\""    + URLEncoder.encode(signaturesGeneratorForTimeline.VERSION_1_0,"ISO-8859-1")
//					+"\", "  + signaturesGeneratorForTimeline.OAUTH_TOKEN               	+"=\""    + URLEncoder.encode(MainSingleTon.currentUserModel.getUserAcessToken(),"ISO-8859-1")
//					+"\", "  + signaturesGeneratorForTimeline.OAUTH_SIGNATURE               +"=\""    + URLEncoder.encode(signaturesGeneratorForTimeline.getOauthSignature(),"ISO-8859-1")+"\"" ;

			
			// SORTED
			
			GeneratedPerams = 
					"OAuth " + signaturesGeneratorForTimeline.OAUTH_CONSUMER_KEY  	        +"=\""	  + URLEncoder.encode(signaturesGeneratorForTimeline.getcKey(),"ISO-8859-1")
 					+"\", "  + signaturesGeneratorForTimeline.OAUTH_NONCE     			    +"=\""	  + URLEncoder.encode(signaturesGeneratorForTimeline.currentOnonce,"ISO-8859-1")
					+"\", "  + signaturesGeneratorForTimeline.OAUTH_SIGNATURE_METHOD	  	+"=\""	  + URLEncoder.encode(signaturesGeneratorForTimeline.HMAC_SHA1,"ISO-8859-1")
					+"\", "  + signaturesGeneratorForTimeline.OAUTH_TIMESTAMP           	+"=\""    + URLEncoder.encode(signaturesGeneratorForTimeline.currentTimeStamp,"ISO-8859-1") 
					+"\", "  + signaturesGeneratorForTimeline.OAUTH_TOKEN               	+"=\""    + URLEncoder.encode(MainSingleTon.currentUserModel.getUserAcessToken(),"ISO-8859-1")
					+"\", "  + signaturesGeneratorForTimeline.OAUTH_VERSION             	+"=\""    + URLEncoder.encode(signaturesGeneratorForTimeline.VERSION_1_0,"ISO-8859-1")
					+"\", "  + signaturesGeneratorForTimeline.OAUTH_SIGNATURE               +"=\""    + URLEncoder.encode(signaturesGeneratorForTimeline.getOauthSignature(),"ISO-8859-1")+"\"" ;

			
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

			myprint("GeneratedPerams UnsupportedEncodingException "+ e);

		}

		String authenticateString = GeneratedPerams;

		String authData = authenticateString;

		return authData;

	}
	public static void myprint(Object msg) {

 	System.out.println(msg.toString());

	}

}
