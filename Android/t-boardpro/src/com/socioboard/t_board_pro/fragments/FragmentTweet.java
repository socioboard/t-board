package com.socioboard.t_board_pro.fragments;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.adapters.SelectAccountAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterMediaUpload;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestTweet;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.FileUtils;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

public class FragmentTweet extends Fragment {

	View rootView;

	Button tweetButton;

	EditText edttext;

	String tweetString;

	Activity aActivity;

	ProgressDialog progressDialog;

	TboardproLocalData tbDAta;

	ArrayList<ModelUserDatas> navDrawerItems;

	TextView textViewCount;

	CheckBox chkBox;
	
	Uri uri;
	
	int count = 0;

	private SparseBooleanArray sparseBooleanArray;

	ImageView imageViewAddUsers, imageView1Choose, imageViewAttached;

	String filpathUtils;

	boolean isImageselected = false;

  	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_tweetcompose, container,false);

		chkBox = (CheckBox) rootView.findViewById(R.id.checkBox1);

		textViewCount = (TextView) rootView.findViewById(R.id.textView1Counted);

		imageViewAddUsers = (ImageView) rootView.findViewById(R.id.imageViewAddUsers);
		
		imageView1Choose = (ImageView) rootView.findViewById(R.id.imageView1Choose);
		
		imageViewAttached = (ImageView) rootView.findViewById(R.id.imageViewAttached);

		imageViewAddUsers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openSelectDialog();

			}
		});

		imageView1Choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();

				intent.setType("image/*");

				intent.setAction(Intent.ACTION_GET_CONTENT);

				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"), 10);

			}
		});

		textViewCount.setText("Selected : " + 0);

		progressDialog = new ProgressDialog(FragmentTweet.this.getActivity());

		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		progressDialog.setIndeterminate(true);

		progressDialog.setCancelable(false);

		tbDAta = new TboardproLocalData(getActivity());

		navDrawerItems = tbDAta.getAllUsersDataArlist();

		sparseBooleanArray = new SparseBooleanArray(navDrawerItems.size());

		for (int i = 0; i < navDrawerItems.size(); ++i) {

			sparseBooleanArray.put(i, false);

		}

		aActivity = getActivity();

		tweetButton = (Button) rootView.findViewById(R.id.button1);

		edttext = (EditText) rootView.findViewById(R.id.editText1);

		tweetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isImageselected) {

					performTweetMedia();

				} else {

					performTweet();

				}

			}
		});

		return rootView;
		
	}

	protected void openSelectDialog() {

		final Dialog dialog;

		dialog = new Dialog(getActivity());

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
				getActivity(), sparseBooleanArray);

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
				
				Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();

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

		int countProcess = 0;

		for (int i = 0; i < navDrawerItems.size(); i++) {

			if (sparseBooleanArray.get(i)) {

				++countProcess;

				progressDialog.setMessage(countProcess + " out of  .." + count
						+ " tweeted");

				showProgress();

				TwitterPostRequestTweet twitterPostRequestTweet = new TwitterPostRequestTweet(
						navDrawerItems.get(i), new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {

							}

							@Override
							public void onSuccess(String jsonResult) {

								++MainSingleTon.tweetsCount;
								MainActivity.isNeedToRefreshDrawer = true;

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

		}

	}

	void performTweetMedia() {

		tweetString = edttext.getText().toString();

		if (tweetString.length() > 117) {

			myToastL("Text size should be max 117 chars in Media attach !");
		
			myToastS("please reduce it!");
			
			return;
		}

		if (count == 0) {

			myToastS("Select User first!");
			
			return;
		}
		
		TwitterMediaUpload twitterMediaUpload = new TwitterMediaUpload(MainSingleTon.currentUserModel, new TwitterRequestCallBack() {
			
			@Override
			public void onSuccess(JSONObject jsonObject) {

				
			}
			
			@Override
			public void onSuccess(String jsonResult) {
				
				myprint(jsonResult);
				
				try {
					
					JSONObject jsonObject = new JSONObject(jsonResult);
					
 				} catch (JSONException e1) {
 					
 					e1.printStackTrace();
 					
				}
					
			}
			
			@Override
			public void onFailure(Exception e) {
				
			}

		});
		
		File selectedFile = new File(filpathUtils);
 		
 		List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();
        
 		int size = (int) selectedFile.length();
 		
 		byte[] bytes = new byte[size];
 		
 		try {
 			
 		    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(selectedFile));
 		    buf.read(bytes, 0, bytes.length);
 		    buf.close();
 		    
 		} catch (FileNotFoundException e) {
 			
  		    e.printStackTrace();
  		    
 		} catch (IOException e) {
 			
  		    e.printStackTrace();
  		    
 		}
 		
 		peramPairs.add(new BasicNameValuePair(Const.media, ""+bytes));
		
		peramPairs.add(new BasicNameValuePair(Const.include_entities, "false"));
        
		twitterMediaUpload.executeThisRequest(MainSingleTon.uploadMedia, peramPairs);
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		  uri = data.getData();

		if (requestCode == 10) {
			
			Bitmap bitmap;

			filpathUtils = FileUtils.getPath(getActivity(), uri);

			try {

				bitmap = MediaStore.Images.Media.getBitmap(getActivity()
						.getContentResolver(), uri);

				imageViewAttached.setImageBitmap(bitmap);

				isImageselected = true;

			} catch (FileNotFoundException e) {
				myToastS("Error while Choosing Image");
				e.printStackTrace();
			} catch (IOException e) {
				myToastS("Error while Choosing Image");
				e.printStackTrace();
			}

		} else {

			myToastS("Error in Choosing Image");
		}

	}

}
