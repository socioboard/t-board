package com.socioboard.t_board_pro.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.socioboard.t_board_pro.adapters.ToFollowingAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentMutualFollowers extends Fragment implements OnScrollListener {

	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	public ToFollowingAdapter toFollowingAdp;
	RelativeLayout reloutProgress;
	Activity aActivity;
	ArrayList<ToFollowingModel> mutualFollowers = new ArrayList<ToFollowingModel>();

	boolean isAlreadyScrolling = true;

	ViewGroup viewGroup;

	Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MainSingleTon.mixpanelAPI.track("Fragment MutualFollowers oncreate called");

		aActivity = getActivity();

		rootView = inflater.inflate(R.layout.fragment_to_following, container, false);

		LoadAd();
		
		reloutProgress = (RelativeLayout) rootView .findViewById(R.id.reloutProgress);

		listView = (ListView) rootView.findViewById(R.id.listViewToFollowing);

		listView.setOnScrollListener(this);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		new FanFollowing().execute();

		return rootView;

	}

	void LoadAd()
	{
		MobileAds.initialize(getActivity(), getString(R.string.adMob_app_id));
		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

	}

	private void addFooterView() {

		LayoutInflater inflater = getActivity().getLayoutInflater();

		viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout, listView, false);

		listView.addFooterView(viewGroup);

		myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

	}
 
	public class FanFollowing extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {

			showProgress();

			TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
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

							cancelProgres();

						}

						@Override
						public void onSuccess(JSONObject jsonObject) {

							myprint("onSuccess JSONObject " + jsonObject);
							
							cancelProgres();

						}

					});

			String userswithComma = "";

			if (mutualFollowers.size() >= MainSingleTon.mutualsIds.size()) {

				handler.post(new Runnable() {

					@Override
					public void run() {

						viewGroup.setVisibility(View.INVISIBLE);

					}
				});

			} else {

				for (int i = mutualFollowers.size(); i < (mutualFollowers.size() + 99); ++i) {

					if (i == mutualFollowers.size()) {

						userswithComma = MainSingleTon.mutualsIds.get(i);

						myprint(i + "++++++++++ i first " + userswithComma);

					} else {

						try {

							userswithComma = userswithComma + ","
									+ MainSingleTon.mutualsIds.get(i);

							myprint(i + "++++++++++ i other " + userswithComma);

						} catch (Exception e) {
							break;
						}

					}

				}

				List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

				peramPairs.add(new BasicNameValuePair(Const.user_id,
						userswithComma));
				
				peramPairs.add(new BasicNameValuePair(Const.include_entities, "false"));

				twitterUserGETRequest.executeThisRequest(
						MainSingleTon.userShowIds, peramPairs);

			}

			return null;

		}

	}

	protected void parseJsonResultPaged(String jsonResult) {

		myprint("parseJsonResult  FragmentMutualFollowers11");

		handler.post(new Runnable() {

			@Override
			public void run() {

				viewGroup.setVisibility(View.INVISIBLE);

			}
		});

		try {

			JSONObject jsonObject = new JSONObject(jsonResult);

			JSONArray jsonArray = jsonObject.getJSONArray("users");

			for (int i = 0; i < jsonArray.length(); ++i) {

				JSONObject jsonObject2 = jsonArray.getJSONObject(i);

				myprint("jsonObject2 " + i + " = " + jsonObject2);

				final ToFollowingModel followingModel = new ToFollowingModel();

				followingModel.setFollowingStatus(jsonObject2.getString(
						Const.following).contains("true"));

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

				followingModel.setUserName("@"
						+ jsonObject2.getString(Const.screen_name));

				myprint(followingModel);

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {

						if (FragmentMutualFollowers.this.getActivity() != null) {

							int listCount = listView.getCount();

							toFollowingAdp.tweetModels.add(followingModel);

							listView.setScrollY(listCount);

							toFollowingAdp.notifyDataSetChanged();

							mutualFollowers = toFollowingAdp.tweetModels;

						}
					}
				});

			}

		} catch (JSONException e) {

			e.printStackTrace();

		}

		isAlreadyScrolling = false;

	}

	public class FetchReqPaged extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
					MainSingleTon.currentUserModel,
					new TwitterRequestCallBack() {

						@Override
						public void onSuccess(String jsonResult) {

							myprint("onSuccess jsonResult " + jsonResult);

							parseJsonResultPaged(jsonResult);

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

			String userswithComma = "";

			if (mutualFollowers.size() >= MainSingleTon.mutualsIds.size()) {

				handler.post(new Runnable() {

					@Override
					public void run() {

						viewGroup.setVisibility(View.INVISIBLE);

					}
				});

			} else {

				for (int i = mutualFollowers.size(); i < (mutualFollowers.size() + 99); ++i) {

					if (i == mutualFollowers.size()) {

						userswithComma = MainSingleTon.mutualsIds.get(i);

					} else {

						try {

							userswithComma = "," + MainSingleTon.mutualsIds.get(i);

						} catch (Exception e) {
							break;
						}

					}

				}

				List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

				peramPairs.add(new BasicNameValuePair(Const.user_id,
						userswithComma));
				
				peramPairs.add(new BasicNameValuePair(Const.include_entities, "false"));

				twitterUserGETRequest.executeThisRequest(
						MainSingleTon.userShowIds, peramPairs);

			}

			return null;
		}

	}

	protected void parseJsonResult(String jsonResult) {
	
		cancelProgres();

		myprint("parseJsonResult  FragmentMutualFollowers22");

		try {

			JSONArray jsonArray = new JSONArray(jsonResult);

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

				followingModel.setUserName("@"
						+ jsonObject2.getString(Const.screen_name));

				mutualFollowers.add(followingModel);

				myprint(followingModel);

			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (FragmentMutualFollowers.this.getActivity() != null) {

						toFollowingAdp = new ToFollowingAdapter(getActivity(),
								mutualFollowers, FragmentMutualFollowers.this.getActivity());

						listView.setAdapter(toFollowingAdp);

						isAlreadyScrolling = false;

					}

				}
			});

		} catch (JSONException e) {

			e.printStackTrace();

		}


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

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		/* maybe add a padding */

		boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

		if (loadMore) {

			myprint("YESSSSSSSSSSSSS load MOOOOOOOOOREE");

			if (isAlreadyScrolling) {

				// DO NOTHING
				myprint("BUT isAlreadyScrolling ");

			} else {

				viewGroup.setVisibility(View.VISIBLE);

				isAlreadyScrolling = true;

				myprint(toFollowingAdp.getItem(toFollowingAdp.getCount() - 1));

				new FetchReqPaged().execute();

			}

		} else {

			myprint("NOOOOOOOOO DONt LOad");

		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
