package com.socioboard.t_board_pro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.socioboard.t_board_pro.fragments.FragmentHashTagSearch;
import com.socioboard.t_board_pro.fragments.FragmentSearch;

public class SearchPageAdapter extends FragmentPagerAdapter {

	public SearchPageAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int pos) {

		switch (pos) {

		case 0:
			return new FragmentSearch();

		case 1:
			return new FragmentHashTagSearch();

		default:
			return new FragmentSearch();

		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

	private final String[] TITLES = { "User search", "Trends Search" };

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position].toUpperCase();
	}

}
