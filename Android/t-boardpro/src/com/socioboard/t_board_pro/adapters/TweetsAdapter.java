package com.socioboard.t_board_pro.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.AnyUserProfileDialog;
import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.dialog.ShowTweetComposeDialog;
import com.socioboard.t_board_pro.lazylist.ImageLoader;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestPerams;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TweetModel;
import com.socioboard.t_board_pro.util.Utils;
import com.socioboard.tboardpro.R;

public class TweetsAdapter extends BaseAdapter {

	
	private Context context;
	public ArrayList<TweetModel> tweetModels;
	public ImageLoader imageLoader;
	public Context activity;
	public static Handler handler = new Handler();
	public ProgressDialog progressDialog;
	private final SimpleDateFormat monthDayYearformatter = new SimpleDateFormat(
			"MMMMM dd, yyyy");
 	
	public TweetsAdapter(ArrayList<TweetModel> tweetModels, Context activity) {

		this.activity = activity;
		this.tweetModels = tweetModels;
		imageLoader = new ImageLoader(context);
		progressDialog = new ProgressDialog(this.activity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);

	}

	@Override
	public int getCount() {
		return tweetModels.size();
	}

	@Override
	public TweetModel getItem(int position) {
		return tweetModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			LayoutInflater mInflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.single_tweet, parent,
					false);
		}

		final TweetModel tweetModel = getItem(position);

		ImageView profilePic = (ImageView) convertView
				.findViewById(R.id.profile_pic);

		ImageView imageView1Tweet = (ImageView) convertView
				.findViewById(R.id.imageView1Tweet);

		String extractImageUrlS = extractImageUrl(tweetModel.getTweeet_str());

		if (extractImageUrlS != null) {

			imageLoader.DisplayImage(extractImageUrlS, imageView1Tweet);

		}

		final ImageView imageViewFav;

		imageViewFav = (ImageView) convertView.findViewById(R.id.imageView1Fav);

		ImageView imageViewReply = (ImageView) convertView
				.findViewById(R.id.imageView1REply);

		final ImageView imageView2Retweet = (ImageView) convertView
				.findViewById(R.id.imageView1Retweet);

		profilePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String newUser = tweetModel.getUserName();

				newUser = newUser.replace("@", "");

				if (newUser.contains(MainSingleTon.currentUserModel
						.getUsername())) {

					return;
				}

				AnyUserProfileDialog anyUserProfile = new AnyUserProfileDialog(
						activity, newUser, tweetModel.getUserID());

			}
		});

		TextView userName = (TextView) convertView.findViewById(R.id.usersName);

		userName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				handler.post(new Runnable() {

					@Override
					public void run() {

						String newUser = tweetModel.getUserName();

						newUser = newUser.replace("@", "");

						if (newUser.contains(MainSingleTon.currentUserModel
								.getUsername())) {
							return;
						}

						AnyUserProfileDialog anyUserProfile = new AnyUserProfileDialog(
								activity, (newUser), tweetModel.getUserID());

					}
				});

			}
		});

		TextView favCount = (TextView) convertView
				.findViewById(R.id.tweetViewfavs);
		TextView retweetCount = (TextView) convertView
				.findViewById(R.id.tweetViewRetweet);
		TextView fullname = (TextView) convertView
				.findViewById(R.id.usersFullName);
		TextView tweettime = (TextView) convertView
				.findViewById(R.id.textView1Time);

		TextView tweetView = (TextView) convertView
				.findViewById(R.id.tweetView);

		if (tweetModel.isRetweeted()) {
			imageView2Retweet
					.setImageResource(R.drawable.ic_action_rt_on_focused);
		} else {
			imageView2Retweet
					.setImageResource(R.drawable.ic_action_rt_off_focused);
		}

		if (tweetModel.isIsfavourated()) {

			imageViewFav.setImageResource(R.drawable.ic_action_fave_on_focused);

		} else {

			imageViewFav
					.setImageResource(R.drawable.ic_action_fave_off_focused);

		}

		userName.setText(tweetModel.getUserName());

		fullname.setText(tweetModel.getFullName());

		retweetCount.setText("" + tweetModel.getRetweetCount());

		favCount.setText("" + tweetModel.getFavCount());

		String[] timeArray = tweetModel.getTweetTime().split(" ");

		String weekday = timeArray[0], month = timeArray[1], day = timeArray[2], timehrMinsec = timeArray[3], gmtStatus = timeArray[4], year = timeArray[5];

		String reqFormat = Utils.GetLocalDateStringFromUTCString(year + "-"
				+ Utils.monthInt(month) + "-" + day + "T" + timehrMinsec
				+ gmtStatus);

		// Tue May 12 00:27:11 +0000 2015

		// myprint("We Made " + year + "-" + Utils.monthInt(month) + "-" + day +
		// "T" + timehrMinsec + gmtStatus);

		// required ------> "2015-05-09T16:43:02+0000"

		myprint("reqFormat " + reqFormat);

		tweettime.setText(reqFormat);

		imageLoader.DisplayImage(tweetModel.getUserImagerUrl(), profilePic);

		tweetView.setMovementMethod(LinkMovementMethod.getInstance());

		tweetView.setText(tweetModel.getTweeet_str());

		imageView2Retweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (tweetModel.isRetweeted()) {
					return;
				}

				myprint(" imageView2Retweet");

				progressDialog.setMessage("Wait...");

				progressDialog.show();

				TwitterPostRequestPerams postPeramsRequest = new TwitterPostRequestPerams(
						MainSingleTon.currentUserModel,
						new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {

								myprint("");
							}

							@Override
							public void onSuccess(String jsonResult) {

								getItem(position).setRetweeted(true);

								long count = getItem(position)
										.getRetweetCount();

								++count;

								MainSingleTon.tweetsCount++;

								MainActivity.isNeedToRefreshDrawer = true;

								getItem(position).setRetweetCount(count);

								progressDialog.cancel();

								handler.post(new Runnable() {

									@Override
									public void run() {

										imageView2Retweet
												.setImageResource(R.drawable.ic_action_rt_on_focused);
										notifyDataSetChanged();
									}
								});

								myprint("onSuccess " + jsonResult);

							}

							@Override
							public void onFailure(Exception e) {

								myprint("onFailure ");

								progressDialog.cancel();

								showToast("Failed!");

							}

						});

				String url = MainSingleTon.reTweeting + tweetModel.getTweetId()
						+ ".json";

				List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

				peramPairs.add(new BasicNameValuePair("id", tweetModel
						.getTweetId()));

				postPeramsRequest.executeThisRequest(url, peramPairs);

			}
		});

		imageViewReply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint(" imageViewReply");

				String mentions = tweetModel.getUserName();

				Pattern pattern = Pattern.compile("@\\s*(\\w+)");

				String input = tweetModel.getTweeet_str();

				Matcher m = pattern.matcher(input);

				while (m.find()) {

					mentions = mentions + " " + m.group();

					System.out.println("" + m.group());
				}

				ShowTweetComposeDialog tweetComposeDialog = new ShowTweetComposeDialog(
						activity, mentions);

				tweetComposeDialog.showThis();

			}
		});

		imageViewFav.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {

				progressDialog.setMessage("Just a moment...");

				progressDialog.show();

				myprint(" imageViewFav");

				String url;

				TwitterPostRequestPerams postPeramsRequest = new TwitterPostRequestPerams(
						MainSingleTon.currentUserModel,
						new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {

								myprint("");

							}

							@Override
							public void onSuccess(String jsonResult) {

								myprint("onSuccess " + jsonResult);

								handler.post(new Runnable() {

									@Override
									public void run() {

										long count = getItem(position)
												.getFavCount();

										progressDialog.cancel();

										if (tweetModel.isIsfavourated()) {

											imageViewFav
													.setImageResource(R.drawable.ic_action_fave_off_focused);

											getItem(position).setIsfavourated(
													false);

											--count;

											MainSingleTon.favoritesCount--;

											MainActivity.isNeedToRefreshDrawer = true;

										} else {

											imageViewFav
													.setImageResource(R.drawable.ic_action_fave_on_focused);

											getItem(position).setIsfavourated(
													true);

											MainSingleTon.favoritesCount++;

											MainActivity.isNeedToRefreshDrawer = true;

											++count;

										}

										getItem(position).setFavCount(count);

										notifyDataSetChanged();

									}
								});

							}

							@Override
							public void onFailure(Exception e) {
								// TODO Auto-generated method stub
								myprint("onFailure ");
								if (progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								handler.post(new Runnable() {

									@Override
									public void run() {

										Toast.makeText(activity, "Failed",
												Toast.LENGTH_LONG).show();
									}
								});
							}
						});

				List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

				peramPairs.add(new BasicNameValuePair(Const.id, tweetModel
						.getTweetId()));

				if (tweetModel.isIsfavourated()) {

					url = MainSingleTon.favouritesDestroy;

				} else {

					url = MainSingleTon.favouritesCreate;

				}

				postPeramsRequest.executeThisRequest(url, peramPairs);

			}
		});

		return convertView;
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	String extractImageUrl(String tweet) {

		String url = null;

		int start = tweet.indexOf("https://pbs");

		if (start < 0) {
			return url;
		}

		String started = tweet.substring(start);

		int end = started.indexOf(" ");

		if (end < 0) {
			url = started.substring(0, started.length() - 1);
		} else {
			url = started.substring(0, end);
		}

		myprint("extractImageUrl  " + url);

		return url;
	}

	void showToast(final String msg) {

		handler.post(new Runnable() {

			@Override
			public void run() {

				Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
			}
		});

	}
}
