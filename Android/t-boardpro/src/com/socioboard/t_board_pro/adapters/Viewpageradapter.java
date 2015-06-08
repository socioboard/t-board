package com.socioboard.t_board_pro.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socioboard.t_board_pro.lazylist.ImageLoader;
import com.socioboard.t_board_pro.util.IntroViewPagerModel;
import com.socioboard.tboardpro.R;

public class Viewpageradapter extends PagerAdapter {
	
	public ImageLoader imageLoader;
	Context mContext;
	LayoutInflater mLayoutInflater;
	boolean showDeatils = true;
	IntroViewPagerModel model;
	ArrayList<IntroViewPagerModel> imageList;

	public Viewpageradapter(Context context,
			ArrayList<IntroViewPagerModel> imageList) {
		imageLoader = new ImageLoader(context);
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageList = imageList;

	}

	@Override
	public int getItemPosition(Object object) {
		
		return POSITION_NONE;
	}
	
	@Override
	public int getCount() {
		return imageList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		model = imageList.get(position);
		
		View itemView = mLayoutInflater.inflate(R.layout.intro_viewpager_item, container,
				false);

		ImageView intro_imageView = (ImageView) itemView
				.findViewById(R.id.introImage);

		TextView itro_text=(TextView) itemView.findViewById(R.id.introText);
		
		intro_imageView.setImageResource(model.getDrawable());
		
		itro_text.setText(model.getIntro_text());
		
		container.addView(itemView);
		
		return itemView;
	}


	

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((RelativeLayout) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}


	
}
