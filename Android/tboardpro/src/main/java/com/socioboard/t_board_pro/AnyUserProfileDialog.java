package com.socioboard.t_board_pro;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.adapters.TweetsAdapter;
import com.socioboard.t_board_pro.dialog.ShowTweetComposeDialog;
import com.socioboard.t_board_pro.lazylist.ImageLoader;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestFollow;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestUnFollow;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserShowRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.FullUserDetailModel;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

public class AnyUserProfileDialog {

	RelativeLayout reloutProgress;

	FullUserDetailModel userDatas;

	Bitmap userBitmap, baner;

	ImageView imageView1Banner, profile;

	Button buttonTweet;

	ProgressDialog progressDialog;

	TextView textView1Name, textView1UserName, textView1Tweets,
			TextView01Followings, TextView0FollowedBy, TextView03CreatedAT,
			textViewFavs;

	public ImageLoader imageLoader;

	Handler handler = new Handler();

	String cretedAt, favs;

	Dialog dialog;

	Context activity;

	String userName, userId;

	FullUserDetailModel fullUserDetailModel;

	protected int allSize;

	private Button button1FollowByAll;

	private Button buttonUnfollow;

	private Button buttonFollow;

	TboardproLocalData tboardproLocalData;

	ArrayList<String> sendingids;

	ArrayList<String> sentIds;

	public AnyUserProfileDialog(Context activity, String userName,
			final String userId) {

		myprint("onCreateView  FragmentProfile");

		this.activity = activity;

		this.userName = userName;

		this.userId = userId;

		tboardproLocalData = new TboardproLocalData(activity);

		sendingids = tboardproLocalData
				.getAllSendingIDs(MainSingleTon.currentUserModel.getUserid());

		sentIds = tboardproLocalData
				.getAllSentIDs(MainSingleTon.currentUserModel.getUserid());

		imageLoader = new ImageLoader(activity);

		progressDialog = new ProgressDialog(activity);

		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		progressDialog.setIndeterminate(true);

		progressDialog.setCancelable(false);

		dialog = new Dialog(activity);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.any_user_profile);

		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

		Window window = dialog.getWindow();

		lp.copyFrom(window.getAttributes());

		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		window.setAttributes(lp);

		dialog.show();

		textView1Name = (TextView) dialog.findViewById(R.id.textView1Name);

		profile = (ImageView) dialog.findViewById(R.id.imageView1Profile);

		buttonTweet = (Button) dialog.findViewById(R.id.button1Tweet);

		buttonTweet.setText("Reply");

		ImageView imageView1Close = (ImageView) dialog
				.findViewById(R.id.imageView1Close);

		imageView1Close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();

			}
		});

		buttonTweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ShowTweetComposeDialog showTweetComposeDialog = new ShowTweetComposeDialog(
						AnyUserProfileDialog.this.activity, "@"
								+ AnyUserProfileDialog.this.userName, handler);

				showTweetComposeDialog.showThis();

			}
		});

		button1FollowByAll = (Button) dialog
				.findViewById(R.id.button1FollowByAll);

		buttonUnfollow = (Button) dialog.findViewById(R.id.buttonUnfollow);

		reloutProgress = (RelativeLayout) dialog
				.findViewById(R.id.reloutProgress);

		reloutProgress.setVisibility(View.VISIBLE);

		buttonFollow = (Button) dialog.findViewById(R.id.buttonFollow);

		textView1UserName = (TextView) dialog
				.findViewById(R.id.textView1UserName);

		textView1UserName.setText("@" + userName);

		TextView01Followings = (TextView) dialog
				.findViewById(R.id.TextView01Followings);

		TextView0FollowedBy = (TextView) dialog
				.findViewById(R.id.TextView0FollowedBy);

		TextView03CreatedAT = (TextView) dialog
				.findViewById(R.id.TextView03CreatedAT);

		textView1Tweets = (TextView) dialog.findViewById(R.id.textView1Tweets);

		textViewFavs = (TextView) dialog.findViewById(R.id.textViewFavs);

		imageView1Banner = (ImageView) dialog
				.findViewById(R.id.imageView1Banner);

		TwitterUserShowRequest userShowRequest = new TwitterUserShowRequest(
				MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

					@Override
					public void onSuccess(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						myprint("onSuccess " + jsonObject);
						parseJsonResultForAccountData(jsonObject);
					}

					@Override
					public void onSuccess(String jsonResult) {
						cancelProgres();

						myToastS("Failed ti load");
					}

					@Override
					public void onFailure(Exception e) {
						// TODO Auto-generated method stub
						cancelProgres();
					}
				});

		userShowRequest.executeThisRequest(userName);

		buttonFollow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (MainSingleTon.isNeedTOstopFollowing) {

					myToastS("You have Exceeded Follow Limit Today");

				} else {

					progressDialog
							.setMessage(AnyUserProfileDialog.this.userName
									+ " Following...");

					progressDialog.show();

					TwitterPostRequestFollow twitterPostRequestFollow = new TwitterPostRequestFollow(
							MainSingleTon.currentUserModel,
							new TwitterRequestCallBack() {

								@Override
								public void onSuccess(JSONObject jsonObject) {

								}

								@Override
								public void onSuccess(String jsonResult) {

									progressDialoghideProgress();

									addDMStatus(AnyUserProfileDialog.this.userId);

									myprint("buttonFollow onSuccess");

									TweetsAdapter.handler.post(new Runnable() {

										@Override
										public void run() {

											buttonFollow
													.setVisibility(View.INVISIBLE);

											buttonUnfollow
													.setVisibility(View.VISIBLE);

											fullUserDetailModel
													.setFollowingStatus(true);

										}
									});

								}

								@Override
								public void onFailure(Exception e) {

									myprint("buttonFollow onFailure" + e);

									progressDialoghideProgress();

								}

							});

					twitterPostRequestFollow
							.executeThisRequest(AnyUserProfileDialog.this.userId);
				}
			}
		});

		buttonUnfollow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				progressDialog.setMessage(AnyUserProfileDialog.this.userName
						+ " UnFollowing...");

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

								TweetsAdapter.handler.post(new Runnable() {

									@Override
									public void run() {

										progressDialog.cancel();

										buttonFollow
												.setVisibility(View.VISIBLE);

										buttonUnfollow
												.setVisibility(View.INVISIBLE);

										fullUserDetailModel
												.setFollowingStatus(false);

									}
								});

							}

							@Override
							public void onFailure(Exception e) {

								progressDialoghideProgress();

								myprint("buttonUnfollow onFailure" + e);
							}
						});

				twitterPostRequestUnFollow
						.executeThisRequest(AnyUserProfileDialog.this.userId);
			}

		});

		button1FollowByAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (MainSingleTon.isNeedTOstopFollowing) {

					myToastS("You have Exceeded Follow Limit Today");

				} else {

					TboardproLocalData localData = new TboardproLocalData(
							AnyUserProfileDialog.this.activity);

					List<ModelUserDatas> listDatas = localData
							.getAllUsersDataArlist();

					allSize = listDatas.size();

					progressDialog
							.setMessage(AnyUserProfileDialog.this.userName
									+ " Following by..." + allSize);

					progressDialog.show();

					for (int i = 0; i < listDatas.size(); i++) {

						TwitterPostRequestFollow twitterPostRequestFollow = new TwitterPostRequestFollow(
								listDatas.get(i), new TwitterRequestCallBack() {

									@Override
									public void onSuccess(JSONObject jsonObject) {

									}

									@Override
									public void onSuccess(String jsonResult) {

										myprint("buttonFollow onSuccess");

										TweetsAdapter.handler
												.post(new Runnable() {

													@Override
													public void run() {

														fullUserDetailModel
																.setFollowingStatus(true);

														buttonFollow
																.setVisibility(View.INVISIBLE);

														buttonUnfollow
																.setVisibility(View.VISIBLE);

														button1FollowByAll
																.setText("Followed By All");

													}
												});

										progressDialog.cancel();

									}

									@Override
									public void onFailure(Exception e) {

										myprint("buttonFollow onFailure" + e);

										progressDialog.cancel();
									}
								});

						twitterPostRequestFollow
								.executeThisRequest(AnyUserProfileDialog.this.userId);

					}

				}
			}
		});

		buttonFollow.setVisibility(View.INVISIBLE);

		buttonUnfollow.setVisibility(View.INVISIBLE);

	}

	void myToastS(final String toastMsg) {

		TweetsAdapter.handler.post(new Runnable() {

			@Override
			public void run() {

				Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT).show();

			}
		});
	}

	void myToastL(final String toastMsg) {

		TweetsAdapter.handler.post(new Runnable() {

			@Override
			public void run() {

				Toast.makeText(activity, toastMsg, Toast.LENGTH_LONG).show();

			}
		});
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	protected void parseJsonResultForAccountData(JSONObject jsonResult) {

		myprint("parseJsonResult AnyUserProfileDialog  ");

		try {

			myprint("jsonResult   = " + jsonResult);

			fullUserDetailModel = new FullUserDetailModel();

			fullUserDetailModel.setFollowingStatus(jsonResult.getString(
					Const.following).contains("true"));

			fullUserDetailModel.setFollowingStatus(jsonResult.getString(
					Const.following).contains("true"));

			fullUserDetailModel.setId(jsonResult.getString(Const.id_str));

			fullUserDetailModel.setFullName(jsonResult.getString(Const.name));

			fullUserDetailModel.setUserName(jsonResult
					.getString(Const.screen_name));

			fullUserDetailModel.setNoFollowers(jsonResult
					.getString(Const.followers_count));

			fullUserDetailModel.setNoToFollowing(jsonResult
					.getString(Const.friends_count));

			fullUserDetailModel.setNoTweets(jsonResult
					.getString(Const.statuses_count));

			fullUserDetailModel.setUserImagerUrl(jsonResult
					.getString(Const.profile_image_url));

			if (jsonResult.has(Const.profile_banner_url)) {

				myprint("Const.profile_banner_url  <"
						+ Const.profile_banner_url + ">");

				myprint("jsonResult .getString(Const.profile_banner_url)>"
						+ jsonResult.getString(Const.profile_banner_url) + ">");

				fullUserDetailModel.setBannerUrl(jsonResult
						.getString(Const.profile_banner_url));

				fullUserDetailModel.setUserName("@"
						+ jsonResult.getString(Const.screen_name));

				new DownloadIamgeBanner().execute(fullUserDetailModel
						.getBannerUrl());

			}

			cretedAt = jsonResult.getString(Const.created_at);
			favs = jsonResult.getString(Const.favourites_count);
			userDatas = fullUserDetailModel;

			myprint(fullUserDetailModel);

			handler.post(new Runnable() {

				@Override
				public void run() {

					cancelProgres();

					showTexts(fullUserDetailModel);

					if (fullUserDetailModel.isFollowingStatus()) {

						buttonFollow.setVisibility(View.INVISIBLE);

						buttonUnfollow.setVisibility(View.VISIBLE);

					} else {

						buttonFollow.setVisibility(View.VISIBLE);

						buttonUnfollow.setVisibility(View.INVISIBLE);

					}

					// button1FollowByAll.setVisibility(View.VISIBLE);

				}
			});

			new DownloadIamge().execute(userDatas.getUserImagerUrl());

		} catch (JSONException e) {

			e.printStackTrace();

		}

		// cancelProgres();

	}

	private void showTexts(final FullUserDetailModel fullUserDetailModel) {

		textView1Name.setText(fullUserDetailModel.getFullName());
		textView1Tweets.setText(fullUserDetailModel.getNoTweets());
		TextView01Followings.setText(fullUserDetailModel.getNoToFollowing());
		TextView0FollowedBy.setText(fullUserDetailModel.getNoFollowers());
		TextView03CreatedAT.setText(cretedAt);
		textViewFavs.setText(favs);

	}

	void showProgress() {

		reloutProgress.setVisibility(View.VISIBLE);

	}

	void cancelProgres() {

		reloutProgress.setVisibility(View.INVISIBLE);

	}

	// + + + + + + + + + + +

	class DownloadIamge extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			final String urlImg = params[0].toString();
			handler.post(new Runnable() {

				@Override
				public void run() {

					imageLoader.DisplayImage(urlImg, profile);

				}
			});

			myprint("Download cPOmpleteas");

			return null;
		}
	}

	class DownloadIamgeBanner extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			final String urlImg = params[0].toString();

			handler.post(new Runnable() {

				@Override
				public void run() {

					imageLoader.DisplayImage(urlImg, imageView1Banner);

				}
			});
			return null;
		}
	}

	// + + + + + + + + + + +
	private void addDMStatus(final String id) {

		if (MainSingleTon.autoDmfirstime.contains("yes")) {

			handler.post(new Runnable() {

				@Override
				public void run() {

					new AlertDialog.Builder(activity)
							.setTitle("Direct Message")
							.setMessage(
									"A thanks message will be sent to those users. who are following you back!")
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											Editor editor = activity
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

											Editor editor = activity
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

	private void progressDialogShowProgress() {

		handler.post(new Runnable() {

			@Override
			public void run() {

				progressDialog.cancel();

			}
		});
	}

	private void progressDialoghideProgress() {

		handler.post(new Runnable() {

			@Override
			public void run() {

				progressDialog.cancel();

			}

		});

	}

}
