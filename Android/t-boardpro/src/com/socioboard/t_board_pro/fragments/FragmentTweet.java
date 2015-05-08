package com.socioboard.t_board_pro.fragments;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestTweet;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.tboardpro.R;

public class FragmentTweet extends Fragment {

	View rootView;
	Button tweetButton;
	EditText edttext;
	String tweetString;
	Activity aActivity;
	ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_tweetcompose, container,
				false);

		progressDialog = new ProgressDialog(FragmentTweet.this.getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);

		aActivity = getActivity();

		tweetButton = (Button) rootView.findViewById(R.id.button1);
		edttext = (EditText) rootView.findViewById(R.id.editText1);
		tweetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				tweetString = edttext.getText().toString();
				
				progressDialog.setMessage("Posting..");

				showProgress();

				TwitterPostRequestTweet twitterPostRequestTweet = new TwitterPostRequestTweet(
						MainSingleTon.currentUserModel,
						new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {

							}

							@Override
							public void onSuccess(String jsonResult) {
								
								aActivity.runOnUiThread(new Runnable() {

									@Override
									public void run() {

										edttext.setText("");

									}
								});
								myprint("jsonResult " + jsonResult);
								myToastS("Tweet successfull!");
								hideProgress();
							}

							@Override
							public void onFailure(Exception e) {
								
								aActivity.runOnUiThread(new Runnable() {

									@Override
									public void run() {

										edttext.setText("");

									}
								});
								myprint("onFailure " + e);
								myToastS("sending failed!");
								hideProgress();

							}
						});

				twitterPostRequestTweet.executeThisRequest(tweetString);
		
			}
		});

		return rootView;
	}

	void myToastS(final String toastMsg) {

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT)
						.show();

			}
		});
	}

	void myToastL(final String toastMsg) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG)
						.show();

			}
		});
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	void showProgress() {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				progressDialog.show();

			}
		});
	}

	void hideProgress() {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				progressDialog.cancel();

			}
		});
	}
}
