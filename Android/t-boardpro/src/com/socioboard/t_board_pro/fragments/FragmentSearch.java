package com.socioboard.t_board_pro.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.adapters.SearchAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.twitterapi.TwitterUserSearchRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

public class FragmentSearch extends Fragment implements TwitterRequestCallBack,
		OnScrollListener {

	View rootView;
	EditText editText;
	TwitterUserSearchRequest userSearchRequest;
	String searchText = "";
	ImageView button1Search;
	ListView listView;
	SearchAdapter searchAdapter;
	Activity aActivity;
	RelativeLayout reloutProgress, rel;
	boolean isAlreadyScrolling = true;
	TextView textView1SearchedText;
	ImageView imageView1;
	ViewGroup viewGroup;

	Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_search, container, false);

		aActivity = getActivity();

		editText = (EditText) rootView.findViewById(R.id.edsearchView1);

		textView1SearchedText = (TextView) rootView.findViewById(R.id.textView1SearchedText);

		button1Search = (ImageView) rootView.findViewById(R.id.button1Search);

		listView = (ListView) rootView.findViewById(R.id.listView1Searched);

		imageView1 = (ImageView) rootView.findViewById(R.id.imageView1);

		imageView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				imageView1.setVisibility(View.INVISIBLE);

				MainSingleTon.searchDetailModel.setSearchText("");

				MainSingleTon.searchDetailModel.getSearchList().clear();

				searchAdapter.tweetModels.clear();

				searchAdapter = new SearchAdapter(getActivity(),
						MainSingleTon.searchDetailModel.getSearchList(),
						FragmentSearch.this.getActivity());

				listView.setAdapter(searchAdapter);

				isAlreadyScrolling = true;

				textView1SearchedText.setText("");

				editText.setText("");

			}
		});

		listView.setOnScrollListener(this);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		rel = (RelativeLayout) rootView.findViewById(R.id.rel);

		rel.setVisibility(View.VISIBLE);

		userSearchRequest = new TwitterUserSearchRequest(
				MainSingleTon.currentUserModel, this);

		button1Search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				isAlreadyScrolling = true;

				searchText = editText.getText().toString();

				if (searchText.length() == 0) {

					myprint("Empty");

					myToastS("Enter username to search");

				} else {

					textView1SearchedText.setText("Searching results for: \""
							+ searchText + "\"");

					MainSingleTon.searchDetailModel.setSearchText(searchText);

					MainSingleTon.searchDetailModel.getSearchList().clear();

					showProgress();

					userSearchRequest.executeThisRequest(searchText);

				}
			}
		});

		initView();

		return rootView;
	}

	private void initView() {

		if (MainSingleTon.searchDetailModel.getSearchList().size() == 0) {

		} else {

			imageView1.setVisibility(View.VISIBLE);

			searchAdapter = new SearchAdapter(getActivity(),
					MainSingleTon.searchDetailModel.getSearchList(),
					FragmentSearch.this.getActivity());

			listView.setAdapter(searchAdapter);

			textView1SearchedText.setText("Searched "
					+ searchAdapter.tweetModels.size() + " results for: \""
					+ MainSingleTon.searchDetailModel.getSearchText() + "\"");

			editText.append(MainSingleTon.searchDetailModel.getSearchText());

			isAlreadyScrolling = false;

		}
	}

	void myToastS(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
	}

	private void addFooterView() {

		LayoutInflater inflater = getActivity().getLayoutInflater();

		viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
				listView, false);

		listView.addFooterView(viewGroup);

		myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

	}

	protected void parseJsonResultPaged(String jsonResult) {

		myprint("parseJsonResultPaged  ");

		handler.post(new Runnable() {

			@Override
			public void run() {

				viewGroup.setVisibility(View.INVISIBLE);

			}
		});

		try {

			JSONArray jsonArray = new JSONArray(jsonResult);

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

						if (FragmentSearch.this.getActivity() != null) {

							int listCount = listView.getCount();

							searchAdapter.tweetModels.add(followingModel);

							listView.setScrollY(listCount);

							searchAdapter.notifyDataSetChanged();

						}
					}
				});
			}

			textView1SearchedText.setText("Searched "
					+ searchAdapter.tweetModels.size() + " results for: \""
					+ searchText + "\"");

			isAlreadyScrolling = false;

			MainSingleTon.searchDetailModel
					.setSearchList(searchAdapter.tweetModels);

		} catch (JSONException e) {

			e.printStackTrace();

		}

	}

	void myToastL(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	@Override
	public void onSuccess(String jsonResult) {
		cancelProgres();
		myprint("onSuccess jsonResult= " + jsonResult);
		parseJsonResult(jsonResult);

	}

	@Override
	public void onSuccess(JSONObject jsonObject) {

	}

	@Override
	public void onFailure(Exception e) {

		cancelProgres();

		myprint("onFailure " + e);

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				textView1SearchedText.setText("No results found for: \"" + searchText + "\" ");
				
			}
		});

	}

	public class FetchReqPaged extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String urlTimeline = MainSingleTon.userSearch;

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

			peramPairs.add(new BasicNameValuePair(Const.count, "" + 20));
			peramPairs.add(new BasicNameValuePair(Const.page, ""
					+ searchAdapter.getCount() / 20));
			peramPairs.add(new BasicNameValuePair(Const.q, searchText));

			twitterUserGETRequest.executeThisRequest(urlTimeline, peramPairs);

			return null;
		}

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult  ");

		try {

			MainSingleTon.searchDetailModel.getSearchList().clear();

			JSONArray jsonArray = new JSONArray(jsonResult);

			for (int i = 0; i < jsonArray.length(); ++i) {

				JSONObject jsonObject2 = jsonArray.getJSONObject(i);

				myprint("jsonObject2 " + i + " = " + jsonObject2);

				ToFollowingModel followingModel = new ToFollowingModel();

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

				MainSingleTon.searchDetailModel.getSearchList().add(
						followingModel);

				myprint(followingModel);

			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (FragmentSearch.this.getActivity() != null) {

						searchAdapter = new SearchAdapter(
								getActivity(),
								MainSingleTon.searchDetailModel.getSearchList(),
								FragmentSearch.this.getActivity());

						listView.setAdapter(searchAdapter);

						
						MainSingleTon.searchDetailModel.setSearchList(searchAdapter.tweetModels);

						if (MainSingleTon.searchDetailModel.getSearchList()
								.size() == 0) {
							
						} else {
						
							isAlreadyScrolling = false;

							imageView1.setVisibility(View.VISIBLE);
						}
						

					}
				}
			});

			aActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					textView1SearchedText.setText("Searched "
							+ searchAdapter.tweetModels.size()
							+ " results for: \"" + searchText + "\"");
				}
			});


		} catch (JSONException e) {

			e.printStackTrace();

		}

		cancelProgres();

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

				String madMaxId = ""
						+ searchAdapter.getItem(searchAdapter.getCount() - 1)
								.getId();

				myprint(searchAdapter.getItem(searchAdapter.getCount() - 1));

				new FetchReqPaged().execute(madMaxId);

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
