package com.socioboard.t_board_pro.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.socioboard.t_board_pro.MainActivity;
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
import java.util.Timer;
import java.util.TimerTask;

public class FragmentTimeLineMine extends Fragment implements OnScrollListener {

	View rootView;

	ListView listView;

	Bitmap userImage, userbannerImage;

	TweetsAdapter twtAdpr;

	RelativeLayout reloutProgress;

	Activity aActivity;

	Timer timer = new Timer();

	Handler handler = new Handler();

	boolean isAlreadyScrolling = true;

	ViewGroup viewGroup;

	public ArrayList<TweetModel> loadedtweets = new ArrayList<TweetModel>();

	private SwipeRefreshLayout swipeContainer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MainSingleTon.mixpanelAPI.track("Fragment TimeLineMine oncreate called");

		rootView = inflater.inflate(R.layout.fragment_timelineview, container,
				false);

		//LoadAd();

		aActivity = FragmentTimeLineMine.this.getActivity();

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		listView = (ListView) rootView.findViewById(R.id.timelineListView);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		listView.setOnScrollListener(FragmentTimeLineMine.this);

		twtAdpr = new TweetsAdapter(loadedtweets,
				FragmentTimeLineMine.this.getActivity());

		listView.setAdapter(twtAdpr);

		MainActivity.showActionBarProgress();

		myprint("++++++++Load Frst time");

		FetchTimeline();

		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				FragmentTimeLineMine.this.getActivity().runOnUiThread(
						new Runnable() {

							@Override
							public void run() {

								twtAdpr.notifyDataSetChanged();

							}
						});

			}
		}, 2000, 60000);

		swipeContainer = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swipeContainer);

		// Setup refresh listener which triggers new data loading

		swipeContainer.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {

				// Your code to refresh the list here.
				// Make sure you call swipeContainer.setRefreshing(false)
				// once the network request has completed successfully.

				swipeContainer.setRefreshing(false);

				if (isAlreadyScrolling) {

				} else {

					cancelProgres();

					MainActivity.showActionBarProgress();

					myprint("+++++++++further Loading");

					FetchTimelineLatestPaged(loadedtweets.get(0).getTweetId());

				}
			}
		});

		// Configure the refreshing colors
		swipeContainer.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		return rootView;
	}

//	void LoadAd()
//	{
//		MobileAds.initialize(getActivity(), getString(R.string.adMob_app_id));
//		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
//		AdRequest adRequest = new AdRequest.Builder().build();
//		mAdView.loadAd(adRequest);
//
//	}

	private void addFooterView() {

		LayoutInflater inflater = FragmentTimeLineMine.this.getActivity()
				.getLayoutInflater();

		viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
				listView, false);

		listView.addFooterView(viewGroup);

		myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

	}

	@Override
	public void onDestroy() {

		super.onDestroy();

		timer.cancel();

	}

	public void FetchTimeline() {

		TwitterUserGETRequest twitterTimeLineRequest = new TwitterUserGETRequest(
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

						isAlreadyScrolling = false;
					}

					@Override
					public void onSuccess(JSONObject jsonObject) {

					}
				});

		List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

		peramPairs.add(new BasicNameValuePair(Const.user_id,
				MainSingleTon.currentUserModel.getUserid()));

		peramPairs.add(new BasicNameValuePair(Const.count, "10"));

		peramPairs.add(new BasicNameValuePair(Const.include_entities, "false"));

		twitterTimeLineRequest.executeThisRequest(MainSingleTon.user_timeline,
				peramPairs);

	}

	public void FetchTimelinePaged(final String madMaxId) {

		TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

					@Override
					public void onSuccess(String jsonResult) {

						myprint("onSuccess jsonResult " + jsonResult);

						parseJsonResultPaged(jsonResult, madMaxId);
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

		peramPairs.add(new BasicNameValuePair(Const.user_id,
				MainSingleTon.currentUserModel.getUserid()));

		peramPairs.add(new BasicNameValuePair(Const.max_id, madMaxId));

		peramPairs.add(new BasicNameValuePair(Const.count, "10"));

		peramPairs.add(new BasicNameValuePair(Const.include_entities, "false"));

		twitterUserGETRequest.executeThisRequest(MainSingleTon.user_timeline,
				peramPairs);

	}

	public void FetchTimelineLatestPaged(String madMaxId) {

		TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

					@Override
					public void onSuccess(String jsonResult) {

						myprint("onSuccess jsonResult " + jsonResult);

						MainActivity.HideActionBarProgress();

						parseJsonResultPagedLatest(jsonResult);

					}

					@Override
					public void onFailure(Exception e) {

						myprint("onFailure e " + e);

						MainActivity.HideActionBarProgress();

					}

					@Override
					public void onSuccess(JSONObject jsonObject) {

					}

				});

		List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

		peramPairs.add(new BasicNameValuePair(Const.user_id,
				MainSingleTon.currentUserModel.getUserid()));

		peramPairs.add(new BasicNameValuePair(Const.since_id, madMaxId));

		peramPairs.add(new BasicNameValuePair(Const.count, "10"));

		peramPairs.add(new BasicNameValuePair(Const.include_entities, "false"));

		twitterUserGETRequest.executeThisRequest(MainSingleTon.user_timeline,
				peramPairs);

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult FragmentTimeLineMine ");

		try {

			JSONArray jsonArray = new JSONArray(jsonResult);

			for (int i = 0; i < jsonArray.length(); ++i) {

				TweetModel tweetModel = new TweetModel();

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

					System.out.println("***** jsonObjectk2  *****"
							+ jsonObjectk2);

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

					loadedtweets.add(tweetModel);

					myprint(tweetModel);

				} catch (JSONException e) {

					e.printStackTrace();

				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		handler.post(new Runnable() {

			@Override
			public void run() {

				cancelProgres();

				if (FragmentTimeLineMine.this.getActivity() != null) {

					twtAdpr = new TweetsAdapter(loadedtweets,
							FragmentTimeLineMine.this.getActivity());

					listView.setAdapter(twtAdpr);

					myprint("listView.setAdapter(twtAdpr);");

					isAlreadyScrolling = false;

				}
			}

		});

	}

	protected void parseJsonResultPaged(String jsonResult, String madMaxId) {

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

					tweetModel.setTweetId(jsonObjectk2.getString(Const.id));

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

					// listMyfollowers.add(tweetModel);

					System.out.println("***** jsonObjectk2  *****"
							+ jsonObjectk2);

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

					handler.post(new Runnable() {

						@Override
						public void run() {

							if (FragmentTimeLineMine.this.getActivity() != null) {

								int listCount = listView.getCount();

								twtAdpr.tweetModels.add(tweetModel);

								listView.setScrollY(listCount);

								twtAdpr.notifyDataSetChanged();

								loadedtweets = twtAdpr.tweetModels;

							}
						}
					});

					myprint(tweetModel);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			isAlreadyScrolling = false;

		} catch (JSONException e) {

			e.printStackTrace();

		}

	}

	protected void parseJsonResultPagedLatest(String jsonResult) {

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

					// listMyfollowers.add(tweetModel);

					System.out.println("***** jsonObjectk2  *****"
							+ jsonObjectk2);

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

					final int indexed = i;

					handler.post(new Runnable() {

						@Override
						public void run() {

							if (FragmentTimeLineMine.this.getActivity() != null) {

								int listCount = listView.getCount();

								twtAdpr.tweetModels.add(indexed, tweetModel);

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

			isAlreadyScrolling = false;

		} catch (JSONException e) {

			e.printStackTrace();

		}

	}

	void myToastS(final String toastMsg) {

		Toast.makeText(FragmentTimeLineMine.this.getActivity(), toastMsg,
				Toast.LENGTH_SHORT).show();
	}

	void myToastL(final String toastMsg) {

		Toast.makeText(FragmentTimeLineMine.this.getActivity(), toastMsg,
				Toast.LENGTH_LONG).show();
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	void showProgress() {

		MainActivity.HideActionBarProgress();

	}

	void cancelProgres() {

		MainActivity.HideActionBarProgress();

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

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

				if (twtAdpr.getCount() != 0) {

					System.out.println("Twitter count=="+twtAdpr.getCount());

					if (twtAdpr.getCount() % 10 == 0) {

						System.out.println("Twitter count12=="+twtAdpr.getCount()%10);

						String madMaxId = ""
								+ twtAdpr.getItem(twtAdpr.getCount() - 1)
										.getTweetId();

						myprint(twtAdpr.getItem(twtAdpr.getCount() - 1));

						System.out.println("Twitter count123=="+madMaxId);

						FetchTimelinePaged(madMaxId);

					} else {

						viewGroup.setVisibility(View.INVISIBLE);

						isAlreadyScrolling = true;

					}

				} else {

					myprint("twtAdpr.getCount() == 0 -->" + twtAdpr.getCount());

				}
			}

		} else {

			myprint("NOOOOOOOOO DONt LOad");

		}

	}

}
