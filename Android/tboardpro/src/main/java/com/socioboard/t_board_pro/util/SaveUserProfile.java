package com.socioboard.t_board_pro.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class SaveUserProfile {

	FullUserDetailModel userDatas;

	Context context;

	TwitterRequestCallBack callBack;
	
	public SaveUserProfile(FullUserDetailModel userDatas, Context context) {

		this.userDatas = userDatas;
		this.context = context;

	}

	class DownloadIamge extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {

			String urlImg = params[0].toString();

			URL url;

			Bitmap userBitImage = null;

			try {

				url = new URL(urlImg);

				userBitImage = BitmapFactory.decodeStream(url.openStream());

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}

			return userBitImage;
		}

		@Override
		protected void onPostExecute(Bitmap userBitImage) {

			super.onPostExecute(userBitImage);

			if (userBitImage != null) {

				savingStringImage(userBitImage);
			}

		}
	}
	

	void savingStringImage(Bitmap userBitImage) {

		TboardproLocalData localData = new TboardproLocalData(context);

		String stringBitpmap = Utils.encodeTobase64(userBitImage);

		localData.updateUserDataField(userDatas.getId(),
				TboardproLocalData.KEY_Userimage, stringBitpmap);

	}
	
	void downloadAndsaveImage(){
		
		new DownloadIamge().execute(userDatas.userImagerUrl);
	}
	 
	

}
