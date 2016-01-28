package com.socioboard.t_board_pro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.socioboard.t_board_pro.fragments.FragmentOverlappingFollowers;
import com.socioboard.t_board_pro.fragments.FragmentOverlappingFollowings;

public class OverlappingPageAdapter extends FragmentPagerAdapter {

	public OverlappingPageAdapter(FragmentManager fm) {
		
		super(fm);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int pos) {

		switch (pos) {

		case 0:
			return new FragmentOverlappingFollowings();

		case 1:
			return new FragmentOverlappingFollowers();

		default:
			return new FragmentOverlappingFollowings();

		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

	private final String[] TITLES = { "Overlapping Followings",
			"Overlapping Followers" };

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position].toUpperCase();
	}

}
