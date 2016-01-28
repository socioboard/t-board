package com.socioboard.t_board_pro.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.adapters.ToFollowingAdapter;
import com.socioboard.t_board_pro.lazylist.ImageLoader;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestFollow;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestUnFollow;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.FullUserDetailModel;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

public class FragmentCopyFollowers extends Fragment implements OnScrollListener {

	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	public ToFollowingAdapter toFollowingAdp;
	RelativeLayout reloutProgress;
	Activity aActivity;
	Button button1Remove;
	ImageView buttonAdd;
	EditText editTextUserName, editText1Range;
	ProgressDialog progressDialog;
	ImageView profile_picorlpAcc;
	RelativeLayout relativeLayout1, reloutSearch, datasheet;
	Dialog progressDialogProcess;
	TextView processDesc, textViewNoOfFollowers;
	Thread thread;

	TboardproLocalData tboardproLocalData;

	ArrayList<String> sendingids;

	ArrayList<String> sentIds;

	TextView textView1Nameovlp, textView1UserNameovlp, textViewNotweets,
			TextView01Followingsovlp, TextView0FollowedByovlp, textView3Record;

	public ImageLoader imageLoader;

	ArrayList<ToFollowingModel> adapterModels = new ArrayList<ToFollowingModel>();

	public ArrayList<String> followerIds = new ArrayList<String>();

	public ArrayList<String> followerIdsLoaded = new ArrayList<String>();

	public ArrayList<String> targetIds = new ArrayList<String>();

	boolean isAlreadyScrolling = true, isneedTostop = false;

	ViewGroup viewGroup;

	long copyFollowedCount = 0, targetCount, failedcount = 0;

	Handler handler = new Handler();

	String edTextUserName;

	FullUserDetailModel fullUserDetailModel;

	Button buttonFollow, buttonUnfollow;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		aActivity = getActivity();

		tboardproLocalData = new TboardproLocalData(aActivity);

		sendingids = tboardproLocalData
				.getAllSendingIDs(MainSingleTon.currentUserModel.getUserid());

		sentIds = tboardproLocalData
				.getAllSentIDs(MainSingleTon.currentUserModel.getUserid());

		imageLoader = new ImageLoader(getActivity());

		rootView = inflater.inflate(R.layout.fragment_copyfollowers, container,
				false);

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		relativeLayout1 = (RelativeLayout) rootView
				.findViewById(R.id.relativeLayout1);

		datasheet = (RelativeLayout) rootView.findViewById(R.id.datasheet);

		relativeLayout1.setVisibility(View.GONE);

		listView = (ListView) rootView.findViewById(R.id.listView1);

		reloutSearch = (RelativeLayout) rootView.findViewById(R.id.rel);

		profile_picorlpAcc = (ImageView) rootView
				.findViewById(R.id.profile_pic);

		textView1Nameovlp = (TextView) rootView.findViewById(R.id.followerName);

		textViewNoOfFollowers = (TextView) rootView
				.findViewById(R.id.textViewNoOfFollowers);

		textViewNotweets = (TextView) rootView
				.findViewById(R.id.textViewNotweets);

		textView1UserNameovlp = (TextView) rootView
				.findViewById(R.id.usersFullName);

		textView3Record = (TextView) rootView
				.findViewById(R.id.textView3Record);

		textView3Record.setText("");

		// ABSDBASJ
		TextView01Followingsovlp = (TextView) rootView
				.findViewById(R.id.textViewNoOfFollowings);

		TextView0FollowedByovlp = (TextView) rootView
				.findViewById(R.id.textViewNoofFollowers);

		editTextUserName = (EditText) rootView
				.findViewById(R.id.editText1HashTagSearch);

		editText1Range = (EditText) rootView.findViewById(R.id.editText1Range);

		buttonAdd = (ImageView) rootView.findViewById(R.id.button1Go);

		buttonAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				edTextUserName = editTextUserName.getText().toString();

				if (edTextUserName.isEmpty()) {
					return;
				}

				View view = getActivity().getCurrentFocus();

				if (view != null) {

					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

				}

				relativeLayout1.setVisibility(View.GONE);

				datasheet.setVisibility(View.INVISIBLE);

				copyFollowedCount = 0;
				targetCount = 0;
				failedcount = 0;
				followerIds.clear();
				targetIds.clear();

				textView3Record.setText("");

				adapterModels.clear();

				followerIdsLoaded.clear();

				isAlreadyScrolling = true;

				toFollowingAdp = new ToFollowingAdapter(getActivity(),
						adapterModels, FragmentCopyFollowers.this.getActivity());

				listView.setAdapter(toFollowingAdp);

				showProgressBar("Fetching User...");

				LoadthisUserData();

			}
		});

		toFollowingAdp = new ToFollowingAdapter(getActivity(), adapterModels,
				aActivity);

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		listView.setOnScrollListener(this);

		addFooterView();

		viewGroup.setVisibility(View.INVISIBLE);

		buttonFollow = (Button) rootView.findViewById(R.id.buttonFollow);

		buttonUnfollow = (Button) rootView.findViewById(R.id.buttonUnfollow);

		Button buttonCopyFollowers = (Button) rootView
				.findViewById(R.id.button1);

		buttonCopyFollowers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				View view = getActivity().getCurrentFocus();

				if (view != null) {

					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

				}

				if (MainSingleTon.isNeedTOstopFollowing) {

					myToastL("You have Exceeded Follow Limit Today");

				} else {

					if (followerIds.size() == 0) {

						myToastL("Non users Available !!");

					} else {

						if (editText1Range.getText().toString().isEmpty()) {

							myToastS("Invalid Range!");

							return;
						}

						Integer tarInteger = new Integer(editText1Range
								.getText().toString());

						if (tarInteger <= 0) {

							myToastS("Invalid Range!");

						} else {

							setTargetIds(tarInteger);

							if (targetIds.size() == 0) {

								myToastL("Non users Available To Follow!!");

							} else {

								isneedTostop = false;

								copyFollowedCount = 0;

								progressDialogProcess.show();

								setProgressData("Processing - "
										+ copyFollowedCount + "/" + targetCount
										+ "...");

								new Thread(new Runnable() {

									@Override
									public void run() {

										followThisId(targetIds.get(0));

									}
								}).start();

							}

						}
					}
				}
			}
		});

		buttonFollow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("buttonFollow ");

				progressDialog.setMessage("Following - "
						+ fullUserDetailModel.getFullName() + "...");

				progressDialog.show();

				TwitterPostRequestFollow twitterPostRequestFollow = new TwitterPostRequestFollow(
						MainSingleTon.currentUserModel,
						new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {

							}

							@Override
							public void onSuccess(String jsonResult) {

								cancelProgresBar();

								addDMStatus(fullUserDetailModel.getId());

								myprint("buttonFollow onSuccess");

								aActivity.runOnUiThread(new Runnable() {

									@Override
									public void run() {

										buttonFollow
												.setVisibility(View.INVISIBLE);

										buttonUnfollow
												.setVisibility(View.VISIBLE);

										++MainSingleTon.followingCount;

										MainSingleTon.toFollowingModelsIDs
												.add(fullUserDetailModel
														.getId());

										MainActivity.isNeedToRefreshDrawer = true;

									}
								});

								fullUserDetailModel.setFollowingStatus(true);

							}

							@Override
							public void onFailure(Exception e) {

								myprint("buttonFollow onFailure" + e);

								myToastS("Request Failed!");

								cancelProgresBar();

							}
						});

				twitterPostRequestFollow.executeThisRequest(fullUserDetailModel
						.getId());

			}
		});

		buttonUnfollow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("buttonUnfollow ");

				progressDialog.setMessage("UnFollowing - "
						+ fullUserDetailModel.getFullName() + " ...");

				progressDialog.show();

				TwitterPostRequestUnFollow twitterPostRequestUnFollow = new TwitterPostRequestUnFollow(
						MainSingleTon.currentUserModel,
						new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {

							}

							@Override
							public void onSuccess(String jsonResult) {

								myprint("buttonUnfollow onSuccess");

								cancelProgresBar();

								aActivity.runOnUiThread(new Runnable() {

									@Override
									public void run() {

										new Handler().post(new Runnable() {

											@Override
											public void run() {

												buttonFollow
														.setVisibility(View.VISIBLE);

												buttonUnfollow
														.setVisibility(View.INVISIBLE);

												--MainSingleTon.followingCount;

												MainSingleTon.toFollowingModelsIDs
														.remove(fullUserDetailModel
																.getId());

												MainActivity.isNeedToRefreshDrawer = true;

											}
										});
									}
								});

								fullUserDetailModel.setFollowingStatus(false);

							}

							@Override
							public void onFailure(Exception e) {

								cancelProgresBar();

								myToastS("Request Failed!");

								myprint("buttonUnfollow onFailure" + e);

							}
						});

				twitterPostRequestUnFollow
						.executeThisRequest(fullUserDetailModel.getId());

			}
		});

		initDialog();

		return rootView;
	}

	void LoadthisUserData() {

		TwitterUserGETRequest twitterShowUserJSon = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

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

							fullUserDetailModel.setFollowingStatus(jsonObject
									.getString(Const.following)
									.contains("true"));

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

		peramPairs
				.add(new BasicNameValuePair(Const.screen_name, edTextUserName));

		twitterShowUserJSon.executeThisRequest(MainSingleTon.userAccountData,
				peramPairs);

	}

	void showTextsovlp(final FullUserDetailModel fullUserDetailModel) {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				myprint(fullUserDetailModel);

				textView1Nameovlp.setText(fullUserDetailModel.getFullName());

				textView1UserNameovlp.setText("@"
						+ fullUserDetailModel.getUserName());

				myprint("fullUserDetailModel.getNoToFollowing()  "
						+ fullUserDetailModel.getNoToFollowing());

				datasheet.setVisibility(View.VISIBLE);

				TextView01Followingsovlp.setText(fullUserDetailModel
						.getNoToFollowing());

				textViewNotweets.setText(fullUserDetailModel.getNoTweets());

				TextView0FollowedByovlp.setText(fullUserDetailModel
						.getNoFollowers());

				textViewNoOfFollowers.setText("Followers : "
						+ fullUserDetailModel.getNoFollowers());

				imageLoader.DisplayImage(
						fullUserDetailModel.getUserImagerUrl(),
						profile_picorlpAcc);

				if (fullUserDetailModel.isFollowingStatus()) {

					buttonFollow.setVisibility(View.INVISIBLE);

					buttonUnfollow.setVisibility(View.VISIBLE);

				} else {

					buttonFollow.setVisibility(View.VISIBLE);

					buttonUnfollow.setVisibility(View.INVISIBLE);

				}

				relativeLayout1.setVisibility(View.VISIBLE);

				loadfollowers();

			}
		});

	}

	protected void loadfollowers() {

		showProgress();

		TwitterUserGETRequest userGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

					@Override
					public void onSuccess(JSONObject jsonObject) {

					}

					@Override
					public void onSuccess(String jsonResult) {

						cancelProgres();

						myprint("jsonResult" + jsonResult);

						JSONArray jsonArray;

						try {

							JSONObject jsonObject = new JSONObject(jsonResult);

							jsonArray = new JSONArray(
									jsonObject.getString("ids"));

							for (int i = 0; i < jsonArray.length(); ++i) {

								followerIds.add(jsonArray.getString(i));

							}

							myprint("followerIds " + followerIds);

							ShowThisIdsFollowers();

						} catch (JSONException e) {

							e.printStackTrace();

						}

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

		peramPairs.add(new BasicNameValuePair(Const.user_id,
				fullUserDetailModel.getId()));

		userGETRequest.executeThisRequest(url, peramPairs);

	}

	private void addFooterView() {

		LayoutInflater inflater = getActivity().getLayoutInflater();

		viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
				listView, false);

		listView.addFooterView(viewGroup);

		myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

	}

	public void ShowThisIdsFollowers() {

		showProgress();

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

		myprint("Sizes arrayListIds.size() " + followerIds.size());

		if (followerIds.size() == 0) {

		} else {

			for (int i = 0; i < 99; ++i) {

				if (i == 0) {

					userswithComma = followerIds.get(i);

					myprint(i + "++++++++++ i first " + userswithComma);

					followerIdsLoaded.add(followerIds.get(i));

				} else {

					try {

						userswithComma = userswithComma + ","
								+ followerIds.get(i);

						myprint(i + "++++++++++ i other " + userswithComma);

						followerIdsLoaded.add(followerIds.get(i));

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

		myprint("parseJsonResult  ");

		handler.post(new Runnable() {

			@Override
			public void run() {

				viewGroup.setVisibility(View.GONE);

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

				// myprint(followingModel);

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {

						if (FragmentCopyFollowers.this.getActivity() != null) {

							int listCount = listView.getCount();

							toFollowingAdp.tweetModels.add(followingModel);

							listView.setScrollY(listCount);

							toFollowingAdp.notifyDataSetChanged();

						}
					}
				});

			}

		} catch (JSONException e) {

			e.printStackTrace();

		}

		isAlreadyScrolling = false;

	}

	public void FetchReqPagedFollowers() {

		TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

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

		myprint("Sizes followerIdsLoaded.size() " + followerIdsLoaded.size());

		myprint("Sizes followerIds.size() " + followerIds.size());

		if (followerIdsLoaded.size() >= followerIds.size()) {

			handler.post(new Runnable() {

				@Override
				public void run() {

					viewGroup.setVisibility(View.INVISIBLE);

					myprint("Show this Thing 0000 ");
				}
			});

		} else {

			System.out.println("followerIdsLoaded.size() + 99"
					+ (followerIdsLoaded.size() + 99));

			boolean loadFirstTime = true;

			int start = followerIdsLoaded.size();

			int target = followerIdsLoaded.size() + 99;

			for (int i = start; i < (target); ++i) {

				if (loadFirstTime) {

					loadFirstTime = false;

					userswithComma = followerIds.get(i);

					followerIdsLoaded.add(followerIds.get(i));

				} else {

					try {

						userswithComma = "," + followerIds.get(i);

						followerIdsLoaded.add(followerIds.get(i));

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

				adapterModels.add(followingModel);

				// myprint(followingModel);

			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (FragmentCopyFollowers.this.getActivity() != null) {

						toFollowingAdp = new ToFollowingAdapter(getActivity(),
								adapterModels, FragmentCopyFollowers.this
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

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	void myToastL(final String toastMsg) {
		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG)
						.show();
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

				myprint("BUT isAlreadyScrolling ");

			} else {

				isAlreadyScrolling = true;

				try {

					myprint(toFollowingAdp
							.getItem(toFollowingAdp.getCount() - 1));

					if (followerIds.size() == followerIdsLoaded.size()) {

						viewGroup.setVisibility(View.GONE);

					} else {

						viewGroup.setVisibility(View.VISIBLE);

						FetchReqPagedFollowers();
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

	public void followThisId(final String id) {

		if (MainSingleTon.isNeedTOstopFollowing) {

			myToastL("You have Exceeded Follow Limit Today");

			thread.interrupt();

			adapterModels.clear();

			followerIdsLoaded.clear();

			isAlreadyScrolling = true;

			toFollowingAdp = new ToFollowingAdapter(getActivity(),
					adapterModels, FragmentCopyFollowers.this.getActivity());

			listView.setAdapter(toFollowingAdp);

			ShowThisIdsFollowers();

		} else {

			myprint("*********targetCount " + targetCount);
			myprint("*********targetIds " + targetIds);
			myprint("*********copyFollowedCount " + copyFollowedCount);
			myprint("*********id " + id);

			TwitterPostRequestFollow twitterPostRequestFollow = new TwitterPostRequestFollow(
					MainSingleTon.currentUserModel,
					new TwitterRequestCallBack() {

						@Override
						public void onSuccess(JSONObject jsonObject) {

						}

						@Override
						public void onSuccess(String jsonResult) {

							addDMStatus(id);

							myprint("buttonFollow onSuccess");

							if (MainSingleTon.toFollowingModelsIDs.contains(id)) {

							} else {

								++MainSingleTon.followingCount;
								MainSingleTon.toFollowingModelsIDs.add(id);
								MainActivity.isNeedToRefreshDrawer = true;

							}

							copyFollowedCount++;

							aActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {

									setProgressData("Processing - "
											+ copyFollowedCount + "/"
											+ targetCount + "...");

									if (targetCount == copyFollowedCount
											|| isneedTostop) {

										adapterModels.clear();

										followerIdsLoaded.clear();

										isAlreadyScrolling = true;

										toFollowingAdp = new ToFollowingAdapter(
												getActivity(), adapterModels,
												FragmentCopyFollowers.this
														.getActivity());

										listView.setAdapter(toFollowingAdp);

										ShowThisIdsFollowers();

									} else {

										Random random = new Random();

										final int randomDelay = random
												.nextInt(12);

										myprint("$$$$$$$$$$$$$$$$$$$$$ Thread started sleeping");

										new Thread(new Runnable() {

											@Override
											public void run() {

												try {

													Thread.sleep(25000 + randomDelay);

													myprint("$$$$$$$$$$$$$$$$$$$$$ Thread stoped sleeping");

													followThisId(targetIds
															.get((int) copyFollowedCount));

												} catch (InterruptedException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}

											}
										}).start();

									}

									textView3Record.setText("Processed - "
											+ copyFollowedCount + "/"
											+ targetCount + "      Failed : "
											+ failedcount);

								}

							});

							if (targetCount == copyFollowedCount
									|| isneedTostop) {

								cancelProgresBar();

								cancelProgressData();

								myToastS("Failed for " + failedcount + " users");

							} else {

							}

						}

						@Override
						public void onFailure(Exception e) {

							myprint("buttonFollow onFailure" + e);

							// myToastS("Request Failed!");

							cancelProgresBar();

							copyFollowedCount++;

							failedcount++;

							aActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {

									setProgressData("Processing - "
											+ copyFollowedCount + "/"
											+ targetCount + "...");

									if (targetCount == copyFollowedCount
											|| isneedTostop) {

										adapterModels.clear();

										followerIdsLoaded.clear();

										isAlreadyScrolling = true;

										toFollowingAdp = new ToFollowingAdapter(
												getActivity(), adapterModels,
												FragmentCopyFollowers.this
														.getActivity());

										listView.setAdapter(toFollowingAdp);

										ShowThisIdsFollowers();

									} else {

										Random random = new Random();

										final int randomDelay = random
												.nextInt(12);

										myprint("$$$$$$$$$$$$$$$$$$$$$ Thread started sleeping");

										new Thread(new Runnable() {

											@Override
											public void run() {

												try {

													Thread.sleep(25000 + randomDelay);

													myprint("$$$$$$$$$$$$$$$$$$$$$ Thread stoped sleeping");

													followThisId(targetIds
															.get((int) copyFollowedCount));

												} catch (InterruptedException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}

											}
										}).start();

									}

									textView3Record.setText("Processed - "
											+ copyFollowedCount + "/"
											+ targetCount + "      Failed : "
											+ failedcount);

								}

							});

							if (targetCount == copyFollowedCount
									|| isneedTostop) {

								// cancelProgresBar();

								cancelProgressData();

								myToastS("Failed for " + failedcount + " users");

							} else {

							}
						}

					});

			twitterPostRequestFollow.executeThisRequest(id);

		}

	}

	private void addDMStatus(final String id) {

		if (MainSingleTon.autoDmfirstime.contains("yes")) {

			MainSingleTon.autoDmfirstime = "no";

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					new AlertDialog.Builder(getActivity())
							.setTitle("Direct Message")
							.setMessage(
									"A thanks message will be sent to those users. who are following you back!")
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											Editor editor = getActivity()
													.getSharedPreferences(
															"twtboardpro",
															Context.MODE_PRIVATE)
													.edit();
											editor.putString("autoDmfirstime",
													"no");

											editor.putBoolean("autodm", true);

											MainSingleTon.autodm = true;

											MainSingleTon.autoDmfirstime = "no";

											editor.commit();

											addDMStatus(id);

										}
									})
							.setNegativeButton(android.R.string.no,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											Editor editor = getActivity()
													.getSharedPreferences(
															"twtboardpro",
															Context.MODE_PRIVATE)
													.edit();

											editor.putString("autoDmfirstime",
													"no");

											editor.putBoolean("autodm", false);

											editor.commit();

											MainSingleTon.autoDmfirstime = "no";

											MainSingleTon.autodm = false;

										}

									}).setIcon(R.drawable.ic_launcher).show();

				}
			});

		} else {

			myprint("Not to diss[ay fvnefisdfnvko nvkjn");

			if (MainSingleTon.autodm) {

				if (sendingids.contains(id) || sentIds.contains(id)) {

				} else {

					tboardproLocalData.addNewDMsendingId(id,
							MainSingleTon.currentUserModel.getUserid());

					sendingids.add(id);
				}

			} else {

			}
		}
	}

	void setTargetIds(int targetCountEditBox) {

		targetIds.clear();

		ArrayList<String> tmpIds = (ArrayList<String>) followerIds.clone();

		Collections.shuffle(tmpIds);

		boolean isNeedToLoadMore = true;

		int i = 0;

		while (isNeedToLoadMore) {

			if (MainSingleTon.toFollowingModelsIDs.contains(tmpIds.get(i))) {

				myprint("toFollowingModelsIDs Contains ++ ");

			} else if (MainSingleTon.currentUserModel.getUserid().contains(
					tmpIds.get(i))) {

				myprint("MainSingleTon.currentUserModel Contains ++ ");

			} else {

				targetIds.add(tmpIds.get(i));

			}

			i++;

			if (i == tmpIds.size()) {

				isNeedToLoadMore = false;

			}

			if (targetCountEditBox == targetIds.size()) {

				isNeedToLoadMore = false;

			}

		}

		myprint("targetIds " + targetIds);

		targetCount = targetIds.size();

	}

	void initDialog() {

		progressDialogProcess = new Dialog(
				FragmentCopyFollowers.this.getActivity());

		progressDialogProcess.requestWindowFeature(Window.FEATURE_NO_TITLE);

		progressDialogProcess
				.setContentView(R.layout.copy_followers_progress_layout);

		progressDialogProcess.setCancelable(false);

		progressDialogProcess.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.R.color.transparent));

		ProgressBar progressBar = (ProgressBar) progressDialogProcess
				.findViewById(R.id.progressBar1);

		TextView name = (TextView) progressDialogProcess
				.findViewById(R.id.textView1Title);

		name.setText(MainSingleTon.currentUserModel.getUsername());

		processDesc = (TextView) progressDialogProcess
				.findViewById(R.id.textView1);

		final Button button = (Button) progressDialogProcess
				.findViewById(R.id.button1);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("button save");

				if (isneedTostop) {

					button.setBackgroundDrawable(new ColorDrawable(
							R.color.Silver));

					myToastS("please wait stopping process..");

				} else {

					isneedTostop = true;

				}

			}
		});

	}

	void setProgressData(final String progressData) {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				processDesc.setText(progressData);

			}
		});
	}

	void cancelProgressData() {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				progressDialogProcess.dismiss();

			}
		});
	}

}
