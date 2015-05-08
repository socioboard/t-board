package com.socioboard.t_board_pro.adapters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socioboard.t_board_pro.lazylist.ImageLoader;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestFollow;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestUnFollow;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

public class ToFollowingAdapter extends BaseAdapter {

	public Context context;

	private ArrayList<ToFollowingModel> tweetModels;

	Activity activity;
	ImageLoader imageLoader;

	ProgressDialog progressDialog;

	public ToFollowingAdapter(Context context,
			ArrayList<ToFollowingModel> tweetModels, Activity activity) {

		this.context = context;

		this.tweetModels = tweetModels;

		this.activity = activity;
		imageLoader = new ImageLoader(context);

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				progressDialog = new ProgressDialog(
						ToFollowingAdapter.this.context);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setIndeterminate(true);
				progressDialog.setCancelable(false);
			}
		});

	}

	@Override
	public int getCount() {
		return tweetModels.size();
	}

	@Override
	public ToFollowingModel getItem(int position) {
		return tweetModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.to_following_item, parent,
					false);
		}

		ToFollowingModel toFollowingModel = getItem(position);

		ImageView profilePic = (ImageView) convertView
				.findViewById(R.id.profile_pic);

		TextView userName = (TextView) convertView
				.findViewById(R.id.followerName);

		userName.setText(toFollowingModel.getUserName());

		TextView noOfTweets = (TextView) convertView
				.findViewById(R.id.textViewNotweets);

		noOfTweets.setText(toFollowingModel.getNoTweets());

		TextView noOfFollowings = (TextView) convertView
				.findViewById(R.id.textViewNoOfFollowings);

		noOfFollowings.setText(toFollowingModel.getNoToFollowing());

		TextView noOfFollowers = (TextView) convertView
				.findViewById(R.id.textViewNoofFollowers);

		noOfFollowers.setText(toFollowingModel.getNoFollowers());

		final Button buttonFollow = (Button) convertView
				.findViewById(R.id.buttonFollow);

		final Button buttonUnfollow = (Button) convertView
				.findViewById(R.id.buttonUnfollow);

		if (toFollowingModel.isFollowingStatus()) {

			buttonFollow.setVisibility(View.INVISIBLE);

			buttonUnfollow.setVisibility(View.VISIBLE);

		} else {

			buttonFollow.setVisibility(View.VISIBLE);

			buttonUnfollow.setVisibility(View.INVISIBLE);

		}

		final int pos = position;

		buttonFollow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("buttonFollow " + getItem(pos));

				progressDialog.setMessage(getItem(pos).getUserName()
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

								hideProgress();

								myprint("buttonFollow onSuccess");

								activity.runOnUiThread(new Runnable() {

									@Override
									public void run() {

										buttonFollow.setVisibility(View.INVISIBLE);

										buttonUnfollow.setVisibility(View.VISIBLE);

									}
								});

								getItem(pos).setFollowingStatus(true);

							}

							@Override
							public void onFailure(Exception e) {

								myprint("buttonFollow onFailure" + e);
								
								hideProgress();
							}
						});

				twitterPostRequestFollow.executeThisRequest(getItem(pos)
						.getId());

			}
		});

		buttonUnfollow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("buttonUnfollow " + getItem(pos));
				progressDialog.setMessage(getItem(pos).getUserName()
						+ " UnFollowing...");

				showProgress();

				TwitterPostRequestUnFollow twitterPostRequestUnFollow = new TwitterPostRequestUnFollow(
						MainSingleTon.currentUserModel,
						new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {

							}

							@Override
							public void onSuccess(String jsonResult) {

								myprint("buttonUnfollow onSuccess");

                                hideProgress();
								
                                activity.runOnUiThread(new Runnable() {

									@Override
									public void run() {

										new Handler().post(new Runnable() {

											@Override
											public void run() {

												buttonFollow
														.setVisibility(View.VISIBLE);

												buttonUnfollow
														.setVisibility(View.INVISIBLE);

											}
										});
									}
								});

								getItem(pos).setFollowingStatus(false);

							}

							@Override
							public void onFailure(Exception e) {

								hideProgress();
								myprint("buttonUnfollow onFailure" + e);
							}
						});

				twitterPostRequestUnFollow.executeThisRequest(getItem(pos)
						.getId());

			}
		});

		imageLoader.DisplayImage(toFollowingModel.getUserImagerUrl(), profilePic);
 
		return convertView;
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	public class ImageLoaders extends AsyncTask<Object, String, Bitmap> {

		ImageView profilePic;

		private Bitmap bitmap = null;

		Integer position;

		@Override
		protected Bitmap doInBackground(Object... parameters) {

			// Get the passed arguments here
			profilePic = (ImageView) parameters[0];

			Integer position = (Integer) parameters[1];

			try {

				Bitmap userImage = BitmapFactory.decodeStream(new URL(getItem(
						position).getUserImagerUrl()).openStream());

				getItem(position).setUserimage(userImage);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(final Bitmap bitmap) {

			if (bitmap != null) {

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {

						profilePic.setImageBitmap(bitmap);

						notifyDataSetChanged();

					}
				});

			}
		}
	}

	void showProgress() {

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				progressDialog.show();

			}
		});
	}

	void hideProgress() {

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				progressDialog.cancel();

			}
		});
	}

}
