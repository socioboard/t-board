package com.socioboard.t_board_pro.twitterapi;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import com.socioboard.t_board_pro.util.MainSingleTon;

public class TwitterAccessTokenPost {

	public TwitterAccessTokenPost() {

	}

	public String postForAccessToken(String oauthToken,String oauthVerifier) { 

 		String response = null;
 		
		try {

			//perams
			
			String urlTimeline = MainSingleTon.accessTokenPost +"?oauth_verifier="+oauthVerifier;
			 
			//String urlTimeline = MainSingleTon.accessTokenPost   ;
			
			String authData = getAuthDAta(urlTimeline,oauthToken,oauthVerifier);

			myprint("url : " + urlTimeline);

			myprint("authData : " + authData);

			URL obj = new URL(urlTimeline);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
  
			con.setRequestMethod("POST");
			
 			con.addRequestProperty("Authorization", authData);

			con.addRequestProperty("Host", "api.twitter.com");

			con.addRequestProperty("User-Agent", "twtboardpro");

			con.addRequestProperty("Accept", "*/*");
 			
			con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			response = TwitterGeneralRequest.readResponse(con);
			
			myprint("jsonString response = " + response);
			
			String string = response;
			
			string = "["+response+"]";
			
 			myprint("jsonString response = " + response);
  
		} catch (Exception e) {

			e.printStackTrace();

			myprint("Exception = =    " + e);

		}

		return response;
	
 	}
	void myprint(Object msg) {

//		System.out.println(msg.toString());
		
	}
	private String getAuthDAta(String urlTimeline,String oauthToken, String oauthVerifier) {

		OAuthSignaturesGeneratorForAccessToken oAuthSignaturesGenerator = new OAuthSignaturesGeneratorForAccessToken
				(oauthToken,  MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "POST");
		
		oAuthSignaturesGenerator.setUrl(MainSingleTon.accessTokenPost);
		
		String GeneratedPerams =  null;
		
		try {
			
			GeneratedPerams =  				       
					"OAuth " + oAuthSignaturesGenerator.OAUTH_CONSUMER_KEY  	        +"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.getcKey(),"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_NONCE     			    +"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.currentOnonce,"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_SIGNATURE                 +"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.getOauthSignature(),"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_SIGNATURE_METHOD	  		+"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.HMAC_SHA1,"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_TIMESTAMP           	    +"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.currentTimeStamp,"ISO-8859-1") 
					+"\", "  + oAuthSignaturesGenerator.OAUTH_TOKEN           	    	+"=\""    + URLEncoder.encode(oauthToken,"ISO-8859-1") 
 					+"\", "  + oAuthSignaturesGenerator.OAUTH_VERSION             		+"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.VERSION_1_0,"ISO-8859-1")
					+"\"" ;

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

			myprint("GeneratedPerams UnsupportedEncodingException "+ e);

		}

		String authenticateString = GeneratedPerams;

		String authData = authenticateString;

		return authData;

	}

	
}
