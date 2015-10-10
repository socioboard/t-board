package com.socioboard.t_board_pro.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.SchedulleComposeActivity;
import com.socioboard.t_board_pro.adapters.SchTweetsAdapter;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.SchTweetModel;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

public class FragmentSchedule extends Fragment {

	View rootView;

	SchTweetsAdapter schTweetsAdapter;

 	ArrayList<SchTweetModel> schTweetModels = new ArrayList<SchTweetModel>();

	TboardproLocalData tboardproLocalData;

	ListView listview;

	ImageView imdNewSchdulle, imageViewAddUsers;

	TextView txtCount;

	Timer timer = new Timer();

	public static boolean isNeedToUpdateUI = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		tboardproLocalData = new TboardproLocalData(getActivity());

		rootView = inflater.inflate(R.layout.tweetschedulle_table, container,
				false);

		listview = (ListView) rootView.findViewById(R.id.listView1);

		txtCount = (TextView) rootView.findViewById(R.id.textView2);

		imdNewSchdulle = (ImageView) rootView
				.findViewById(R.id.imageViewNewTWeet);

		schTweetModels.clear();

		getAllTweets();

		schTweetsAdapter = new SchTweetsAdapter(getActivity(), schTweetModels);

		txtCount.setText("Scheduled tweets : " + schTweetModels.size());

		listview.setAdapter(schTweetsAdapter);

		imdNewSchdulle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(),
						SchedulleComposeActivity.class);

				getActivity().startActivity(intent);

			}
		});

		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				if (isNeedToUpdateUI) {

					isNeedToUpdateUI = false;

					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {

							int getCont = listview.getCount();

							getAllTweets();

							schTweetsAdapter = new SchTweetsAdapter(
									getActivity(), schTweetModels);

							txtCount.setText("Scheduled tweets : "
									+ schTweetModels.size());

							listview.setAdapter(schTweetsAdapter);

							listview.setScrollY(getCont);
						}
					});
				}
			}

		}, 1000, 500);

		return rootView;
	}

	@Override
	public void onResume() {

		super.onResume();

		getAllTweets();

		txtCount.setText("Scheduled tweets : " + schTweetModels.size());

		listview.setAdapter(schTweetsAdapter);

		MainActivity.isNeedToRefreshDrawer = true;

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		timer.cancel();

	}

	void getAllTweets() {
		
		ArrayList<SchTweetModel> tmpSchTweetModels = new ArrayList<SchTweetModel>();

		tmpSchTweetModels = tboardproLocalData.getAllSchedulledTweet();
		
		schTweetModels.clear();
		
		for (int i = 0; i < tmpSchTweetModels.size(); ++i) {

			ModelUserDatas userDatas = tboardproLocalData.getUserData(tmpSchTweetModels.get(i).getUserID());

			if (userDatas != null) {

				tmpSchTweetModels.get(i).setUserDatas(userDatas);

				SchTweetModel schTweetModel = tmpSchTweetModels.get(i);

				System.out.println("schTweetModel " + schTweetModel);

				if (schTweetModel.getTweet().startsWith(
						"in_reply_to_status_id=@@")) {

					String original = schTweetModel.getTweet(), finals, tmp;

					tmp = original.split("in_reply_to_status_id=@@")[1];

					System.out.println("tmp String " + tmp);

					int last_index = tmp.indexOf("@@");

					finals = tmp.substring(last_index+2);

					System.out.println("Final String " + finals);

					schTweetModel.setTweet(finals);

					schTweetModel.setTweetType(1);

				} else if (schTweetModel.getTweet().startsWith(
						"retweet_to_status_id=@@")) {

					String original = schTweetModel.getTweet(), finals, tmp;

					tmp = original.split("retweet_to_status_id=@@")[1];

					System.out.println("tmp String " + tmp);

					int last_index = tmp.indexOf("@@");

					finals = tmp.substring(last_index+2);

					System.out.println("Final String " + finals);

					schTweetModel.setTweet(finals);

					schTweetModel.setTweetType(2);

				} else {

					schTweetModel.setTweetType(0);

				}

				schTweetModels.add(schTweetModel);

			} else {

				Collections.reverse(schTweetModels);
				
				tboardproLocalData.deleteThisTweet(tmpSchTweetModels.get(i)
						.getTweetId());

			}

		}

	}

}
