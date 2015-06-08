package com.socioboard.t_board_pro.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.socioboard.tboardpro.R;

public class FragmentSettingsRight extends Fragment {

	View rootView;

	EditText editText;

	Button button;

	SharedPreferences prefs;

	String svedtext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_settings_right,
				container, false);

		prefs = getActivity().getSharedPreferences("twtboardpro",
				Context.MODE_PRIVATE);

		svedtext = prefs.getString("autodmtext", "");

		myprint("svedtext " + svedtext);

		button = (Button) rootView.findViewById(R.id.button1);

		editText = (EditText) rootView.findViewById(R.id.editText1);

		if (svedtext.isEmpty()) {

		} else {

			editText.append(svedtext);

		}

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				button.requestFocus();

				Editor editor = prefs.edit();

				svedtext = editText.getText().toString();

				editor.putString("autodmtext", svedtext);

				myprint("svedtext " + svedtext);

				editor.commit();

				myToastS("Saved !");
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