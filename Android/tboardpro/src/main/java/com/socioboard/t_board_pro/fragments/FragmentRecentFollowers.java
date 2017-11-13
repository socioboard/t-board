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
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentRecentFollowers extends Fragment implements
		OnScrollListener {

	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	public ToFollowingAdapter toFollowingAdp;
	RelativeLayout reloutProgress;
	Activity aActivity;
	ArrayList<ToFollowingModel> rscFollowers = new ArrayList<ToFollowingModel>();
	ArrayList<String> rescentsIds = new ArrayList<String>();
	boolean isAlreadyScrolling = true;
	ViewGroup viewGroup;
	TboardproLocalData tboardproLocalData;

	Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MainSingleTon.mixpanelAPI.track("Fragment RecentFollowers oncreate called");

		aActivity = getActivity();

		rootView = inflater.inflate(R.layout.fragment_to_following, container,
				false);

		LoadAd();
		tboardproLocalData = new TboardproLocalData(getActivity());

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		listView = (ListView) rootView.findViewById(R.id.listViewToFollowing);

		listView.setOnScrollListener(this);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		showProgress();

		// * * * * * * * * * * * * * * Recent

		ArrayList<String> oldIds = new ArrayList<String>();

		try {

			JSONObject jsonObjectTMp = new JSONObject(
					tboardproLocalData
							.getAllFollowersIDs(MainSingleTon.currentUserModel
									.getUserid()));

			JSONArray jsonArrayTmp;

			jsonArrayTmp = new JSONArray(jsonObjectTMp.getString("ids"));

			for (int i = 0; i < jsonArrayTmp.length(); ++i) {

				oldIds.add(jsonArrayTmp.getString(i));

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		ArrayList<String> tmpIds = (ArrayList<String>) differenciate(
				MainSingleTon.listMyfollowersIDs, oldIds);

		MainSingleTon.recentsFollowersCount = tmpIds.size();

		rescentsIds = (ArrayList<String>) differenciate(
				MainSingleTon.listMyfollowersIDs, oldIds);

		if (rescentsIds.size() == 0) {

			cancelProgres();

		} else {

			rescentFollowers();

		}

		return rootView;
	}

	void LoadAd()
	{
		MobileAds.initialize(getActivity(), getString(R.string.adMob_app_id));
		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

	}

	public List<String> differenciate(List<String> a, List<String> b) {

		// difference a-b
		List<String> c = new ArrayList<String>(a.size());
		c.addAll(a);
		c.removeAll(b);

		return c;
	}

	private void addFooterView() {

		LayoutInflater inflater = getActivity().getLayoutInflater();

		viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
				listView, false);

		listView.addFooterView(viewGroup);

		myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

	}

	public void rescentFollowers() {

		TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

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
					}

				});

		String userswithComma = "";

		if (rscFollowers.size() >= rescentsIds.size()) {

			handler.post(new Runnable() {

				@Override
				public void run() {

					viewGroup.setVisibility(View.INVISIBLE);

				}
			});

		} else {

			for (int i = rscFollowers.size(); i < (rscFollowers.size() + 99); ++i) {

				if (i == rscFollowers.size()) {

					userswithComma = rescentsIds.get(i);

					myprint(i + "++++++++++ i first " + userswithComma);

				} else {

					try {

						userswithComma = userswithComma + ","
								+ rescentsIds.get(i);

						myprint(i + "++++++++++ i other " + userswithComma);

					} catch (Exception e) {
						break;
					}

				}

			}

			List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

			peramPairs
					.add(new BasicNameValuePair(Const.user_id, userswithComma));

			peramPairs.add(new BasicNameValuePair(Const.include_entities,
					"false"));

			twitterUserGETRequest.executeThisRequest(MainSingleTon.userShowIds,
					peramPairs);

		}

	}

	protected void parseJsonResultPaged(String jsonResult) {

		myprint("parseJsonResult  FragmentRecentFollowers11111");

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

						if (FragmentRecentFollowers.this.getActivity() != null) {

							int listCount = listView.getCount();

							toFollowingAdp.tweetModels.add(followingModel);

							listView.setScrollY(listCount);

							toFollowingAdp.notifyDataSetChanged();

							rscFollowers = toFollowingAdp.tweetModels;

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

			if (rscFollowers.size() >= rescentsIds.size()) {

				handler.post(new Runnable() {

					@Override
					public void run() {

						viewGroup.setVisibility(View.INVISIBLE);

					}
				});

			} else {

				for (int i = rscFollowers.size(); i < (rscFollowers.size() + 99); ++i) {

					if (i == rscFollowers.size()) {

						userswithComma = rescentsIds.get(i);

					} else {

						try {

							userswithComma = "," + rescentsIds.get(i);

						} catch (Exception e) {
							break;
						}

					}

				}

				List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

				peramPairs.add(new BasicNameValuePair(Const.user_id,
						userswithComma));

				peramPairs.add(new BasicNameValuePair(Const.include_entities,
						"false"));

				twitterUserGETRequest.executeThisRequest(
						MainSingleTon.userShowIds, peramPairs);

			}

			return null;
		}

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult  FragmentRecentFollowers222222");

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

				rscFollowers.add(followingModel);

				myprint(followingModel);

			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (FragmentRecentFollowers.this.getActivity() != null) {

						toFollowingAdp = new ToFollowingAdapter(getActivity(),
								rscFollowers, FragmentRecentFollowers.this
										.getActivity());

						listView.setAdapter(toFollowingAdp);

						isAlreadyScrolling = false;

					}

				}
			});

		} catch (JSONException e) {

			e.printStackTrace();

		}

		cancelProgres();

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

	}
}