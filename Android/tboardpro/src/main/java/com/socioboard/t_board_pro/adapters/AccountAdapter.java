package com.socioboard.t_board_pro.adapters;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.Utils;
import com.socioboard.tboardpro.R;

public class AccountAdapter extends BaseAdapter {

	private Context context;

	private ArrayList<ModelUserDatas> navDrawerItems;

	MainActivity mainActivity;

	public AccountAdapter(ArrayList<ModelUserDatas> navDrawerItems,
			MainActivity mainActivity) {
		this.context = mainActivity.getApplicationContext();
		this.navDrawerItems = navDrawerItems;
		this.mainActivity = mainActivity;
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
			
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = mInflater.inflate(R.layout.account_item, parent,false);
			
		}

		ImageView profilePic = (ImageView) convertView.findViewById(R.id.profile_pic);
		
		ImageView settingspic = (ImageView) convertView.findViewById(R.id.settings);

		TextView text = (TextView) convertView.findViewById(R.id.user_name);

		String stringBtmp = navDrawerItems.get(position).getUserimage();

		Bitmap bitmap = null;

		if (stringBtmp != null) {

			bitmap = Utils.decodeBase64(stringBtmp);

			profilePic.setImageBitmap(bitmap);

		}

		settingspic.setImageResource(R.drawable.delete_account_icon);

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

		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		ImageView imageView = (ImageView) dialog.findViewById(R.id.profile_pic);

		TextView textView = (TextView) dialog
				.findViewById(R.id.textViewAccount);

		if (userDatas.getUserimage() != null) {

			Bitmap bitmap = Utils.decodeBase64(userDatas.getUserimage());

			if (bitmap == null) {
			} else {
				imageView.setImageBitmap(bitmap);
			}

		}

		textView.setText(userDatas.getUsername());

		Button buttonRemove, buttonCancel;

		buttonRemove = (Button) dialog.findViewById(R.id.button1Remove);

		buttonCancel = (Button) dialog.findViewById(R.id.button2Cancel);

		buttonRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myprint("buttonRemove");

				TboardproLocalData twiterManyLocalData = new TboardproLocalData(
						context);

				twiterManyLocalData.deleteThisUserData(userDatas.getUserid());
 
 				if (twiterManyLocalData.getAllIds().size()==1) {
					
					mainActivity.imageViewSettings.setVisibility(View.VISIBLE);
				}
				
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
