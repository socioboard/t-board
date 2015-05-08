package com.socioboard.t_board_pro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.socioboard.tboardpro.R;


public class FragmentSettingsLeft extends Fragment {
	
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
 		rootView = inflater.inflate(R.layout.fragment_settings_left, container, false);

		return rootView;
	}

	void myToastS(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
	}

	void myToastL(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}
}