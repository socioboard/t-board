package com.socioboard.t_board_pro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socioboard.t_board_pro.adapters.OverlappingPageAdapter;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.tboardpro.R;
import com.viewpagerindicator.TabPageIndicator;

public class FragmentCombinedOverlappings extends Fragment {

	View rootView;
	OverlappingPageAdapter overlappingPageAdapter;
	ViewPager viewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MainSingleTon.mixpanelAPI.track("Fragment CombinedOverlappings oncreate called");

		rootView = inflater.inflate(R.layout.fragment_combined_search,
				container, false);

		//LoadAd();

		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

		viewPager.setAdapter(new OverlappingPageAdapter(
				getChildFragmentManager()));

		TabPageIndicator indicator = (TabPageIndicator) rootView
				.findViewById(R.id.indicator);

		indicator.setViewPager(viewPager);

		return rootView;
	}

//	void LoadAd()
//	{
//		MobileAds.initialize(getActivity(), getString(R.string.adMob_app_id));
//		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
//		AdRequest adRequest = new AdRequest.Builder().build();
//		mAdView.loadAd(adRequest);
//
//	}

}
