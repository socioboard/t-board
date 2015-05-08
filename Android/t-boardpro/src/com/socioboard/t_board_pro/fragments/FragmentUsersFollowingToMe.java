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

import com.socioboard.t_board_pro.adapters.MyFollowersAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterTimeLineRequest2;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.MyFollowersModel;
import com.socioboard.tboardpro.R;

public class FragmentUsersFollowingToMe extends Fragment {

	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	ArrayList<MyFollowersModel> listMyfollowers = new ArrayList<MyFollowersModel>();
	MyFollowersAdapter myFollowersAdapter;
	RelativeLayout reloutProgress;
	Activity aActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_my_followers, container,
				false);
		
		aActivity = getActivity();

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

 		listView = (ListView) rootView.findViewById(R.id.listViewMyfolowers);
   
		new FollowingToMe().execute();

		return rootView;

	}

	public class FollowingToMe extends AsyncTask<Void, Void, Void> {

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
							// TODO Auto-generated method stub

						}
					});

			twitterTimeLineRequest
					.doInBackground(MainSingleTon.users_following_to_me);

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

				MyFollowersModel myFollowersModel = new MyFollowersModel();

				myFollowersModel.setFollowingStatus(jsonObject2.getString(Const.following).contains("true"));

 				myFollowersModel.setUserImagerUrl(jsonObject2.getString(Const.profile_image_url));

				myFollowersModel.setUserimage(null);

				myFollowersModel.setUserName("@" + jsonObject2.getString(Const.screen_name));
				
				myFollowersModel.setId( jsonObject2.getString(Const.id_str)); 

				listMyfollowers.add(myFollowersModel);

				myprint(myFollowersModel);

			}

		getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
					myFollowersAdapter = new MyFollowersAdapter(getActivity(),
							listMyfollowers,FragmentUsersFollowingToMe.this.getActivity());

 					listView.setAdapter(myFollowersAdapter);

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
