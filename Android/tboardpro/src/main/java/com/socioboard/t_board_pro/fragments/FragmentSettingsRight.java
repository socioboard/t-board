package com.socioboard.t_board_pro.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.SplashActivity;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

public class FragmentSettingsRight extends Fragment {

	View rootView;

	EditText editText;

	Button button, button2Remove;

	SharedPreferences prefs;

	Switch switch1;

	String svedtext;

	Editor editor;

	TextView textView7Key, textView7Secret, textViewCallBackUrl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		MainSingleTon.mixpanelAPI.track("Fragment SettingRight oncreate called");

		rootView = inflater.inflate(R.layout.fragment_settings_right, container, false);

		prefs = getActivity().getSharedPreferences("twtboardpro", Context.MODE_PRIVATE);

		editor = getActivity().getSharedPreferences("twtboardpro", Context.MODE_PRIVATE).edit();

		final TboardproLocalData localData = new TboardproLocalData(getActivity());

		svedtext = prefs.getString("autodmtext", "");

		myprint("svedtext " + svedtext);

		myprint("MainSingleTon.autodm " + MainSingleTon.autodm);

		button = (Button) rootView.findViewById(R.id.button1);

		button2Remove = (Button) rootView.findViewById(R.id.button2Remove);

		textView7Key = (TextView) rootView.findViewById(R.id.textView7Key);
		textView7Secret = (TextView) rootView.findViewById(R.id.textView7Secret);
		textViewCallBackUrl = (TextView) rootView.findViewById(R.id.textViewCallBackUrl);

		textView7Key.setText(MainSingleTon.TWITTER_KEY);
		textView7Secret.setText(MainSingleTon.TWITTER_SECRET);
		textViewCallBackUrl.setText(MainSingleTon.oauth_callbackURL);
		
		switch1 = (Switch) rootView.findViewById(R.id.switch1);

		switch1.setChecked(MainSingleTon.autodm);

		switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				editor = prefs.edit();

				if (MainSingleTon.autodm) {

					MainSingleTon.autodm = false;

				} else {

					MainSingleTon.autodm = true;

				}

				editor.putBoolean("autodm", MainSingleTon.autodm);

				myprint("MainSingleTon.autodm " + MainSingleTon.autodm);

				editor.commit();

			}

		});

		editText = (EditText) rootView.findViewById(R.id.editText1);

		if (svedtext.isEmpty()) {

		} else {

			editText.append(svedtext);

		}

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				button.requestFocus();

				editor = prefs.edit();

				svedtext = editText.getText().toString();

				editor.putString("autodmtext", svedtext);

				myprint("svedtext " + svedtext);

				editor.commit();

				myToastS("Saved !");

			}
		});

		button2Remove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new android.app.AlertDialog.Builder(getActivity()).setTitle("Remove Credentials?")
						.setMessage(
								"After removing the \"Twitter API Credentials\" you will be logged out from the accounts which you added. You can logIn again after setting new Credentials.")
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						localData.deleteAllRows();

						editor.putBoolean(MainSingleTon.isTwitterKeyAssigned, false);

						editor.commit();

						getActivity().startActivity(new Intent(getActivity(), SplashActivity.class));

						getActivity().finish();

					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				}).setIcon(R.drawable.ic_dialog_alert_holo_light).show();
			}
		});

		return rootView;
	}

	void myToastS(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
	}

	void myToastL(final String toastMsg) {

		Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}
}