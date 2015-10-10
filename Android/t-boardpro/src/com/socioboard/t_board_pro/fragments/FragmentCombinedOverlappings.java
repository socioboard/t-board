package com.socioboard.t_board_pro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socioboard.t_board_pro.adapters.OverlappingPageAdapter;
import com.socioboard.tboardpro.R;
import com.viewpagerindicator.TabPageIndicator;

public class FragmentCombinedOverlappings extends Fragment {

	View rootView;
	OverlappingPageAdapter overlappingPageAdapter;
	ViewPager viewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_combined_search,
				container, false);

		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

		viewPager.setAdapter(new OverlappingPageAdapter(
				getChildFragmentManager()));

		TabPageIndicator indicator = (TabPageIndicator) rootView
				.findViewById(R.id.indicator);

		indicator.setViewPager(viewPager);

		return rootView;
	}

}
