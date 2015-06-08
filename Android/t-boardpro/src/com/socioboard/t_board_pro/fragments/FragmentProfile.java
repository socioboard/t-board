package com.socioboard.t_board_pro.fragments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.dialog.ShowTweetComposeDialog;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserShowRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.FullUserDetailModel;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.Utils;
import com.socioboard.tboardpro.R;

public class FragmentProfile extends Fragment {

	View rootView;
	Activity aActivity;
	RelativeLayout reloutProgress;
	FullUserDetailModel userDatas;
	Bitmap userBitmap;
	TboardproLocalData localData;
	ImageView imageView1Banner, profile;
	Button buttonTweet;

	TextView textView1Name, textView1UserName, textView1Tweets,
			TextView01Followings, TextView0FollowedBy, TextView03CreatedAT,
			textViewFavs;

	Handler handler = new Handler();

	String cretedAt, favs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		myprint("onCreateView  FragmentProfile");

		aActivity = getActivity();

		localData = new TboardproLocalData(FragmentProfile.this.getActivity()
				.getApplicationContext());

		rootView = inflater
				.inflate(R.layout.fragment_profile, container, false);

		textView1Name = (TextView) rootView.findViewById(R.id.textView1Name);

		profile = (ImageView) rootView.findViewById(R.id.imageView1Profile);

		buttonTweet = (Button) rootView.findViewById(R.id.button1Tweet);

		buttonTweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ShowTweetComposeDialog showTweetComposeDialog = new ShowTweetComposeDialog(
						getActivity(), "");

				showTweetComposeDialog.showThis();

			}
		});

		String userStringImage = MainSingleTon.currentUserModel.getUserimage();

		if (userStringImage != null) {

			Bitmap bitmap = Utils.decodeBase64(userStringImage);

			profile.setImageBitmap(bitmap);

		}

		textView1UserName = (TextView) rootView
				.findViewById(R.id.textView1UserName);

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		textView1UserName.setText("@"
				+ MainSingleTon.currentUserModel.getUsername());

		TextView01Followings = (TextView) rootView
				.findViewById(R.id.TextView01Followings);
		TextView0FollowedBy = (TextView) rootView
				.findViewById(R.id.TextView0FollowedBy);
		TextView03CreatedAT = (TextView) rootView
				.findViewById(R.id.TextView03CreatedAT);
		textView1Tweets = (TextView) rootView
				.findViewById(R.id.textView1Tweets);
		textViewFavs = (TextView) rootView.findViewById(R.id.textViewFavs);

		imageView1Banner = (ImageView) rootView
				.findViewById(R.id.imageView1Banner);

		if (MainSingleTon.bitmapBanner != null) {
			imageView1Banner.setImageBitmap(MainSingleTon.bitmapBanner);
		}
		showProgress();
		TwitterUserShowRequest userShowRequest = new TwitterUserShowRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

					@Override
					public void onSuccess(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						myprint("onSuccess " + jsonObject);
						parseJsonResultForAccountData(jsonObject);
					}

					@Override
					public void onSuccess(String jsonResult) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Exception e) {
						// TODO Auto-generated method stub

					}
				});

		userShowRequest.executeThisRequest(MainSingleTon.currentUserModel
				.getUsername());

		return rootView;
	}

	void myToastS(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
	}

	void myToastL(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	protected void parseJsonResultForAccountData(JSONObject jsonResult) {

		myprint("parseJsonResult  ");

		try {

			myprint("jsonResult   = " + jsonResult);

			FullUserDetailModel fullUserDetailModel = new FullUserDetailModel();

			fullUserDetailModel.setFollowingStatus(jsonResult.getString(Const.following).contains("true"));

			fullUserDetailModel.setFollowingStatus(jsonResult.getString(Const.following).contains("true"));

			fullUserDetailModel.setId(jsonResult.getString(Const.id_str));

			fullUserDetailModel.setFullName(jsonResult.getString(Const.name));

			fullUserDetailModel.setNoFollowers(jsonResult.getString(Const.followers_count));

			fullUserDetailModel.setNoToFollowing(jsonResult.getString(Const.friends_count));

			fullUserDetailModel.setNoTweets(jsonResult.getString(Const.statuses_count));

			fullUserDetailModel.setUserImagerUrl(jsonResult.getString(Const.profile_image_url));
			
			if (jsonResult.has(Const.profile_banner_url)) {

				myprint("Const.profile_banner_url  <"+ Const.profile_banner_url + ">");

				myprint("jsonResult .getString(Const.profile_banner_url)>" + jsonResult.getString(Const.profile_banner_url) + ">");

				fullUserDetailModel.setBannerUrl(jsonResult.getString(Const.profile_banner_url));

				fullUserDetailModel.setUserName("@" + jsonResult.getString(Const.screen_name));

				if (MainSingleTon.bitmapBanner == null) {
					
					new DownloadIamgeBanner().execute(fullUserDetailModel
							.getBannerUrl());
					
				}

			}

			fullUserDetailModel.setUserName("@"
					+ jsonResult.getString(Const.screen_name));

			cretedAt = jsonResult.getString(Const.created_at);

			favs = jsonResult.getString(Const.favourites_count);

			userDatas = fullUserDetailModel;

			myprint(fullUserDetailModel);

			showTexts(fullUserDetailModel);

			new DownloadIamge().execute(userDatas.getUserImagerUrl());

		} catch (JSONException e) {

			e.printStackTrace();

		}

		// cancelProgres();

	}

	private void showTexts(final FullUserDetailModel fullUserDetailModel) {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				textView1Name.setText(fullUserDetailModel.getFullName());
				textView1Tweets.setText(fullUserDetailModel.getNoTweets());
				TextView01Followings.setText(fullUserDetailModel
						.getNoToFollowing());
				TextView0FollowedBy.setText(fullUserDetailModel
						.getNoFollowers());
				TextView03CreatedAT.setText(cretedAt);
				textViewFavs.setText(favs);

			}
		});

	}

	void showProgress() {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				reloutProgress.setVisibility(View.VISIBLE);
			}

		});

	}

	void cancelProgres() {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				reloutProgress.setVisibility(View.INVISIBLE);
			}
		});
	}

	// + + + + + + + + + + +

	class DownloadIamge extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String urlImg = params[0].toString();

			URL url;

			Bitmap userBitImage = null;

			try {

				url = new URL(urlImg);

				userBitImage = BitmapFactory.decodeStream(url.openStream());

				myprint("Download cPOmpleteas");

				if (userBitImage != null) {

					savingStringImage(userDatas, userBitImage);

				}

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}

			return null;
		}

	}

	void savingStringImage(FullUserDetailModel userDatas,
			final Bitmap userBitImage) {

		String stringBitpmap = Utils.encodeTobase64(userBitImage);

		myprint("converted");

		MainSingleTon.currentUserModel.setUserimage(stringBitpmap);

		localData.updateUserData(MainSingleTon.currentUserModel);

		MainSingleTon.currentUserModel.setUserimage(stringBitpmap);

		userBitmap = userBitImage;

		handler.post(new Runnable() {

			@Override
			public void run() {

				cancelProgres();

				profile.setImageBitmap(userBitImage);

			}
		});

		myprint("Saved");
	}

	class DownloadIamgeBanner extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String urlImg = params[0].toString();

			URL url;

			Bitmap userBitBanner = null;

			try {

				url = new URL(urlImg);

				userBitBanner = BitmapFactory.decodeStream(url.openStream());

				MainSingleTon.bitmapBanner = userBitBanner;

				aActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// imageView1Banner
						// .setImageBitmap(MainSingleTon.bitmapBanner);
						imageView1Banner
								.setImageBitmap(MainSingleTon.bitmapBanner);
					}
				});

				myprint("Banner downloaded");

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}
			return null;
		}

	}

	// + + + + + + + + + + +

}
