package com.socioboard.t_board_pro.fragments;

import java.util.ArrayList;
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
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.SchTweetModel;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

public class FragmentSchedule extends Fragment {

	View rootView;

	SchTweetsAdapter schTweetsAdapter;

	ArrayList<SchTweetModel> tmpSchTweetModels = new ArrayList<SchTweetModel>();

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

		tmpSchTweetModels = tboardproLocalData.getAllSchedulledTweet();

		schTweetModels.clear();

		for (int i = 0; i < tmpSchTweetModels.size(); ++i) {

			ModelUserDatas userDatas = tboardproLocalData
					.getUserData(tmpSchTweetModels.get(i).getUserID());

			if (userDatas != null) {

				tmpSchTweetModels.get(i).setUserDatas(userDatas);

				SchTweetModel schTweetModel = tmpSchTweetModels.get(i);

				System.out.println("schTweetModel " + schTweetModel);

				schTweetModels.add(tmpSchTweetModels.get(i));

			} else {

				tboardproLocalData.deleteThisTweet(tmpSchTweetModels.get(i).getTweetId());

			}
			
 		}

		MainSingleTon.schedulecount = schTweetModels.size();

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

							schTweetModels = tboardproLocalData
									.getAllSchedulledTweet();

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

		tmpSchTweetModels = tboardproLocalData.getAllSchedulledTweet();

		schTweetModels.clear();

		for (int i = 0; i < tmpSchTweetModels.size(); ++i) {

			ModelUserDatas userDatas = tboardproLocalData
					.getUserData(tmpSchTweetModels.get(i).getUserID());

			tmpSchTweetModels.get(i).setUserDatas(userDatas);

			SchTweetModel schTweetModel = tmpSchTweetModels.get(i);

			System.out.println("schTweetModel " + schTweetModel);

		}

		MainSingleTon.schedulecount = tmpSchTweetModels.size();

		schTweetsAdapter = new SchTweetsAdapter(getActivity(),
				tmpSchTweetModels);

		txtCount.setText("Scheduled tweets : " + tmpSchTweetModels.size());

		listview.setAdapter(schTweetsAdapter);

		MainActivity.isNeedToRefreshDrawer = true;

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		timer.cancel();

	}
}
