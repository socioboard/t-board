package com.socioboard.t_board_pro.twitterapi;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import com.socioboard.t_board_pro.util.MainSingleTon;

public class TwitterSignIn {

	public TwitterSignIn() {

	}

	public String postForRequestToken() { 

 		String response = null;
 		
		try {

			//perams
			String urlTimeline = MainSingleTon.reqTokenResourceURL ;
			
			String authData = getAuthDAta(urlTimeline);

			myprint("url : " + urlTimeline);

			myprint("authData : " + authData);

			URL obj = new URL(urlTimeline);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// optional default is GET

			con.setRequestMethod("POST");
			
 			con.addRequestProperty("Authorization", authData);

			con.addRequestProperty("Host", "api.twitter.com");

			con.addRequestProperty("User-Agent", "twtboardpro");

			con.addRequestProperty("Accept", "*/*");
  
			response = TwitterGeneralRequest.readResponse(con);
			
			myprint("jsonString response = " + response);

		} catch (Exception e) {

			e.printStackTrace();

			myprint("Exception = =    " + e);

		}

		return response;
	
 	}

	private String getAuthDAta(String urlTimeline) {

		AuthSignaturesGeneratorRequestToken oAuthSignaturesGenerator = new AuthSignaturesGeneratorRequestToken
				(MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "POST" );
		
		oAuthSignaturesGenerator.setUrl(MainSingleTon.reqTokenResourceURL);
		
		String GeneratedPerams =  null;
		
		try {
			
			GeneratedPerams =  				       
					"OAuth " + oAuthSignaturesGenerator.OAUTH_CALLBACK 	                +"=\""	  + URLEncoder.encode(MainSingleTon.oauth_callbackURL,"ISO-8859-1")
 					+"\", "  + oAuthSignaturesGenerator.OAUTH_CONSUMER_KEY  	        +"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.getcKey(),"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_NONCE     			    +"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.currentOnonce,"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_SIGNATURE                 +"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.getOauthSignature(),"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_SIGNATURE_METHOD	  		+"=\""	  + URLEncoder.encode(oAuthSignaturesGenerator.HMAC_SHA1,"ISO-8859-1")
					+"\", "  + oAuthSignaturesGenerator.OAUTH_TIMESTAMP           	    +"=\""    + URLEncoder.encode(oAuthSignaturesGenerator.currentTimeStamp,"ISO-8859-1") 
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
	public void myprint(Object msg) {

 	System.out.println(msg.toString());

	}

}
