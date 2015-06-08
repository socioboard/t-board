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

import com.socioboard.t_board_pro.adapters.ToFollowingAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterTimeLineRequest2;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

public class FragmentIAMFollowingTo extends Fragment implements
		OnScrollListener {

	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	public ToFollowingAdapter toFollowingAdp;
	RelativeLayout reloutProgress;
	Activity aActivity;

	boolean isAlreadyScrolling = true;

	ViewGroup viewGroup;

	Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		aActivity = getActivity();

		rootView = inflater.inflate(R.layout.fragment_to_following, container,
				false);
		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		listView = (ListView) rootView.findViewById(R.id.listViewToFollowing);

		listView.setOnScrollListener(this);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		if (MainSingleTon.toFollowingModels.size() > 0) {

			for (int i = 0; i < MainSingleTon.toFollowingModels.size(); i++) {
				
				if (MainSingleTon.toFollowingModels.get(i).isFollowingStatus()) {
				} else {
					MainSingleTon.toFollowingModels.remove(i);
				}
				
 			}

			toFollowingAdp = new ToFollowingAdapter(getActivity(),
					MainSingleTon.toFollowingModels,
					FragmentIAMFollowingTo.this.getActivity());

			listView.setAdapter(toFollowingAdp);

			isAlreadyScrolling = false;
			
			cancelProgres();

		} else {

			new ToFollowing().execute();

		}

		return rootView;

	}

	private void addFooterView() {

		LayoutInflater inflater = getActivity().getLayoutInflater();

		viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
				listView, false);

		listView.addFooterView(viewGroup);

		myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

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

	protected void parseJsonResultPaged(String jsonResult) {

		myprint("parseJsonResult  ");

		handler.post(new Runnable() {

			@Override
			public void run() {

				viewGroup.setVisibility(View.INVISIBLE);

			}
		});

		try {

			JSONObject jsonObject = new JSONObject(jsonResult);

			JSONArray jsonArray = jsonObject.getJSONArray("users");

			MainSingleTon.followingNextCursor = jsonObject.getString(Const.next_cursor_str);

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
						if (FragmentIAMFollowingTo.this.getActivity() != null) {

							int listCount = listView.getCount();

							toFollowingAdp.tweetModels.add(followingModel);

							listView.setScrollY(listCount);

							toFollowingAdp.notifyDataSetChanged();

							MainSingleTon.toFollowingModels = toFollowingAdp.tweetModels;

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

			String urlTimeline = MainSingleTon.i_am_following_to;

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

			peramPairs.add(new BasicNameValuePair(Const.cursor, MainSingleTon.followingNextCursor));

			twitterUserGETRequest.executeThisRequest(urlTimeline, peramPairs);

			return null;
		}

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult  ");

		try {

			JSONObject jsonObject = new JSONObject(jsonResult);

			JSONArray jsonArray = jsonObject.getJSONArray("users");

			MainSingleTon.followingNextCursor = jsonObject.getString(Const.next_cursor_str);

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

				MainSingleTon.toFollowingModels.add(followingModel);

				myprint(followingModel);

			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (FragmentIAMFollowingTo.this.getActivity() != null) {

						toFollowingAdp = new ToFollowingAdapter(getActivity(),
								MainSingleTon.toFollowingModels,
								FragmentIAMFollowingTo.this.getActivity());

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