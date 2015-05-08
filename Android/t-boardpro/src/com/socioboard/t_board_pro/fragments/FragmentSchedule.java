package com.socioboard.t_board_pro.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.socioboard.t_board_pro.util.TweetScheduller;
import com.socioboard.tboardpro.R;

public class FragmentSchedule extends Fragment {

	View rootView;
	AlarmManager alarmManagers;
	TimePicker timePicker;;
	DatePicker datePicker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// **************************************
 		
		Intent myIntent = new Intent(getActivity(),
				TweetScheduller.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
				myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManagers = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);

		alarmManagers.cancel(pendingIntent);

		// **************************************
		
		rootView = inflater.inflate(R.layout.fragment_scheduller, container, false);

		return rootView;
	}
}
