package com.socioboard.t_board_pro.fragments;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.adapters.SearchAdapter;
import com.socioboard.t_board_pro.adapters.TweetsAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TweetModel;
import com.socioboard.tboardpro.R;

public class FragmentHashTagSearch extends Fragment implements
		TwitterRequestCallBack, OnScrollListener {

	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	ArrayList<TweetModel> listTaggedTweets = new ArrayList<TweetModel>();
	TweetsAdapter twtAdpr;
	RelativeLayout reloutProgress;
	Activity aActivity;
	Handler handler = new Handler();
	ViewGroup viewGroup;
	boolean isAlreadyScrolling = true;
	EditText editText1HashTagSearch;
	Button buttopnGo;
	String searchTag;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_search_hashtag,
				container, false);

		aActivity = getActivity();

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		listView = (ListView) rootView.findViewById(R.id.timelineListView);

		editText1HashTagSearch = (EditText) rootView
				.findViewById(R.id.editText1HashTagSearch);

		editText1HashTagSearch.append("#");

		buttopnGo = (Button) rootView.findViewById(R.id.button1Go);

		listView.setOnScrollListener(FragmentHashTagSearch.this);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		buttopnGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				searchTag = editText1HashTagSearch.getText().toString();

				isAlreadyScrolling = true;

				listTaggedTweets.clear();

				if (searchTag.length() == 0) {

					myToastS("Invalid input");

					return;

				} else {

					if (searchTag.startsWith("#")) {

					} else {

						searchTag = "#" + searchTag;

						editText1HashTagSearch.setText(searchTag);

					}

					showProgress();

					List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

					TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
							MainSingleTon.currentUserModel,
							FragmentHashTagSearch.this);

					peramPairs.add(new BasicNameValuePair(Const.count, "10"));

					peramPairs.add(new BasicNameValuePair(
							Const.include_entities, "false"));

					try {

						peramPairs.add(new BasicNameValuePair(Const.q,
								URLEncoder.encode(searchTag, "UTF-8")));

					} catch (UnsupportedEncodingException e) {

						e.printStackTrace();
					}

					twitterUserGETRequest.executeThisRequest(
							MainSingleTon.tweetsSearch, peramPairs);

				}
			}
		});

		return rootView;

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult  ");

		try {

			JSONObject jsonObject = new JSONObject(jsonResult);

			JSONArray jsonArray = jsonObject.getJSONArray(Const.statuses);

			if (jsonArray.length() == 0) {

				cancelProgres();

			} else {

				isAlreadyScrolling = false;

			}

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

					tweetModel.setUserimage(null);

					tweetModel.setUserName("@"
							+ jsonObject3.getString(Const.screen_name));

					tweetModel.setFullName(jsonObject3.getString(Const.name));

					tweetModel.setFollowing(jsonObject3
							.getBoolean(Const.following));

					listTaggedTweets.add(tweetModel);

					myprint(tweetModel);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {

				if (FragmentHashTagSearch.this.getActivity() != null) {

					twtAdpr = new TweetsAdapter(listTaggedTweets, getActivity());

					listView.setAdapter(twtAdpr);

					myprint("listView.setAdapter(twtAdpr);");

				}

			}
		});

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

			JSONObject jsonObject = new JSONObject(jsonResult);

			JSONArray jsonArray = jsonObject.getJSONArray(Const.statuses);

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

					tweetModel.setUserimage(null);

					tweetModel.setUserName("@"
							+ jsonObject3.getString(Const.screen_name));

					tweetModel.setUserID(jsonObject3.getString(Const.id));

					tweetModel.setFullName(jsonObject3.getString(Const.name));

					tweetModel.setFollowing(jsonObject3
							.getBoolean(Const.following));

					// listMyfollowers.add(tweetModel);

					handler.post(new Runnable() {

						@Override
						public void run() {

							if (FragmentHashTagSearch.this.getActivity() != null) {

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

		if (twtAdpr.getCount() == 0) {

		} else {

			isAlreadyScrolling = false;

		}

	}

	public class FetchReqPaged extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String madMaxId = params[0].toString();

			String urlTimeline = MainSingleTon.tweetsSearch;

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
			try {

				peramPairs.add(new BasicNameValuePair(Const.q, URLEncoder
						.encode(searchTag, "UTF-8")));

			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}

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

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				Toast.makeText(aActivity, toastMsg, Toast.LENGTH_SHORT).show();

			}

		});

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

		myToastS("Search Failed!");
		myprint("onFailure e " + e);
		cancelProgres();

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

				if (twtAdpr.getCount() < 15) {
					return;
				}

				viewGroup.setVisibility(View.VISIBLE);

				isAlreadyScrolling = true;

				String madMaxId = ""
						+ twtAdpr.getItem(twtAdpr.getCount() - 1).getTweetId();

				myprint(twtAdpr.getItem(twtAdpr.getCount() - 1));

				new FetchReqPaged().execute(madMaxId);

			}

		} else {

			myprint("NOOOOOOOOO DONt LOad");

		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

}
