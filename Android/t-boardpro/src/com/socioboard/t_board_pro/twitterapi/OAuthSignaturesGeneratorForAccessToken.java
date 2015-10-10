package com.socioboard.t_board_pro.twitterapi;

 
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OAuthSignaturesGeneratorForAccessToken {

	public static final String ENCODING = "UTF-8";
	
	public static final String VERSION_1_0 = "1.0";

	public static final String FORM_ENCODED = "application/x-www-form-urlencoded";

	public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
	public static final String OAUTH_TOKEN = "oauth_token";
	public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
	public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
	public static final String OAUTH_SIGNATURE = "oauth_signature";
	public static final String OAUTH_TIMESTAMP = "oauth_timestamp";
	public static final String OAUTH_NONCE = "oauth_nonce";
	public static final String OAUTH_VERSION = "oauth_version";
	public static final String OAUTH_CALLBACK = "oauth_callback";
	public static final String OAUTH_CALLBACK_CONFIRMED = "oauth_callback_confirmed";
	public static final String OAUTH_VERIFIER = "oauth_verifier";

	public static final String HMAC_SHA1 = "HMAC-SHA1";

	public static final String RSA_SHA1 = "RSA-SHA1";

	public String accesToken;
 	public String cKey,cSecret;
	public String url,method;

	public String currentTimeStamp,currentOnonce;
	
	public OAuthSignaturesGeneratorForAccessToken(String accesToken,  
			String cKey,String cSecret,  String method) {

		super();
		this.accesToken = accesToken;
 		this.cKey = cKey;
		this.method = method;
 		this.cSecret = cSecret;

	}

	public String getOauthSignature(){
		
		String oAuthSignature = null;
		
		// * * * 1 * * * *
		
		String GeneratedPerams = gentratedPerams();
		
		// * * * 2 * * * *

		String baseString = generateBaseString(GeneratedPerams);
		
		// * * * 3 * * * *

		String singningKey = genrateSigningKey();
		
		// * * * 4 * * * *

		oAuthSignature = getCalcShaHash(baseString, singningKey);
		
		// * * * Done * * * *

		return oAuthSignature;
	}
	
	String gentratedPerams(){
		
		String GeneratedPerams = null;
		
		try {
			
 		  GeneratedPerams =  
					   OAUTH_CONSUMER_KEY  	     +"="	+ URLEncoder.encode(getcKey(),"ISO-8859-1")        
				+"&" + OAUTH_NONCE     			 +"="	+ URLEncoder.encode(currentOnonce,"ISO-8859-1")    
				+"&" + OAUTH_SIGNATURE_METHOD	 +"="	+ URLEncoder.encode(HMAC_SHA1,"ISO-8859-1")        
				+"&" + OAUTH_TIMESTAMP           +"="   + URLEncoder.encode(currentTimeStamp,"ISO-8859-1") 
				+"&" + OAUTH_TOKEN               +"="   + URLEncoder.encode(getAccesToken(),"ISO-8859-1")  
				+"&" + OAUTH_VERSION             +"="   + URLEncoder.encode(VERSION_1_0,"ISO-8859-1");
		
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		
		myprint("GeneratedPerams = "+ GeneratedPerams);

		return GeneratedPerams;
	}

	String generateBaseString(String peramsUrl){

		String baseString = null;

		try {

			myprint("URLEncoder.encode("+url+") = "+ URLEncoder.encode(url,"ISO-8859-1"));

			baseString = method+"&"+URLEncoder.encode(url,"ISO-8859-1")+"&"+URLEncoder.encode(peramsUrl,"ISO-8859-1");

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		myprint("baseString = "+ baseString);

		return baseString;
	}	

	String genrateSigningKey(){

		String singningKey = null;

		try {

			singningKey = URLEncoder.encode(cSecret,"ISO-8859-1")+"&";
		
			myprint("genrateSigningKey = "+ singningKey);

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		return singningKey;
	} 
 
	public String getAccesToken() {

		return accesToken;
	}

	public void setAccesToken(String accesToken) {
		this.accesToken = accesToken;
	}
 
	public String getcKey() {
		return cKey;
	}

	public void setcKey(String cKey) {
		this.cKey = cKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		
		currentTimeStamp = calcTstamp();
	
		currentOnonce = calcOnonce();
		
		this.url = url;
	}

	public  String calcTstamp() {
		return new String("" + System.currentTimeMillis() / 1000);
	}

	public   String calcOnonce() {
		
		String oNonce = new String("" + (System.currentTimeMillis())/ 1000*55);
		
		myprint("getOnonce = "+ oNonce);

		return oNonce;
	}

	public  String getCalcShaHash (String data, String key) {

 		String oAuthSignature = null;

		try {         

			Key signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1);

			Mac mac = Mac.getInstance(HMAC_SHA1);

			mac.init(signingKey);

			byte[] rawHmac = mac.doFinal(data.getBytes());

			// Done
			String noUrlEncoding = Base64.encodeBytes(rawHmac);
			
			// Dircet
			oAuthSignature = noUrlEncoding;    
			
			// perencoded
			// oAuthSignature = URLEncoder.encode(noUrlEncoding,"ISO-8859-1");    
			
 			myprint("getCalcShaHash = "+ oAuthSignature);

		} catch (Exception e) {

			e.printStackTrace(); 
			
			myprint("Exception = "+ e);

		}       

		return oAuthSignature;
	}
	
	public void myprint(Object msg) {

//		System.out.println(msg.toString());

	}

}
