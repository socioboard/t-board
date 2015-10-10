package com.socioboard.t_board_pro.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
import com.socioboard.t_board_pro.util.SearchDetailModel;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

public class FragmentSearch extends Fragment implements OnScrollListener {

	View rootView;

	EditText editText;

	TwitterUserSearchRequest userSearchRequest;

	String searchText = "";

	ImageView button1Search;

	ListView listView;

	SearchAdapter searchAdapter;

	Activity aActivity;

	RelativeLayout reloutProgress;

	boolean isAlreadyScrolling = true;

	TextView textView1SearchedText;

	ImageView imageView1;

	ViewGroup viewGroup;

	Handler handler = new Handler();

	SearchDetailModel searchDetailModel = new SearchDetailModel();

	public static FragmentSearch newInstance(String text) {

		FragmentSearch f = new FragmentSearch();
		Bundle b = new Bundle();
		b.putString("msg", text);
		f.setArguments(b);

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_search, container, false);

		aActivity = getActivity();

		editText = (EditText) rootView.findViewById(R.id.edsearchView1);

		textView1SearchedText = (TextView) rootView
				.findViewById(R.id.textView1SearchedText);

		button1Search = (ImageView) rootView.findViewById(R.id.button1Search);

		listView = (ListView) rootView.findViewById(R.id.listView1Searched);

		imageView1 = (ImageView) rootView.findViewById(R.id.imageView1Removes);

		imageView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				viewGroup.setVisibility(View.INVISIBLE);

				imageView1.setVisibility(View.INVISIBLE);

				searchDetailModel.setSearchText("");

				searchDetailModel.getSearchList().clear();

				searchAdapter.tweetModels.clear();

				searchAdapter = new SearchAdapter(FragmentSearch.this
						.getActivity(), searchDetailModel.getSearchList(),
						FragmentSearch.this.getActivity());

				listView.setAdapter(searchAdapter);

				isAlreadyScrolling = true;

				textView1SearchedText.setText("");

				editText.setText("");
				
				cancelProgres();
				
			}
		});

		listView.setOnScrollListener(this);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		button1Search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				isAlreadyScrolling = true;

				searchText = editText.getText().toString();

				if (searchText.length() == 0) {

					myprint("Empty");

					myToastS("Enter username to search");

				} else {
					
					View view = getActivity().getCurrentFocus();

					if (view != null) {

						InputMethodManager imm = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

					}

					searchDetailModel.getSearchList().clear();

					searchAdapter = new SearchAdapter(aActivity,
							searchDetailModel.getSearchList(), aActivity);

					textView1SearchedText.setText("Searching results for: \""
							+ searchText + "\"");

					searchDetailModel.setSearchText(searchText);

					listView.setAdapter(searchAdapter);

					showProgress();

					FetchReqPaged();

				}
			}
		});

		initView();

		return rootView;
	}

	private void initView() {

		if (searchDetailModel.getSearchList().size() == 0) {

		} else {

			imageView1.setVisibility(View.VISIBLE);

			searchAdapter = new SearchAdapter(getActivity(),
					searchDetailModel.getSearchList(),
					FragmentSearch.this.getActivity());

			listView.setAdapter(searchAdapter);

			textView1SearchedText.setText("Searched "
					+ searchAdapter.tweetModels.size() + " results for: \""
					+ searchDetailModel.getSearchText() + "\"");

			editText.append(searchDetailModel.getSearchText());

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

		myprint("addFooterView ++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

	}

	void myToastL(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	public void FetchReqPaged() {

		String urlTimeline = MainSingleTon.userSearch;

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
				+ (searchAdapter.getCount() / 20 + 1)));

		peramPairs.add(new BasicNameValuePair(Const.q, searchText));

		twitterUserGETRequest.executeThisRequest(urlTimeline, peramPairs);

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult  ");

		try {

			JSONArray jsonArray = new JSONArray(jsonResult);

			for (int i = 0; i < jsonArray.length(); ++i) {

				JSONObject jsonObject2 = jsonArray.getJSONObject(i);

				myprint("jsonObject2 " + (searchAdapter.getCount() + i) + " = "
						+ "");

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

				searchAdapter.tweetModels.add(followingModel);

				// myprint(followingModel);

			}

			if (FragmentSearch.this.getActivity() != null) {

				aActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {

						searchDetailModel
								.setSearchList(searchAdapter.tweetModels);

						if (searchDetailModel.getSearchList().size() < 20) {

							isAlreadyScrolling = true;

						} else {

							isAlreadyScrolling = false;

						}

						if (searchDetailModel.getSearchList().size() == 0) {

						} else {

							imageView1.setVisibility(View.VISIBLE);
						}

						int listCount = listView.getCount();

						listView.setScrollY(listCount);

						textView1SearchedText.setText("Searched "
								+ searchAdapter.tweetModels.size()
								+ " results for: \"" + searchText + "\"");

						searchAdapter.notifyDataSetChanged();

					}
				});

			}
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

				myprint("BUT isAlreadyScrolling ");

			} else {

				isAlreadyScrolling = true;

				String madMaxId = ""
						+ searchAdapter.getItem(searchAdapter.getCount() - 1)
								.getId();

				myprint(searchAdapter.getItem(searchAdapter.getCount() - 1));

				if (searchAdapter.getCount() % 20 != 0) {

					myprint("searchAdapter.getCount() % 20 != 0 "
							+ searchAdapter.getCount() % 20);

				} else {

					viewGroup.setVisibility(View.VISIBLE);

					FetchReqPaged();

				}

				myprint("*********** searchAdapter.getCount() "
						+ (searchAdapter.getCount()));

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
