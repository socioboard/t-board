package com.socioboard.t_board_pro.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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

import com.socioboard.t_board_pro.adapters.ToFollowingAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterTimeLineRequest2;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

public class FragmentIAMFollowingTo extends Fragment {

	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	ArrayList<ToFollowingModel> toFollowingModels = new ArrayList<ToFollowingModel>();
	ToFollowingAdapter toFollowingAdp;
	RelativeLayout reloutProgress;
	Activity aActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		aActivity = getActivity();

		rootView = inflater.inflate(R.layout.fragment_to_following, container,
				false);
		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		listView = (ListView) rootView.findViewById(R.id.listViewToFollowing);

		new ToFollowing().execute();

		return rootView;

	}

	public class ToFollowing extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			
			showProgress();
			
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

							myprint("onSuccess JSONObject " + jsonObject);

						}

					});

			twitterTimeLineRequest
					.doInBackground(MainSingleTon.i_am_following_to);

			return null;

		}

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult  ");

		try {

			JSONObject jsonObject = new JSONObject(jsonResult);

			JSONArray jsonArray = jsonObject.getJSONArray("users");

			for (int i = 0; i < jsonArray.length(); ++i) {

				JSONObject jsonObject2 = jsonArray.getJSONObject(i);

				myprint("jsonObject2 " + i + " = " + jsonObject2);

				ToFollowingModel followingModel = new ToFollowingModel();

				followingModel.setFollowingStatus(true);

				followingModel.setId(jsonObject2.getString(Const.id_str));

				followingModel.setNoFollowers(jsonObject2
						.getString(Const.followers_count));

				followingModel.setNoToFollowing(jsonObject2
						.getString(Const.friends_count));

				followingModel.setNoTweets(jsonObject2
						.getString(Const.listed_count));

				followingModel.setTweeet_str("");

				followingModel.setUserImagerUrl(jsonObject2
						.getString(Const.profile_image_url));

				followingModel.setUserimage(null);

				followingModel.setUserName("@"
						+ jsonObject2.getString(Const.screen_name));

				toFollowingModels.add(followingModel);

				myprint(followingModel);

			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
					toFollowingAdp = new ToFollowingAdapter(getActivity(),
							toFollowingModels,FragmentIAMFollowingTo.this.getActivity());

					
					listView.setAdapter(toFollowingAdp);

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