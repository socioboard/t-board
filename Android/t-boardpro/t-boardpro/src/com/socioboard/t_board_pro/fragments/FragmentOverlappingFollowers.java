package com.socioboard.t_board_pro.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.adapters.ToFollowingAdapter;
import com.socioboard.t_board_pro.lazylist.ImageLoader;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.FullUserDetailModel;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

public class FragmentOverlappingFollowers extends Fragment implements
		OnScrollListener {

	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	public ToFollowingAdapter toFollowingAdp;
	RelativeLayout reloutProgress;
	Activity aActivity;
	Button buttonAdd, button1Remove;
	EditText editTextUserName;
	ProgressDialog progressDialog;
	ImageView profile_pic, profile_picorlpAcc;
	RelativeLayout reloutorlpAcc, datasheet;

	TextView textView1Name, textView1UserName, TextView01Followings,
			TextView0FollowedBy;

	TextView textView1Nameovlp, textView1UserNameovlp,
			TextView01Followingsovlp, TextView0FollowedByovlp;

	public ImageLoader imageLoader;

	ArrayList<ToFollowingModel> ovlpFollwers = new ArrayList<ToFollowingModel>();

	public ArrayList<String> thirdPartyUserFolloersIds = new ArrayList<String>();

	public ArrayList<String> mutalFolloersIds = new ArrayList<String>();

	boolean isAlreadyScrolling = true;

	ViewGroup viewGroup;

	Handler handler = new Handler();

	String edTextUserName;

	FullUserDetailModel fullUserDetailModel;

	TextView nomoreData, textView3Counts;

	ProgressBar prgsBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		aActivity = getActivity();

		imageLoader = new ImageLoader(getActivity());

		rootView = inflater.inflate(R.layout.fragment_ovlp_following,
				container, false);

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		datasheet = (RelativeLayout) rootView.findViewById(R.id.datasheet);

		prgsBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);

		textView3Counts = (TextView) rootView
				.findViewById(R.id.textView3Counts);

		nomoreData = (TextView) rootView.findViewById(R.id.loadingText);

		listView = (ListView) rootView.findViewById(R.id.listViewToFollowing);

		profile_pic = (ImageView) rootView.findViewById(R.id.profile_pic);

		textView1Name = (TextView) rootView.findViewById(R.id.followerName);

		textView1UserName = (TextView) rootView
				.findViewById(R.id.usersFullName);

		TextView01Followings = (TextView) rootView
				.findViewById(R.id.textViewNoOfFollowings);

		TextView0FollowedBy = (TextView) rootView
				.findViewById(R.id.textViewNoofFollowers);

		reloutorlpAcc = (RelativeLayout) rootView
				.findViewById(R.id.reloutorlpAcc);

		showTexts();

		profile_picorlpAcc = (ImageView) rootView
				.findViewById(R.id.profile_picorlpAcc);

		textView1Nameovlp = (TextView) rootView
				.findViewById(R.id.followerNameorlpAcc);

		textView1UserNameovlp = (TextView) rootView
				.findViewById(R.id.usersFullNameorlpAcc);

		// ABSDBASJ
		TextView01Followingsovlp = (TextView) rootView
				.findViewById(R.id.textViewNoOfFollowingsorlpAccS);

		TextView0FollowedByovlp = (TextView) rootView
				.findViewById(R.id.textViewNoofFollowersorlpAcc);

		editTextUserName = (EditText) rootView
				.findViewById(R.id.editText1HashTagSearch);

		buttonAdd = (Button) rootView.findViewById(R.id.button1Go);

		button1Remove = (Button) rootView.findViewById(R.id.button1Remove);

		buttonAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				edTextUserName = editTextUserName.getText().toString();

				if (edTextUserName.isEmpty()) {
					return;
				}
				
				showProgressBar("Fetching User...");

				new LoadthisUserData().execute();

			}
		});
		
		toFollowingAdp = new ToFollowingAdapter(getActivity(), ovlpFollwers, aActivity);
		
		button1Remove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				editTextUserName.setVisibility(View.VISIBLE);
				
				datasheet.setVisibility(View.GONE);

				button1Remove.setVisibility(View.GONE);
				
				buttonAdd.setVisibility(View.VISIBLE);

 				reloutorlpAcc.setVisibility(View.GONE);

				editTextUserName.setText("");
				
				editTextUserName.requestFocus();
 
				mutalFolloersIds.clear();

				ovlpFollwers.clear();

				thirdPartyUserFolloersIds.clear();

				toFollowingAdp.tweetModels.clear();

				toFollowingAdp.notifyDataSetChanged();

			}
		});

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);

		listView.setOnScrollListener(this);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		return rootView;

	}

	class LoadthisUserData extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			TwitterUserGETRequest twitterShowUserJSon = new TwitterUserGETRequest(
					MainSingleTon.currentUserModel,
					new TwitterRequestCallBack() {

						@Override
						public void onSuccess(JSONObject jsonObject) {
							cancelProgresBar();
						}

						@Override
						public void onSuccess(String jsonResult) {

							cancelProgresBar();

							JSONObject jsonObject;

							try {

								myprint(jsonResult);

								jsonObject = new JSONObject(jsonResult);

								fullUserDetailModel = new FullUserDetailModel();

								fullUserDetailModel.setUserImagerUrl(jsonObject
										.getString(Const.profile_image_url));

								fullUserDetailModel.setNoFollowers(jsonObject
										.getString(Const.followers_count));

								fullUserDetailModel.setNoToFollowing(jsonObject
										.getString(Const.friends_count));

								fullUserDetailModel.setUserName(jsonObject
										.getString(Const.screen_name));

								fullUserDetailModel.setFullName(jsonObject
										.getString(Const.name));

								fullUserDetailModel.setId(jsonObject
										.getString(Const.id_str));

								fullUserDetailModel.setNoTweets(jsonObject
										.getString(Const.statuses_count));

								showTextsovlp(fullUserDetailModel);

							} catch (JSONException e) {

								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(Exception e) {
							cancelProgresBar();
							myToastS("Faild to fetch User");
						}
					});

			List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

			peramPairs.add(new BasicNameValuePair(Const.screen_name,
					edTextUserName));

			twitterShowUserJSon.executeThisRequest(
					MainSingleTon.userAccountData, peramPairs);

			return null;
		}
	}

	void showTextsovlp(final FullUserDetailModel fullUserDetailModel) {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				myprint(fullUserDetailModel);

				editTextUserName.setVisibility(View.GONE);

				buttonAdd.setVisibility(View.GONE);

				reloutorlpAcc.setVisibility(View.VISIBLE);

				textView1Nameovlp.setText(fullUserDetailModel.getFullName());

				textView1UserNameovlp.setText("@"
						+ fullUserDetailModel.getUserName());

				myprint("fullUserDetailModel.getNoToFollowing()  "
						+ fullUserDetailModel.getNoToFollowing());

				TextView01Followingsovlp.setText(fullUserDetailModel
						.getNoToFollowing());

				TextView0FollowedByovlp.setText(fullUserDetailModel
						.getNoFollowers());

				imageLoader.DisplayImage(
						fullUserDetailModel.getUserImagerUrl(),
						profile_picorlpAcc);

				showProgress();

				loadfollowers();
			}
		});

	}

	protected void loadfollowers() {

		TwitterUserGETRequest userGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

					@Override
					public void onSuccess(JSONObject jsonObject) {

					}

					@Override
					public void onSuccess(String jsonResult) {

						myprint("jsonResult" + jsonResult);

						JSONArray jsonArray;

						try {

							JSONObject jsonObject = new JSONObject(jsonResult);

							jsonArray = new JSONArray(jsonObject
									.getString("ids"));

							for (int i = 0; i < jsonArray.length(); ++i) {

								thirdPartyUserFolloersIds.add(jsonArray
										.getString(i));

							}

							ArrayList<String> CopyThirdPartyUserFolloersIds = (ArrayList<String>) thirdPartyUserFolloersIds.clone();

							mutalFolloersIds = (ArrayList<String>) intersection(CopyThirdPartyUserFolloersIds,MainSingleTon.listMyfollowersIDs);

							myprint("mutalFolloersIds " + mutalFolloersIds);

							new MutalFollowing().execute();

						} catch (JSONException e) {

							e.printStackTrace();

						}
						
						cancelProgres();

						aActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {

								button1Remove.setVisibility(View.VISIBLE);

								datasheet.setVisibility(View.VISIBLE);

								textView3Counts
										.setText("Overlapping followers :   "
												+ mutalFolloersIds.size());

							}
						});

					}

					@Override
					public void onFailure(Exception e) {

						cancelProgres();

					}
					
				});

		String url = MainSingleTon.users_following_to_me_Ids;

		List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

		peramPairs.add(new BasicNameValuePair(Const.cursor, "-1"));

		peramPairs.add(new BasicNameValuePair(Const.count, "5000"));
		
 		peramPairs.add(new BasicNameValuePair(Const.user_id, fullUserDetailModel.getId()));

		userGETRequest.executeThisRequest(url, peramPairs);
		
	}

	public <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<T>();

		for (T t : list1) {
			if (list2.contains(t)) {
				list.add(t);
			}
		}

		return list;
	}

	void showTexts() {

		textView1UserName.setText(MainSingleTon.fullUserDetailModel.getUserName());

		textView1Name.setText(MainSingleTon.fullUserDetailModel.getFullName());

		TextView01Followings.setText(MainSingleTon.fullUserDetailModel.getNoToFollowing());

		TextView0FollowedBy.setText(MainSingleTon.fullUserDetailModel.getNoFollowers());

		imageLoader.DisplayImage(MainSingleTon.fullUserDetailModel.getUserImagerUrl(),profile_pic);

	}

	private void addFooterView() {

		LayoutInflater inflater = getActivity().getLayoutInflater();

		viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
				listView, false);

		listView.addFooterView(viewGroup);

		myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

	}

	public class MutalFollowing extends AsyncTask<Void, Void, Void> {

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

						}

					});

			String userswithComma = "";

			myprint("Sizes ovlpFollwers.size() " + ovlpFollwers.size());

			myprint("Sizes mutalFolloersIds.size() " + mutalFolloersIds.size());

			if (ovlpFollwers.size() == mutalFolloersIds.size()) {

				handler.post(new Runnable() {

					@Override
					public void run() {

						viewGroup.setVisibility(View.INVISIBLE);

						cancelProgres();
						
					}
				});

			} else {

				for (int i = ovlpFollwers.size(); i < (ovlpFollwers.size() + 99); ++i) {

					if (i == ovlpFollwers.size()) {

						userswithComma = mutalFolloersIds.get(i);

						myprint(i + "++++++++++ i first " + userswithComma);

					} else {

						try {

							userswithComma = userswithComma + ","
									+ mutalFolloersIds.get(i);

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

						if (FragmentOverlappingFollowers.this.getActivity() != null) {

							int listCount = listView.getCount();

							toFollowingAdp.tweetModels.add(followingModel);

							listView.setScrollY(listCount);

							toFollowingAdp.notifyDataSetChanged();

							ovlpFollwers = toFollowingAdp.tweetModels;

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

			myprint("Sizes ovlpFollwers.size() " + ovlpFollwers.size());

			myprint("Sizes mutalFolloersIds.size() " + mutalFolloersIds.size());

			if (ovlpFollwers.size() >= mutalFolloersIds.size()) {

				handler.post(new Runnable() {

					@Override
					public void run() {

						prgsBar.setVisibility(View.GONE);

						nomoreData.setVisibility(View.VISIBLE);

						// nomoreData.setText("-- No Data Available --");

						viewGroup.setVisibility(View.INVISIBLE);

						myprint("Show this Thing 0000 ");
					}
				});

			} else {

				for (int i = ovlpFollwers.size(); i < (ovlpFollwers.size() + 99); ++i) {

					if (i == ovlpFollwers.size()) {

						userswithComma = mutalFolloersIds.get(i);

					} else {

						try {

							userswithComma = "," + mutalFolloersIds.get(i);

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

		myprint("parseJsonResult  ");

		try {

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

				ovlpFollwers.add(followingModel);

				myprint(followingModel);

			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (FragmentOverlappingFollowers.this.getActivity() != null) {

						toFollowingAdp = new ToFollowingAdapter(getActivity(),
								ovlpFollwers, FragmentOverlappingFollowers.this
										.getActivity());

						listView.setAdapter(toFollowingAdp);

						isAlreadyScrolling = false;

					}

				}
			});

		} catch (JSONException e) {

			e.printStackTrace();

		}

		if (ovlpFollwers.size() == 0) {

			prgsBar.setVisibility(View.GONE);

			nomoreData.setText("-- No Data Available --");

		} else {

			cancelProgres();
		}

	}

	void myToastS(final String toastMsg) {
		
		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	void myToastL(final String toastMsg) {
		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
			}

		});

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

	void showProgressBar(final String msg) {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				progressDialog.setMessage(msg);

				progressDialog.show();

			}
		});
	}

	void cancelProgresBar() {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				progressDialog.cancel();

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