package com.socioboard.t_board_pro.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton;

import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.Utils;
import com.socioboard.tboardpro.R;

public class SelectAccountAdapter extends BaseAdapter implements
		CompoundButton.OnCheckedChangeListener {

	private Context context;
	public SparseBooleanArray sparseBooleanArray;
	public int count = 0;
	private ArrayList<ModelUserDatas> navDrawerItems;

	public SelectAccountAdapter(ArrayList<ModelUserDatas> navDrawerItems,
			Context context, SparseBooleanArray sparseBooleanArray) {

		this.context = context;

		this.navDrawerItems = navDrawerItems;

		for (int i = 0; i < navDrawerItems.size(); ++i) {

			myprint("sparseBooleanArray = " + sparseBooleanArray.get(i));

		}

		this.sparseBooleanArray = sparseBooleanArray;

	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.usercheck, parent, false);
		}

		ImageView profilePic = (ImageView) convertView
				.findViewById(R.id.profile_pic);

		TextView text = (TextView) convertView.findViewById(R.id.user_name);

		String stringBtmp = navDrawerItems.get(position).getUserimage();

		Bitmap bitmap = null;

		if (stringBtmp != null) {

			bitmap = Utils.decodeBase64(stringBtmp);

			profilePic.setImageBitmap(bitmap);

		}

		text.setText(navDrawerItems.get(position).getUsername());

		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
		checkBox.setTag(position);
		checkBox.setChecked(sparseBooleanArray.get(position));
		checkBox.setOnCheckedChangeListener(SelectAccountAdapter.this);

		return convertView;
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		sparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);

		// myprint(" onCheckedChanged " + getItem((Integer)
		// buttonView.getTag()));

	}
}
