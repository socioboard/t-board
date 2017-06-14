package com.socioboard.t_board_pro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.socioboard.t_board_pro.fragments.FragmentTimeLine;
import com.socioboard.t_board_pro.fragments.FragmentTimeLineMentions;
import com.socioboard.t_board_pro.fragments.FragmentTimeLineMine;

public class FeedsPagerAdapter extends FragmentPagerAdapter {

	public FeedsPagerAdapter(FragmentManager fm) {

		super(fm);

	}

	@Override
	public Fragment getItem(int pos) {

		switch (pos) {

		case 0:
			return new FragmentTimeLine();

		case 1:
			return new FragmentTimeLineMentions();

		case 2:
			return new FragmentTimeLineMine();

		default:
			return new FragmentTimeLine();

		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	private final String[] TITLES = { "Home", "Mentions", "Me" };

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position].toUpperCase();
	}

}
