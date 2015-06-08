package com.socioboard.t_board_pro.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socioboard.t_board_pro.ui.Items;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.Utils;
import com.socioboard.tboardpro.R;

/**
 * Created by d4ddy-lild4rk on 11/8/14.
 */
public class DrawerAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Items> navDrawerItems;

	public DrawerAdapter(Context context, ArrayList<Items> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
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
			convertView = mInflater.inflate(R.layout.drawer_list_item, parent,
					false);
		}

		ProgressBar progressBar = (ProgressBar) convertView
				.findViewById(R.id.progressBar1);

		ImageView image = (ImageView) convertView.findViewById(R.id.item_icon);

		TextView text = (TextView) convertView.findViewById(R.id.item_text);

		TextView item_count = (TextView) convertView
				.findViewById(R.id.item_count);

		if (position == 0) {

			String stringBtmp = MainSingleTon.currentUserModel.getUserimage();

			Bitmap bitmap = null;

			if (stringBtmp != null) {

				bitmap = Utils.decodeBase64(stringBtmp);

				image.setImageBitmap(bitmap);

			}

		} else {

			image.setImageResource(navDrawerItems.get(position).getIcon());

		}

		switch (position) {

		case 0:
			progressBar.setVisibility(View.INVISIBLE);
			item_count.setVisibility(View.INVISIBLE);
			item_count.setText("");
			break;

		case 1:

			if (MainSingleTon.tweetsCount == -1) {

				progressBar.setVisibility(View.VISIBLE);
				item_count.setVisibility(View.INVISIBLE);

			} else {

				progressBar.setVisibility(View.INVISIBLE);
				item_count.setVisibility(View.VISIBLE);
				item_count.setText("" + MainSingleTon.tweetsCount);

			}

			break;

		case 2:
			progressBar.setVisibility(View.INVISIBLE);
			item_count.setVisibility(View.INVISIBLE);
			break;

		case 3:
			if (MainSingleTon.followingCount == -1) {
				progressBar.setVisibility(View.VISIBLE);
				item_count.setVisibility(View.INVISIBLE);
			} else {
				progressBar.setVisibility(View.INVISIBLE);
				item_count.setVisibility(View.VISIBLE);
				item_count.setText("" + MainSingleTon.followingCount);
			}
			break;

		case 4:
			if (MainSingleTon.myfollowersCount == -1) {
				progressBar.setVisibility(View.VISIBLE);
				item_count.setVisibility(View.INVISIBLE);
			} else {
				progressBar.setVisibility(View.INVISIBLE);
				item_count.setVisibility(View.VISIBLE);
				item_count.setText("" + MainSingleTon.myfollowersCount);
			}
			break;

		case 5:
			progressBar.setVisibility(View.INVISIBLE);
			item_count.setVisibility(View.INVISIBLE);
			item_count.setText("");
			break;

		case 6:
			if (MainSingleTon.favoritesCount == -1) {
				progressBar.setVisibility(View.VISIBLE);
				item_count.setVisibility(View.INVISIBLE);
			} else {
				progressBar.setVisibility(View.INVISIBLE);
				item_count.setVisibility(View.VISIBLE);
				item_count.setText("" + MainSingleTon.favoritesCount);
			}
			break;

		case 7:
			progressBar.setVisibility(View.INVISIBLE);
			item_count.setVisibility(View.INVISIBLE);
			item_count.setText("");
			break;

		case 8:
			progressBar.setVisibility(View.INVISIBLE);
			item_count.setVisibility(View.INVISIBLE);
			item_count.setText("");
			break;

		case 9:
			if (MainSingleTon.fansCount == -1) {
				progressBar.setVisibility(View.VISIBLE);
				item_count.setVisibility(View.INVISIBLE);
			} else {
				progressBar.setVisibility(View.INVISIBLE);
				item_count.setVisibility(View.VISIBLE);
				item_count.setText("" + MainSingleTon.fansCount);
			}
			break;

		case 10:
			if (MainSingleTon.mutualfansCount == -1) {
				progressBar.setVisibility(View.VISIBLE);
				item_count.setVisibility(View.INVISIBLE);
			} else {
				progressBar.setVisibility(View.INVISIBLE);
				item_count.setVisibility(View.VISIBLE);
				item_count.setText("" + MainSingleTon.mutualfansCount);
			}
			break;

		case 11:
			if (MainSingleTon.NOnfollowersCount == -1) {
				progressBar.setVisibility(View.VISIBLE);
				item_count.setVisibility(View.INVISIBLE);
			} else {
				progressBar.setVisibility(View.INVISIBLE);
				item_count.setVisibility(View.VISIBLE);
				item_count.setText("" + MainSingleTon.NOnfollowersCount);
			}
			break;

		case 12:
			progressBar.setVisibility(View.INVISIBLE);
			item_count.setVisibility(View.INVISIBLE);
			item_count.setText("");
			break;

		case 13:
			progressBar.setVisibility(View.INVISIBLE);
			item_count.setVisibility(View.INVISIBLE);
			item_count.setText("");
			break;

		case 14:

			if (MainSingleTon.schedulecount == -1) {
				progressBar.setVisibility(View.VISIBLE);
				item_count.setVisibility(View.INVISIBLE);
			} else {
				progressBar.setVisibility(View.INVISIBLE);
				item_count.setVisibility(View.VISIBLE);
				item_count.setText("" + MainSingleTon.schedulecount);
			}

			break;

		default:
		}

		text.setText(navDrawerItems.get(position).getTitle());

		return convertView;
	}
}
