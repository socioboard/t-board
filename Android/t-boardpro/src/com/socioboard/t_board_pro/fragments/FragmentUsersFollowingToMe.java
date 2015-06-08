package com.socioboard.t_board_pro.fragments;

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
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.socioboard.t_board_pro.adapters.MyFollowersAdapter;
import com.socioboard.t_board_pro.adapters.ToFollowingAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterTimeLineRequest2;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

public class FragmentUsersFollowingToMe extends Fragment implements
		OnScrollListener {

	View rootView;

	ListView listView;

	Bitmap userImage, userbannerImage;

	MyFollowersAdapter myFollowersAdapter;

	RelativeLayout reloutProgress;

	Activity aActivity;

	boolean isAlreadyScrolling = true;

	ViewGroup viewGroup;

	Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_my_followers, container,
				false);

		aActivity = getActivity();

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		listView = (ListView) rootView.findViewById(R.id.listViewMyfolowers);

		listView.setOnScrollListener(this);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		if (MainSingleTon.myFollowers.size() > 0) {

			myFollowersAdapter = new MyFollowersAdapter(getActivity(),MainSingleTon.myFollowers,FragmentUsersFollowingToMe.this.getActivity());

			listView.setAdapter(myFollowersAdapter);

		} else {

			new FollowingToMe().execute();

		}

		return rootView;

	}

	protected void parseJsonResultPaged(String jsonResult) {

		myprint("parseResult  ");

		handler.post(new Runnable() {

			@Override
			public void run() {

				viewGroup.setVisibility(View.INVISIBLE);

			}

		});

		try {

			JSONObject jsonObject = new JSONObject(jsonResult);

			JSONArray jsonArray = jsonObject.getJSONArray("users");

			MainSingleTon.myfollowersNextCursor = jsonObject
					.getString(Const.next_cursor_str);

			for (int i = 0; i < jsonArray.length(); ++i) {

				JSONObject jsonObject2 = jsonArray.getJSONObject(i);

				myprint("jsonObject2 " + i + " = " + jsonObject2);

				final ToFollowingModel myFollowersModel = new ToFollowingModel();

				myFollowersModel.setFollowingStatus(jsonObject2.getString(
						Const.following).contains("true"));

				myFollowersModel.setId(jsonObject2.getString(Const.id_str));

				myFollowersModel.setNoFollowers(jsonObject2
						.getString(Const.followers_count));

				myFollowersModel.setNoToFollowing(jsonObject2
						.getString(Const.friends_count));

				myFollowersModel.setNoTweets(jsonObject2
						.getString(Const.listed_count));

				myFollowersModel.setTweeet_str("");

				myFollowersModel.setUserImagerUrl(jsonObject2
						.getString(Const.profile_image_url));

				myFollowersModel.setUserName("@"
						+ jsonObject2.getString(Const.screen_name));

				myprint(myFollowersModel);

				final int indexed = i;

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {

						if (FragmentUsersFollowingToMe.this.getActivity() != null) {

							int listCount = listView.getCount();

							myFollowersAdapter.tweetModels
									.add(myFollowersModel);

							listView.setScrollY(listCount);

							myFollowersAdapter.notifyDataSetChanged();

							MainSingleTon.myFollowers.add(indexed,
									myFollowersModel);

						}
						
					}
				});

			}

		} catch (JSONException e) {

			e.printStackTrace();

		}

		isAlreadyScrolling = false;

	}

	private void addFooterView() {

		LayoutInflater inflater = getActivity().getLayoutInflater();

		viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
				listView, false);

		listView.addFooterView(viewGroup);

		myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

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

						}
					});

			twitterTimeLineRequest
					.doInBackground(MainSingleTon.users_following_to_me);

			return null;

		}

	}

	public class FetchReqPaged extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String urlTimeline = MainSingleTon.users_following_to_me;

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

									isAlreadyScrolling = false;

								}
							});

						}

						@Override
						public void onSuccess(JSONObject jsonObject) {

						}

					});

			List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

			peramPairs.add(new BasicNameValuePair(Const.cursor,
					MainSingleTon.myfollowersNextCursor));

			twitterUserGETRequest.executeThisRequest(urlTimeline, peramPairs);

			return null;
		}

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult  ");

		try {

			JSONObject jsonObject = new JSONObject(jsonResult);

			JSONArray jsonArray = jsonObject.getJSONArray("users");

			MainSingleTon.myfollowersNextCursor = jsonObject
					.getString(Const.next_cursor_str);

			for (int i = 0; i < jsonArray.length(); ++i) {

				JSONObject jsonObject2 = jsonArray.getJSONObject(i);

				myprint("jsonObject2 " + i + " = " + jsonObject2);

				ToFollowingModel myFollowersModel = new ToFollowingModel();

				myFollowersModel.setFollowingStatus(jsonObject2.getString(
						Const.following).contains("true"));

				myFollowersModel.setId(jsonObject2.getString(Const.id_str));

				myFollowersModel.setNoFollowers(jsonObject2
						.getString(Const.followers_count));

				myFollowersModel.setNoToFollowing(jsonObject2
						.getString(Const.friends_count));

				myFollowersModel.setNoTweets(jsonObject2
						.getString(Const.listed_count));

				myFollowersModel.setTweeet_str("");

				myFollowersModel.setUserImagerUrl(jsonObject2
						.getString(Const.profile_image_url));

				myFollowersModel.setUserName("@"
						+ jsonObject2.getString(Const.screen_name));

				myprint(myFollowersModel);

				MainSingleTon.myFollowers.add(myFollowersModel);

				myprint(myFollowersModel);

			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (FragmentUsersFollowingToMe.this.getActivity() != null) {

						myFollowersAdapter = new MyFollowersAdapter(
								getActivity(), MainSingleTon.myFollowers,
								FragmentUsersFollowingToMe.this.getActivity());

						listView.setAdapter(myFollowersAdapter);

					}
				}
			});

		} catch (JSONException e) {

			e.printStackTrace();

		}

		isAlreadyScrolling = false;

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

				// DO NOTHING
				myprint("BUT isAlreadyScrolling ");

			} else {

				viewGroup.setVisibility(View.VISIBLE);

				isAlreadyScrolling = true;

				myprint(myFollowersAdapter.getItem(myFollowersAdapter
						.getCount() - 1));

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
