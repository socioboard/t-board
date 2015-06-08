package com.socioboard.t_board_pro.dialog;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.adapters.SelectAccountAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestTweet;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

public class ShowTweetComposeDialog {

	Dialog dialog;

	String tweetString;

	Context context;

	int count = 0;

	TextView textViewCount;

	ArrayList<ModelUserDatas> navDrawerItems;

	TboardproLocalData tbDAta;

	ImageView imageViewAddUsers;

	int countProcess;

	Button replyButton;

	ProgressDialog progressDialog;

	public SparseBooleanArray sparseBooleanArray;

	EditText edttext;

	public ShowTweetComposeDialog(Context activity, String autoText) {

		this.context = activity;

		progressDialog = new ProgressDialog(activity);

		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		progressDialog.setIndeterminate(true);

		progressDialog.setCancelable(false);

		dialog = new Dialog(context);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.fragment_tweetcomposedialog);

		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

		Window window = dialog.getWindow();

		lp.copyFrom(window.getAttributes());

		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		window.setAttributes(lp);

		dialog.setCancelable(true);

		tbDAta = new TboardproLocalData(context);

		navDrawerItems = tbDAta.getAllUsersDataArlist();

		sparseBooleanArray = new SparseBooleanArray(navDrawerItems.size());

		for (int i = 0; i < navDrawerItems.size(); ++i) {

			sparseBooleanArray.put(i, false);

		}
		edttext = (EditText) dialog.findViewById(R.id.editText1);

		edttext.setText("");

		if (autoText.length() != 0) {
			edttext.append(autoText + " ");
		}

		textViewCount = (TextView) dialog.findViewById(R.id.textView1Counted);

		textViewCount.setText("Selected : 0");

		imageViewAddUsers = (ImageView) dialog
				.findViewById(R.id.imageViewAddUsers);

		replyButton = (Button) dialog.findViewById(R.id.button1);

		imageViewAddUsers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openSelectDialog();

			}
		});

		replyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				performTweet();
			}
		});

	}

	public void showThis() {

		dialog.show();

	}

	protected void openSelectDialog() {

		final Dialog dialog;

		dialog = new Dialog(context);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.dialog_user_select);

		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

		Window window = dialog.getWindow();

		lp.copyFrom(window.getAttributes());

		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		window.setAttributes(lp);

		dialog.setCancelable(true);

		ListView listView = (ListView) dialog
				.findViewById(R.id.listView1select);

		final SelectAccountAdapter selectAccountAdapter;

		selectAccountAdapter = new SelectAccountAdapter(navDrawerItems,
				context, sparseBooleanArray);

		listView.setAdapter(selectAccountAdapter);

		Button buttonDone;

		buttonDone = (Button) dialog.findViewById(R.id.button1);

		buttonDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				sparseBooleanArray = selectAccountAdapter.sparseBooleanArray;

				count = selectAccountAdapter.count;

				myprint("buttonCancel");

				dialog.cancel();

			}
		});

		new Handler().post(new Runnable() {

			@Override
			public void run() {

				dialog.show();

			}
		});

		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

				count = 0;

				for (int i = 0; i < navDrawerItems.size(); ++i) {

					if (sparseBooleanArray.get(i)) {
						++count;
					}

				}
				textViewCount.setText("" + count);
			}
		});
	}

	public void myprint(Object msg) {

		System.out.println(msg.toString());

	}

	void performTweet() {

		tweetString = edttext.getText().toString();

		if (new String(tweetString).trim().length() == 0) {

			myToastS("Text cannot be empty");
			
			return;
		}

		if (count == 0) {

			myToastS("Select User first!");
			return;
		}

		countProcess = 0;

		progressDialog.setMessage("Processing for .." + count);

		for (int i = 0; i < navDrawerItems.size(); i++) {

			if (sparseBooleanArray.get(i)) {

				showProgress();

				TwitterPostRequestTweet twitterPostRequestTweet = new TwitterPostRequestTweet(
						navDrawerItems.get(i), new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {

							}

							@Override
							public void onSuccess(String jsonResult) {

								++countProcess;

								myprint("jsonResult " + jsonResult);

								// myToastS("Tweet successfull!");
								++MainSingleTon.tweetsCount;
								MainActivity.isNeedToRefreshDrawer = true;
								hideProgress();
							}

							@Override
							public void onFailure(Exception e) {

								edttext.setText("");

								myprint("onFailure " + e);

								// myToastS("sending failed!");

								hideProgress();
							}
						});

				twitterPostRequestTweet.executeThisRequest(tweetString);

			}

		}

	}

	void myToastS(final String toastMsg) {

		Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();

	}

	void myToastL(final String toastMsg) {

		Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show();

	}

	void showProgress() {

		progressDialog.show();

	}

	void hideProgress() {

		progressDialog.cancel();

		dialog.dismiss();
	}

}
