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
import com.socioboard.t_board_pro.adapters.TweetsAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TweetModel;
import com.socioboard.tboardpro.R;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavourites extends Fragment implements
		TwitterRequestCallBack, OnScrollListener {

	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	ArrayList<TweetModel> listFavtes = new ArrayList<TweetModel>();
	TweetsAdapter twtAdpr;
	RelativeLayout reloutProgress;
	Activity aActivity;
	Handler handler = new Handler();
	ViewGroup viewGroup;
	boolean isAlreadyScrolling = true;
	String nextCursor = "-1";
	String reqFetchedID = "reqFetchedID";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MainSingleTon.mixpanelAPI.track("Fragment Favourites oncreate called");

		rootView = inflater.inflate(R.layout.fragment_favorite, container,
				false);

		LoadAd();

		aActivity = getActivity();

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		listView = (ListView) rootView.findViewById(R.id.timelineListView);

		listView.setOnScrollListener(FragmentFavourites.this);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, this);

		List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

		peramPairs.add(new BasicNameValuePair(Const.count, "10"));

		peramPairs.add(new BasicNameValuePair(Const.include_entities, "false"));

		twitterUserGETRequest.executeThisRequest(MainSingleTon.twtFavourites,
				peramPairs);

		return rootView;

	}

	void LoadAd()
	{
		MobileAds.initialize(getActivity(), getString(R.string.adMob_app_id));
		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult FragmentFavourites1 ");

		try {

			JSONArray jsonArray = new JSONArray(jsonResult);

			for (int i = 0; i < jsonArray.length(); ++i) {

				TweetModel tweetModel = new TweetModel();

				try {

					JSONObject jsonObjectk2 = jsonArray.getJSONObject(i);

					myprint("jsonObjectk2  " + jsonObjectk2);

					tweetModel
							.setTweeet_str(jsonObjectk2.getString(Const.text));

					tweetModel.setIsfavourated(jsonObjectk2
							.getBoolean(Const.favorited));

					tweetModel.setRetweeted(jsonObjectk2
							.getBoolean(Const.retweeted));

					tweetModel.setTweetTime(jsonObjectk2
							.getString(Const.created_at));

					tweetModel.setFavCount(new Long(jsonObjectk2
							.getString(Const.favorite_count)));

					tweetModel.setRetweetCount(new Long(jsonObjectk2
							.getString(Const.retweet_count)));

					tweetModel.setTweetId(jsonObjectk2.getString(Const.id));

					JSONObject jsonObject3 = jsonObjectk2
							.getJSONObject(Const.user);

					tweetModel.setUserImagerUrl(jsonObject3
							.getString(Const.profile_image_url));

					tweetModel.setUserName("@"
							+ jsonObject3.getString(Const.screen_name));

					tweetModel.setUserID(jsonObject3.getString(Const.id));

					tweetModel.setFullName(jsonObject3.getString(Const.name));

					tweetModel.setFollowing(jsonObject3
							.getBoolean(Const.following));

					if (jsonObjectk2.has("extended_entities")) {

						JSONObject jsonObjectEntities = jsonObjectk2
								.getJSONObject("extended_entities");

						System.out.println("***** jsonObjectEntities  *****"
								+ jsonObjectEntities);

						System.out
								.println("***** jsonObjectk2.has(Const.media) *****");

						JSONArray jsonArray2Media = jsonObjectEntities
								.getJSONArray(Const.media);

						System.out.println("***** jsonArray2Media *****");

						JSONObject jsonObjectMedia = jsonArray2Media
								.getJSONObject(0);

						System.out.println("***** jsonObjectMedia *****"
								+ jsonObjectMedia);

						tweetModel.setMediaImagerUrl(jsonObjectMedia
								.getString(Const.media_url));

					} else {

						System.out
								.println("***** Noooooo jsonObjectk2.has(Const.media) *****");

						tweetModel.setMediaImagerUrl("");

					}

					listFavtes.add(tweetModel);

					myprint(tweetModel);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		if (FragmentFavourites.this.getActivity() != null) {

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					twtAdpr = new TweetsAdapter(listFavtes,
							FragmentFavourites.this.getActivity());

					listView.setAdapter(twtAdpr);

					myprint("listView.setAdapter(twtAdpr);");

					isAlreadyScrolling = false;

				}

			});
		}

		cancelProgres();

	}

	protected void parseJsonResultPaged(String jsonResult) {

		handler.post(new Runnable() {

			@Override
			public void run() {

				viewGroup.setVisibility(View.INVISIBLE);

			}
		});

		try {

			JSONArray jsonArray = new JSONArray(jsonResult);

			for (int i = 0; i < jsonArray.length(); ++i) {

				final TweetModel tweetModel = new TweetModel();

				try {

					JSONObject jsonObjectk2 = jsonArray.getJSONObject(i);

					tweetModel
							.setTweeet_str(jsonObjectk2.getString(Const.text));

					tweetModel.setIsfavourated(jsonObjectk2
							.getBoolean(Const.favorited));

					tweetModel.setRetweeted(jsonObjectk2
							.getBoolean(Const.retweeted));

					tweetModel.setTweetTime(jsonObjectk2
							.getString(Const.created_at));

					tweetModel.setFavCount(new Long(jsonObjectk2
							.getString(Const.favorite_count)));

					tweetModel.setRetweetCount(new Long(jsonObjectk2
							.getString(Const.retweet_count)));

					tweetModel.setTweetId(jsonObjectk2.getString(Const.id));

					JSONObject jsonObject3 = jsonObjectk2
							.getJSONObject(Const.user);

					tweetModel.setUserImagerUrl(jsonObject3
							.getString(Const.profile_image_url));

					tweetModel.setUserName("@"
							+ jsonObject3.getString(Const.screen_name));

					tweetModel.setUserID(jsonObject3.getString(Const.id));

					tweetModel.setFullName(jsonObject3.getString(Const.name));

					tweetModel.setFollowing(jsonObject3
							.getBoolean(Const.following));

					if (jsonObjectk2.has("extended_entities")) {

						JSONObject jsonObjectEntities = jsonObjectk2
								.getJSONObject("extended_entities");

						System.out.println("***** jsonObjectEntities  *****"
								+ jsonObjectEntities);

						System.out
								.println("***** jsonObjectk2.has(Const.media) *****");

						JSONArray jsonArray2Media = jsonObjectEntities
								.getJSONArray(Const.media);

						System.out.println("***** jsonArray2Media *****");

						JSONObject jsonObjectMedia = jsonArray2Media
								.getJSONObject(0);

						System.out.println("***** jsonObjectMedia *****"
								+ jsonObjectMedia);

						tweetModel.setMediaImagerUrl(jsonObjectMedia
								.getString(Const.media_url));

					} else {

						System.out
								.println("***** Noooooo jsonObjectk2.has(Const.media) *****");

						tweetModel.setMediaImagerUrl("");

					}
					// listMyfollowers.add(tweetModel);

					handler.post(new Runnable() {

						@Override
						public void run() {

							if (FragmentFavourites.this.getActivity() != null) {

								int listCount = listView.getCount();

								twtAdpr.tweetModels.add(tweetModel);

								listView.setScrollY(listCount);

								twtAdpr.notifyDataSetChanged();

							}
						}
					});

					myprint(tweetModel);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

		} catch (JSONException e) {

			e.printStackTrace();

		}

		isAlreadyScrolling = false;

	}

	public class FetchReqPaged extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String madMaxId = params[0].toString();

			String urlTimeline = MainSingleTon.twtFavourites;

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

							handler.post(new Runnable() {

								@Override
								public void run() {

									viewGroup.setVisibility(View.INVISIBLE);

								}
							});
						}

						@Override
						public void onSuccess(JSONObject jsonObject) {
						}
					});

			List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

			peramPairs.add(new BasicNameValuePair(Const.max_id, madMaxId));

			peramPairs.add(new BasicNameValuePair(Const.count, "10"));

			peramPairs.add(new BasicNameValuePair(Const.include_entities,
					"false"));

			twitterUserGETRequest.executeThisRequest(urlTimeline, peramPairs);

			return null;
		}

	}

	private void addFooterView() {

		LayoutInflater inflater = getActivity().getLayoutInflater();

		viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
				listView, false);

		listView.addFooterView(viewGroup);

		myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

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
	public void onSuccess(String jsonResult) {
		// TODO Auto-generated method stub
		myprint("onSuccess jsonResult " + jsonResult);
		parseJsonResult(jsonResult);
	}

	@Override
	public void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFailure(Exception e) {
		// TODO Auto-generated method stub

		myprint("onFailure e " + e);

		handler.post(new Runnable() {

			@Override
			public void run() {

				viewGroup.setVisibility(View.INVISIBLE);

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

				isAlreadyScrolling = true;

				try {
					
					String madMaxId = ""
							+ twtAdpr.getItem(twtAdpr.getCount() - 1)
									.getTweetId();

					if (reqFetchedID.contains(madMaxId)) {

					} else {

						viewGroup.setVisibility(View.VISIBLE);

						myprint(twtAdpr.getItem(twtAdpr.getCount() - 1));

						new FetchReqPaged().execute(madMaxId);

						reqFetchedID = madMaxId;

					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		} else {

			myprint("NOOOOOOOOO DONt LOad");

		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

}
