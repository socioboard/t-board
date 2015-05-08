package com.socioboard.t_board_pro.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.tboardpro.R;

public class FragmentSearch extends Fragment {

	View rootView;
	SearchView searchView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_search, container, false);
		searchView = (SearchView) rootView.findViewById(R.id.searchView1);
		searchView.setIconified(false);
		searchView.setQueryHint("user screen_name");
		searchView.setSaveEnabled(true);
		searchView.setSaveEnabled(true);
		int id = searchView.getContext().getResources()
				.getIdentifier("android:id/search_src_text", null, null);
		TextView textView = (TextView) searchView.findViewById(id);
		textView.setTextColor(Color.LTGRAY);
		


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
