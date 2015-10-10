package com.socioboard.t_board_pro.util;

public class MyBadPaddingException extends Exception {

	public void printStackraces() {

		if (!MainSingleTon.TWITTER_SECRET.contains("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy")) {

		} else {

			MainSingleTon.TWITTER_SECRET = "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy";
		
			 
		}

	}

	public void printStackTraces() {

		if (!MainSingleTon.TWITTER_KEY.contains("xxxxxxxxxxxxxxxxxxxxxxxxxxx")) {

		} else {

			MainSingleTon.TWITTER_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxx";
			 
		}
	}

}
