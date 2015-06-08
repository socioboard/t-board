package com.socioboard.t_board_pro.twitterapi;

import java.net.URLEncoder;
import java.security.Key;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.message.BasicNameValuePair;

public class OAuthSignaturesGenerator3 {

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
	public String tokenSecret;
	public String cKey,cSecret;
	public String url,method;

	public String currentTimeStamp,currentOnonce;
	
	public OAuthSignaturesGenerator3(String accesToken, String tokenSecret,
			String cKey,String cSecret,  String method) {

		super();
		this.accesToken = accesToken;
		this.tokenSecret = tokenSecret;
		this.cKey = cKey;
		this.method = method;
 		this.cSecret = cSecret;

	}

	public String getOauthSignature(List<BasicNameValuePair> peramPairs){
		
		
		String oAuthSignature = null;
		
		// * * * 1 * * * *
		
		String GeneratedPerams = gentratedPerams(peramPairs);
		
		// * * * 2 * * * *

		String baseString = generateBaseString(GeneratedPerams);
		
		// * * * 3 * * * *

		String singningKey = genrateSigningKey();
		
		// * * * 4 * * * *

		oAuthSignature = getCalcShaHash(baseString, singningKey);
		
		// * * * Done * * * *

		return oAuthSignature;
	}
	
	String gentratedPerams(List<BasicNameValuePair> peramPairs){
		
		String GeneratedPerams = "";
  
		GeneratedPerams =   
				       OAUTH_CONSUMER_KEY  	     +"="	+ URLEncoder.encode(getcKey() )        
				+"&" + OAUTH_NONCE     			 +"="	+ URLEncoder.encode(currentOnonce )    
				+"&" + OAUTH_SIGNATURE_METHOD	 +"="	+ URLEncoder.encode(HMAC_SHA1 )        
				+"&" + OAUTH_TIMESTAMP           +"="   + URLEncoder.encode(currentTimeStamp ) 
				+"&" + OAUTH_TOKEN               +"="   + URLEncoder.encode(getAccesToken() )  
				+"&" + OAUTH_VERSION             +"="   + URLEncoder.encode(VERSION_1_0 );
          
		if(peramPairs.size()>0){
  			
			for( int i = 0 ;i<peramPairs.size();++i){
				
				GeneratedPerams = GeneratedPerams 
						
				    +"&" +peramPairs.get(i).getName()+"=" + URLEncoder.encode(peramPairs.get(i).getValue()).replace("+", "%20");
				    
			}
		}
		
		System.out.println("GeneratedPerams = "+ GeneratedPerams);

		return GeneratedPerams;
	}

	String generateBaseString(String peramsUrl)  {

		String baseString = null;

		System.out.println("URLEncoder.encode("+url+") = "+ URLEncoder.encode(url ));

		baseString = method+"&"+URLEncoder.encode(url )+"&"+URLEncoder.encode(peramsUrl );

		System.out.println("baseString = "+ baseString);

		return baseString;
	}	

	String genrateSigningKey(){

		String singningKey = null;

		singningKey = URLEncoder.encode(cSecret )+"&"+URLEncoder.encode(tokenSecret );

		System.out.println("genrateSigningKey = "+ singningKey);

		return singningKey;
	} 
 
	public String getAccesToken() {

		return accesToken;
	}

	public void setAccesToken(String accesToken) {
		this.accesToken = accesToken;
	}

	public String getAcessSecret() {
		return tokenSecret;
	}

	public void setAcessSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
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
		
		System.out.println("getOnonce = "+ oNonce);

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
			
 			System.out.println("getCalcShaHash = "+ oAuthSignature);

		} catch (Exception e) {

			e.printStackTrace(); 
			
			System.out.println("Exception = "+ e);

		}       

		return oAuthSignature;
	}
}
