package com.socioboard.t_board_pro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socioboard.t_board_pro.adapters.SearchPageAdapter;
import com.socioboard.tboardpro.R;
import com.viewpagerindicator.TabPageIndicator;

public class FragmentCombinedSearch extends Fragment {

	SearchPageAdapter searchPageAdapter;
	
	ViewPager viewPager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		System.out.println("***************************onCreateView");

		View rootView = inflater.inflate(R.layout.fragment_combined_search,
				container, false);

 		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

		viewPager.setAdapter(new SearchPageAdapter(getChildFragmentManager()));

		TabPageIndicator indicator = (TabPageIndicator) rootView.findViewById(R.id.indicator);

		indicator.setViewPager(viewPager);

		return rootView;
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//onDestroy();
		System.out.println("***************************onStop");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		System.out.println("***************************onDestroyView");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("***************************onDestroy");

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("***************************onResume");
	}

}
