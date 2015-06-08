package com.socioboard.t_board_pro.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.SchedulleComposeActivity;
import com.socioboard.t_board_pro.fragments.FragmentProfile;
import com.socioboard.t_board_pro.fragments.FragmentSchedule;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.SchTweetModel;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.TweetSchedullerReceiver;
import com.socioboard.tboardpro.R;

public class SchTweetsAdapter extends BaseAdapter {

	AlarmManager alarmManagers;

	private Context context;
	private ArrayList<SchTweetModel> schTweetModels;
	private final SimpleDateFormat monthDayYearformatter = new SimpleDateFormat(
			"MMMMM dd, yyyy");

	public SchTweetsAdapter(Context context,
			ArrayList<SchTweetModel> schTweetModels) {

		this.context = context;
		this.schTweetModels = schTweetModels;

	}

	@Override
	public int getCount() {
		return schTweetModels.size();
	}

	@Override
	public SchTweetModel getItem(int position) {
		return schTweetModels.get(position);
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
			convertView = mInflater.inflate(R.layout.sch_tweet_item2, parent,
					false);
		}

		final SchTweetModel schTweetModel = getItem(position);

		TextView userName = (TextView) convertView
				.findViewById(R.id.textViewUser);

		TextView txtTime = (TextView) convertView
				.findViewById(R.id.textViewTime);

		ImageView imageViewRemove = (ImageView) convertView
				.findViewById(R.id.imageView2Remove);

		imageViewRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openRemoveTweetDialog(schTweetModel);

			}
		});

		TextView txtDate = (TextView) convertView
				.findViewById(R.id.textViewDate);

		TextView tweetView = (TextView) convertView
				.findViewById(R.id.textViewTweet);

		monthDayYearformatter.format(schTweetModel.getTweettime());

		Calendar calendar = monthDayYearformatter.getCalendar();

		userName.setText("@" + schTweetModel.getUserDatas().getUsername());

		txtTime.setText(calendar.getTime().getHours() + ":"
				+ calendar.getTime().getMinutes());

		String yearstr = "" + calendar.getTime().getYear();

		yearstr = yearstr.substring(yearstr.length() - 2, yearstr.length());

		txtDate.setText(calendar.getTime().getMonth() + "-"
				+ calendar.getTime().getDay() + "-" + yearstr);

		tweetView.setText(getItem(position).getTweet());

		return convertView;

	}

	protected void openRemoveTweetDialog(final SchTweetModel schTweetModel) {

		final Dialog dialog = new Dialog(context);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.unlock_dialog);

		TextView user = (TextView) dialog.findViewById(R.id.myUserName);

		TextView textTweet = (TextView) dialog.findViewById(R.id.tweetView);

		Button buttonRemove = (Button) dialog.findViewById(R.id.unlockButton);

		textTweet.setText(schTweetModel.getTweet());

		user.setText("@"+schTweetModel.getUserDatas().getUsername());

		buttonRemove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				TboardproLocalData localData = new TboardproLocalData(context);

				localData.deleteThisTweet(schTweetModel.getTweetId());
				
				MainSingleTon.schedulecount--;
				
				MainActivity.isNeedToRefreshDrawer = true;
				
				// **************************************
				
				alarmManagers = (AlarmManager) context
						.getSystemService(context.ALARM_SERVICE);

				Intent myIntent = new Intent(context,
						TweetSchedullerReceiver.class);

				myIntent.putExtra(Const.RES_CODE, schTweetModel.getTweetId());

				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, schTweetModel.getTweetId(), myIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				alarmManagers.set(AlarmManager.RTC_WAKEUP,
						schTweetModel.getTweettime(), pendingIntent);

				alarmManagers.cancel(pendingIntent);
				
				FragmentSchedule.isNeedToUpdateUI = true;

 				
				// **************************************

				dialog.dismiss();


			}
		});

		dialog.show();

	}

}
