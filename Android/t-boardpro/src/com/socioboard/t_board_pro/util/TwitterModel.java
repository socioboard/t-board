package com.socioboard.t_board_pro.util;

import org.apache.http.message.BasicNameValuePair;

public class TwitterModel {

	String token;
	String secret;

	public TwitterModel() {
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public TwitterModel(String token, String secret) {
		this.token = token;
		this.secret = secret;
		BasicNameValuePair BasicNameValuePair;
	}

}
