package com.socioboard.t_board_pro.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.adapters.ToFollowingAdapter;
import com.socioboard.t_board_pro.lazylist.ImageLoader;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestUnFollow;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

public class FragmentUnfollowUsers extends Fragment implements OnScrollListener {

	View rootView;
	ListView listView;
	public ToFollowingAdapter toFollowingAdp;
	RelativeLayout reloutProgress;
	Activity aActivity;
	EditText editText1Range;
	ProgressDialog progressDialog;
	Dialog progressDialogProcess;
	TextView processDesc, textViewNoOfFollowers, textView3Record;
	Thread thread;

	Timer timer = new Timer();

	TboardproLocalData tboardproLocalData;

	public ImageLoader imageLoader;

	ArrayList<ToFollowingModel> adapterModels = new ArrayList<ToFollowingModel>();

	public ArrayList<String> followerIds = new ArrayList<String>();

	public ArrayList<String> followerIdsLoaded = new ArrayList<String>();

	public ArrayList<String> targetIds = new ArrayList<String>();

	boolean isAlreadyScrolling = true, isneedTostop = false;

	ViewGroup viewGroup;

	long unFollowedCount = 0, targetCount, failedcount = 0;

	Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		aActivity = getActivity();

		rootView = inflater.inflate(R.layout.unfollowers_fragment, container,
				false);

		reloutProgress = (RelativeLayout) rootView
				.findViewById(R.id.reloutProgress);

		listView = (ListView) rootView.findViewById(R.id.listView1);

		textViewNoOfFollowers = (TextView) rootView
				.findViewById(R.id.textViewNoOfFollowers);

		textView3Record = (TextView) rootView
				.findViewById(R.id.textView3Record);

		editText1Range = (EditText) rootView.findViewById(R.id.editText1Range);

		toFollowingAdp = new ToFollowingAdapter(getActivity(), adapterModels,
				aActivity);

		// /////////////////////////////////////////////////////////

		unFollowedCount = 0;
		targetCount = 0;
		failedcount = 0;
		followerIds = (ArrayList<String>) MainSingleTon.nonFollowersIds.clone();
		targetIds.clear();

		adapterModels.clear();

		followerIdsLoaded.clear();

		isAlreadyScrolling = true;

		toFollowingAdp = new ToFollowingAdapter(getActivity(), adapterModels,
				FragmentUnfollowUsers.this.getActivity());

		listView.setAdapter(toFollowingAdp);

		// //////////////////////////////////////////////////

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		listView.setOnScrollListener(this);

		addFooterView();
		cancelProgres();
		viewGroup.setVisibility(View.INVISIBLE);

		Button buttonCopyFollowers = (Button) rootView
				.findViewById(R.id.button1);

		buttonCopyFollowers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (MainSingleTon.isNeedTOstopFollowing) {

					myToastL("You have Exceeded UnFollow Limit Today");

				} else {

					followerIds = (ArrayList<String>) MainSingleTon.nonFollowersIds
							.clone();

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

								unFollowedCount = 0;

								progressDialogProcess.show();

								setProgressData("Processing - "
										+ unFollowedCount + "/" + targetCount
										+ "...");

								thread = new Thread(new Runnable() {

									@Override
									public void run() {

										UnfollowThisId(targetIds.get(0));

									}
								});

								thread.start();

							}

						}
					}
				}
			}
		});

		textViewNoOfFollowers.setText("Total NonFollowers : "
				+ MainSingleTon.nonFollowersIds.size());

		initDialog();

		ShowThisIdsFollowers();

		startScheduller();

		return rootView;

	}

	void startScheduller() {

		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				handler.post(new Runnable() {

					@Override
					public void run() {

						textViewNoOfFollowers.setText("Total NonFollowers : "
								+ MainSingleTon.nonFollowersIds.size());

					}
				});

			}
		}, 2000, 1000);

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

			cancelProgres();

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
						e.printStackTrace();
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

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				textViewNoOfFollowers.setText("Total NonFollowers : "
						+ MainSingleTon.nonFollowersIds.size());

			}
		});

	}

	protected void parseJsonResultPaged(String jsonResult) {

		myprint("parseJsonResult FragmentUnfollowUsers11111 ");

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

				if (FragmentUnfollowUsers.this.getActivity() != null) {

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {

							int listCount = listView.getCount();

							toFollowingAdp.tweetModels.add(followingModel);

							listView.setScrollY(listCount);

							toFollowingAdp.notifyDataSetChanged();

						}
					});
				}
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

			for (int i = followerIdsLoaded.size(); i < (followerIdsLoaded
					.size() + 99); ++i) {

				if (i == followerIdsLoaded.size()) {

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

		myprint("parseJsonResult  FragmentUnfollowUsers222222");

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

				myprint(followingModel);

			}

			if (FragmentUnfollowUsers.this.getActivity() != null) {

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {

						textViewNoOfFollowers.setText("Total NonFollowers : "
								+ MainSingleTon.nonFollowersIds.size());

						toFollowingAdp = new ToFollowingAdapter(getActivity(),
								adapterModels, FragmentUnfollowUsers.this
										.getActivity());

						listView.setAdapter(toFollowingAdp);

						isAlreadyScrolling = false;
					}

				});
			}

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

				// DO NOTHING
				myprint("BUT isAlreadyScrolling ");

			} else {
				
				isAlreadyScrolling = true;

				myprint(toFollowingAdp.getItem(toFollowingAdp.getCount() - 1));

				if (followerIds.size() == followerIdsLoaded.size()) {

					viewGroup.setVisibility(View.GONE);
					
 				} else {

					viewGroup.setVisibility(View.VISIBLE);
  
					FetchReqPagedFollowers();
				}

			}

		} else {

			myprint("NOOOOOOOOO DONt LOad");

		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	public void UnfollowThisId(final String id) {

		if (MainSingleTon.isNeedTOstopFollowing) {

			myToastL("You have Exceeded Follow Limit Today");

			thread.interrupt();

			adapterModels.clear();

			followerIdsLoaded.clear();

			isAlreadyScrolling = true;

			toFollowingAdp = new ToFollowingAdapter(getActivity(),
					adapterModels, FragmentUnfollowUsers.this.getActivity());

			listView.setAdapter(toFollowingAdp);

			ShowThisIdsFollowers();

		} else {

			myprint("*********targetCount " + targetCount);
			myprint("*********targetIds " + targetIds);
			myprint("*********unFollowedCount " + unFollowedCount);
			myprint("*********id " + id);

			TwitterPostRequestUnFollow twitterPostRequestFollow = new TwitterPostRequestUnFollow(
					MainSingleTon.currentUserModel,
					new TwitterRequestCallBack() {

						@Override
						public void onSuccess(JSONObject jsonObject) {

						}

						@Override
						public void onSuccess(String jsonResult) {

							myprint("buttonUnFollow onSuccess");

							MainSingleTon.toFollowingModelsIDs.remove(id.trim());

							MainActivity.isNeedToRefreshDrawer = true;

							unFollowedCount++;

							aActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {

									textViewNoOfFollowers
											.setText("Total NonFollowers : "
													+ MainSingleTon.nonFollowersIds
															.size());

									setProgressData("Processing - "
											+ unFollowedCount + "/"
											+ targetCount + "...");

									if (targetCount == unFollowedCount
											|| isneedTostop) {

										adapterModels.clear();

										followerIdsLoaded.clear();

										isAlreadyScrolling = true;

										toFollowingAdp = new ToFollowingAdapter(
												getActivity(), adapterModels,
												FragmentUnfollowUsers.this
														.getActivity());

										listView.setAdapter(toFollowingAdp);

										ShowThisIdsFollowers();

									} else {

										try {

											myprint("$$$$$$$$$$$$$$$$$$$$$ Thread started sleeping");

											Thread.sleep(3000);

											myprint("$$$$$$$$$$$$$$$$$$$$$ Thread stoped sleeping");

										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										UnfollowThisId(targetIds
												.get((int) unFollowedCount));

									}

									textView3Record.setText("Processed - "
											+ unFollowedCount + "/"
											+ targetCount + "      Failed : "
											+ failedcount);

								}

							});

							if (targetCount == unFollowedCount || isneedTostop) {

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

							unFollowedCount++;

							failedcount++;

							aActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {

									setProgressData("Processing - "
											+ unFollowedCount + "/"
											+ targetCount + "...");

									if (targetCount == unFollowedCount
											|| isneedTostop) {

										adapterModels.clear();

										followerIdsLoaded.clear();

										isAlreadyScrolling = true;

										toFollowingAdp = new ToFollowingAdapter(
												getActivity(), adapterModels,
												FragmentUnfollowUsers.this
														.getActivity());

										listView.setAdapter(toFollowingAdp);

										ShowThisIdsFollowers();

									} else {
										try {

											myprint("$$$$$$$$$$$$$$$$$$$$$ Thread started sleeping");

											Thread.sleep(2000);

											myprint("$$$$$$$$$$$$$$$$$$$$$ Thread stoped sleeping");

										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										UnfollowThisId(targetIds
												.get((int) unFollowedCount));

									}

									textView3Record.setText("Processed - "
											+ unFollowedCount + "/"
											+ targetCount + "      Failed : "
											+ failedcount);

								}

							});

							if (targetCount == unFollowedCount || isneedTostop) {

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

	void setTargetIds(int targetCountEditBox) {

		targetIds.clear();

		ArrayList<String> tmpIds = (ArrayList<String>) followerIds.clone();

		Collections.shuffle(tmpIds);

		boolean isNeedToLoadMore = true;

		int i = 0;

		while (isNeedToLoadMore) {

			targetIds.add(tmpIds.get(i));

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
				FragmentUnfollowUsers.this.getActivity());

		progressDialogProcess.requestWindowFeature(Window.FEATURE_NO_TITLE);

		progressDialogProcess
				.setContentView(R.layout.copy_followers_progress_layout);

		progressDialogProcess.setCancelable(false);

		progressDialogProcess.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.R.color.transparent));

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

				myprint("button isneedTostop");

				if (isneedTostop) {

					button.setBackgroundDrawable(new ColorDrawable(
							R.color.Silver));

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

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	
		timer.cancel();
	}
}
