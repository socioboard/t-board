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
import com.socioboard.t_board_pro.util.ModelUserDatas;

public class TwitterShowUserJSon {

	ModelUserDatas userDatas;
	
	String url = "https://api.twitter.com/1.1/users/show.json";
 	
	public TwitterShowUserJSon( TwitterRequestCallBack twitterRequestCallBack,ModelUserDatas userDatas) {
		
		this.twitterRequestCallBack =twitterRequestCallBack;
		
		this.userDatas = userDatas;
		
		url = url+"?screen_name="+userDatas.getUsername()+"&"+userDatas.getUserid();
		
	}
	
	TwitterRequestCallBack twitterRequestCallBack;

	public void requestCall() { 

		myprint("TwitterShowUserJSon requestCall");
		
 		String response = null;
 		
		try {
		
			String authData = getAuthDAta(url, "GET");

			myprint("url : " + url);

			//myprint("authData : " + authData);

			URL obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
  
			con.setRequestMethod("GET");
			
 			con.addRequestProperty("Authorization", authData);

			con.addRequestProperty("Host", "api.twitter.com");
			
			con.addRequestProperty("X-Target-URI", "https://api.twitter.com");
			
			con.addRequestProperty("Connection", "Keep-Alive");
  
			response = readResponse(con);
			
			myprint("jsonString response = " + response);
			
 			twitterRequestCallBack.onSuccess(response);
 			
		} catch (Exception e) {

			e.printStackTrace();

			myprint("Exception = =    " + e);
					
 			twitterRequestCallBack.onFailure(e);

		}

 	
 	}
	void myprint(Object msg) {

 		System.out.println(msg.toString());
		
	}
	String readResponse(HttpsURLConnection connection) {

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

			return jsonString.toString();

		} catch (IOException e) {

			e.printStackTrace();
			
 			twitterRequestCallBack.onFailure(e);

			myprint("readResponse IOExceptionException   " + e);

			return new String();
		}
	}
	
	private String getAuthDAta(String url ,String method ) {

		SignatureForUserShow oAuthSignaturesGenerator = new SignatureForUserShow
				(userDatas, MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, method);
		
		oAuthSignaturesGenerator.setUrl(url);
		
		String GeneratedPerams =  null;
		
		try {
			
			GeneratedPerams =  				       
					"OAuth " + oAuthSignaturesGenerator.OAUTH_CONSUMER_KEY  	        +"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.getcKey(),"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_SIGNATURE_METHOD	  		+"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.HMAC_SHA1,"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_TIMESTAMP           	    +"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.currentTimeStamp,"ISO-8859-1") 
					+"\", "  + oAuthSignaturesGenerator.OAUTH_NONCE     			    +"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.currentOnonce,"ISO-8859-1")
 					+"\", "  + oAuthSignaturesGenerator.OAUTH_VERSION             		+"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.VERSION_1_0,"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_TOKEN           	    	+"=\""    + URLEncoder.encode(userDatas.getUserAcessToken(),"ISO-8859-1") 
			        +"\", "  + oAuthSignaturesGenerator.OAUTH_SIGNATURE                 +"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.getOauthSignature(),"ISO-8859-1")
					+"\"" ;
 
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

			myprint("GeneratedPerams UnsupportedEncodingException "+ e);
			
 			twitterRequestCallBack.onFailure(e);

		}

		String authenticateString = GeneratedPerams;

		String authData = authenticateString;

		return authData;

	}

	
}
