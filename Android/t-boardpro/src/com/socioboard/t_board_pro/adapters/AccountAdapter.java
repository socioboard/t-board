package com.socioboard.t_board_pro.adapters;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TwtboardproLocalData;
import com.socioboard.tboardpro.R;

 public class AccountAdapter extends BaseAdapter {

	private Context context;
	
	private ArrayList<ModelUserDatas> navDrawerItems;

	MainActivity mainActivity;

	public AccountAdapter(ArrayList<ModelUserDatas> navDrawerItems,
			MainActivity mainActivity) {
		this.context = mainActivity.getApplicationContext();
		this.navDrawerItems = navDrawerItems;
		this.mainActivity= mainActivity;
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.account_item, parent,
					false);
		}

		ImageView profilePic = (ImageView) convertView
				.findViewById(R.id.profile_pic);
		ImageView settingspic = (ImageView) convertView
				.findViewById(R.id.settings);

		TextView text = (TextView) convertView.findViewById(R.id.user_name);

		profilePic.setImageResource(R.drawable.account_image);

		settingspic.setImageResource(R.drawable.ic_settings);

		text.setText(navDrawerItems.get(position).getUsername());

		settingspic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("settingspic  " + navDrawerItems.get(position));

				accountSettings(navDrawerItems.get(position), position);
			}
		});

		return convertView;
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	void accountSettings(final ModelUserDatas userDatas, final int position) {

		final Dialog dialog;

		dialog = new Dialog(mainActivity);
		
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		dialog.setContentView(R.layout.account_dialog);

		dialog.setCancelable(true);

		ImageView imageView = (ImageView) dialog.findViewById(R.id.profile_pic);
		
		TextView textView = (TextView) dialog.findViewById(R.id.textViewAccount);

		// imageView.setImageBitmap(userDatas.);

		textView.setText(userDatas.getUsername());
		
		Button buttonRemove, buttonCancel;

		buttonRemove = (Button) dialog.findViewById(R.id.button1Remove);

		buttonCancel = (Button) dialog.findViewById(R.id.button2Cancel);

		buttonRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("buttonRemove");

				TwtboardproLocalData twiterManyLocalData = new TwtboardproLocalData(
						context);

				twiterManyLocalData.deleteThisUserData(userDatas.getUserid());

				navDrawerItems.remove(position);

				notifyDataSetChanged();

				dialog.dismiss();

			}
		});

		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("buttonCancel");

				dialog.dismiss();
			}
		});
		new Handler().post(new Runnable() {

			@Override
			public void run() {

				dialog.show();

			}
		});
	}

}
