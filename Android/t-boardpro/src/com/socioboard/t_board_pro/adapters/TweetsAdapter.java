package com.socioboard.t_board_pro.adapters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.socioboard.t_board_pro.lazylist.ImageLoader;
import com.socioboard.t_board_pro.util.TweetModel;
import com.socioboard.tboardpro.R;

public class TweetsAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<TweetModel> tweetModels;
	public ImageLoader imageLoader;

	public TweetsAdapter(Context context, ArrayList<TweetModel> tweetModels) {
		this.context = context;
		this.tweetModels = tweetModels;
		imageLoader = new ImageLoader(context);

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
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.single_tweet, parent,
					false);
		}

		TweetModel tweetModel = getItem(position);

		ImageView profilePic = (ImageView) convertView
				.findViewById(R.id.profile_pic);

		TextView userName = (TextView) convertView.findViewById(R.id.usersName);

		TextView tweetView = (TextView) convertView
				.findViewById(R.id.tweetView);

 		userName.setText(tweetModel.getUserName());

		imageLoader.DisplayImage(tweetModel.getUserImagerUrl(), profilePic);

		tweetView.setText(getItem(position).getTweeet_str());

		return convertView;
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

				new Handler().post(new Runnable() {

					@Override
					public void run() {

						profilePic.setImageBitmap(bitmap);

						notifyDataSetChanged();

					}
				});

			}
		}
	}
}
