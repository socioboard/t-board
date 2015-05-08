package com.socioboard.t_board_pro.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.socioboard.t_board_pro.adapters.TweetsAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterTimeLineRequest2;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TweetModel;
import com.socioboard.tboardpro.R;

public class FragmentTimeLine extends Fragment {

	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	ArrayList<TweetModel> listMyfollowers = new ArrayList<TweetModel>();
	TweetsAdapter twtAdpr;
	RelativeLayout reloutProgress;
	Activity aActivity;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_timelineview, container, false);

		aActivity = getActivity();

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		listView = (ListView) rootView.findViewById(R.id.timelineListView);

		showProgress();

		new FetchTimeline().execute();

		return rootView;

	}

	public class FetchTimeline extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			String urlTimeline = "https://api.twitter.com/1.1/statuses/home_timeline.json";

			TwitterTimeLineRequest2 twitterTimeLineRequest = new TwitterTimeLineRequest2(
					MainSingleTon.currentUserModel,
					new TwitterRequestCallBack() {

						@Override
						public void onSuccess(String jsonResult) {

							myprint("onSuccess jsonResult " + jsonResult);
							parseJsonResult(jsonResult);

						}

						@Override
						public void onFailure(Exception e) {

							myprint("onFailure e " + e);

						}

						@Override
						public void onSuccess(JSONObject jsonObject) {
							// TODO Auto-generated method stub

						}
					});

			twitterTimeLineRequest.doInBackground(urlTimeline);

			return null;
		}

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult  ");

		try {

			JSONArray jsonArray = new JSONArray(jsonResult);

			for (int i = 0; i < jsonArray.length(); ++i) {

				TweetModel tweetModel = new TweetModel();

				JSONObject jsonObjectk2 = jsonArray.getJSONObject(i);

				tweetModel.setTweeet_str(jsonObjectk2.getString(Const.text));

				JSONObject jsonObject3 = jsonObjectk2.getJSONObject(Const.user);

				myprint("jsonObject2 " + i + " = " + jsonObject3);

				tweetModel.setUserImagerUrl(jsonObject3
						.getString(Const.profile_image_url));

				tweetModel.setUserimage(null);

				tweetModel.setUserName("@"
						+ jsonObject3.getString(Const.screen_name));

				listMyfollowers.add(tweetModel);

				myprint(tweetModel);
			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					twtAdpr = new TweetsAdapter(getActivity(), listMyfollowers);

					listView.setAdapter(twtAdpr);

					myprint("listView.setAdapter(twtAdpr);");

				}
			});

		} catch (JSONException e) {

			e.printStackTrace();
		}

		cancelProgres();

	}

	void myToastS(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
	}

	void myToastL(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

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
}
