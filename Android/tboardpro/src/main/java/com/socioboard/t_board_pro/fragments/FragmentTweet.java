package com.socioboard.t_board_pro.fragments;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.adapters.SelectAccountAdapter;
import com.socioboard.t_board_pro.twitterapi.OAuthSignaturesGeneratorPostReq;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestPerams;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.FileUtils;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.ModelUserDatas;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentTweet extends Fragment
{

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MainSingleTon.mixpanelAPI.track("Fragment Tweet oncreate called");

		rootView = inflater.inflate(R.layout.fragment_tweetcompose, container,
				false);

		LoadAd();

		chkBox = (CheckBox) rootView.findViewById(R.id.checkBox1);

		textViewCount = (TextView) rootView.findViewById(R.id.textView1Counted);

		imageViewAddUsers = (ImageView) rootView
				.findViewById(R.id.imageViewAddUsers);

		imageView1Choose = (ImageView) rootView
				.findViewById(R.id.imageView1Choose);

		imageViewAttached = (ImageView) rootView
				.findViewById(R.id.imageViewAttached);

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

			if (navDrawerItems.get(i).getUserid()
					.contains(MainSingleTon.currentUserModel.getUserid())) {

				sparseBooleanArray.put(i, true);
				
				count++;
			}
		}
		
		textViewCount.setText("Selected : " + count);

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

	void LoadAd()
	{
		MobileAds.initialize(getActivity(), getString(R.string.adMob_app_id));
		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

	}

	private String filename;

	class uploadIt extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			uploadFile2();

			return null;
		}
	}

	protected void openSelectDialog() {

		final Dialog dialog;

		dialog = new Dialog(getActivity());

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.select_user_dialog);

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

		Button buttonDone, cancelbtn;

		cancelbtn = (Button) dialog.findViewById(R.id.cancelbtn);

		cancelbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.cancel();

			}
		});

		buttonDone = (Button) dialog.findViewById(R.id.button1);

		buttonDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				sparseBooleanArray = selectAccountAdapter.sparseBooleanArray;

				count = selectAccountAdapter.count;

				myprint("buttonDone");

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
		
		View view = getActivity().getCurrentFocus();

		if (view != null) {

			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

		}

		int countProcess = 0;

		for (int i = 0; i < navDrawerItems.size(); i++) {

			if (sparseBooleanArray.get(i)) {

				++countProcess;

				progressDialog.setMessage(countProcess + " out of  .." + count
						+ " tweeted");

				showProgress();

				final ModelUserDatas modelUserDatas = navDrawerItems.get(i);

				TwitterPostRequestPerams twitterPostRequestTweet = new TwitterPostRequestPerams(
						modelUserDatas, new TwitterRequestCallBack() {

							@Override
							public void onSuccess(JSONObject jsonObject) {
								
							System.out.println(" Success JSONObject :"+ jsonObject);

							}

							@Override
							public void onSuccess(String jsonResult) {

								if (modelUserDatas.getUserid().contains(
										MainSingleTon.currentUserModel
												.getUserid())) {

									++MainSingleTon.tweetsCount;

									MainActivity.isNeedToRefreshDrawer = true;

								}

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

										// edttext.setText("");

									}
								});

								myprint("onFailure " + e);
								myToastS(" failed!");
								hideProgress();

							}

						});

				String url = MainSingleTon.updateTweet;

				List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

				peramPairs
						.add(new BasicNameValuePair(Const.status, tweetString));

				twitterPostRequestTweet.executeThisRequest(url, peramPairs);

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

	}

	public void PostFile() {

	}

	public void uploadFile2() {}

	public int uploadFile(String sourceFileUri) {

		authSignaturesGenerator3 = new OAuthSignaturesGeneratorPostReq(
				MainSingleTon.currentUserModel.getUserAcessToken(),
				MainSingleTon.currentUserModel.getUsersecretKey(),
				MainSingleTon.TWITTER_KEY, MainSingleTon.TWITTER_SECRET, "POST");

		HttpURLConnection conn = null;

		DataOutputStream dos = null;

		String lineEnd = "\r\n";

		String twoHyphens = "--";

		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;

		byte[] buffer;

		int maxBufferSize = 1 * 1024 * 1024;

		File sourceFile = new File(sourceFileUri);

		filename = sourceFile.getName();

		System.out.println("****** filename = ");

		//

		if (!sourceFile.isFile()) {

			Log.e("uploadFile",
					"Source File not exist :" + sourceFile.getAbsolutePath()
							+ "");

			return 0;

		} else {

			int serverResponseCode = 0;

			try {

				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);

				URL url = new URL(MainSingleTon.uploadMedia);

				// Open a HTTP connection to the URL

				conn = (HttpURLConnection) url.openConnection();

				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");

				List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

				String authData = getAuthDAta(MainSingleTon.uploadMedia,
						peramPairs);

				conn.addRequestProperty("Authorization", authData);

				conn.setRequestProperty("Connection", "Keep-Alive");

				conn.addRequestProperty("Host", "upload.twitter.com");

				conn.addRequestProperty("User-Agent", "OAuth gem v0.4.4");

				conn.addRequestProperty("X-Target-URI",
						"https://upload.twitter.com");

				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);

				dos.writeBytes("Content-Disposition: file; name=\"media\"; filename=\""
						+ filename + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);

				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);

				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();

				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {

					String response;

					response = readResponse(conn);

					System.out.println("****** response = " + response);

				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				ex.printStackTrace();

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

			} catch (Exception e) {

				e.printStackTrace();

				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}

			return serverResponseCode;

		} // End else block
	}

	OAuthSignaturesGeneratorPostReq authSignaturesGenerator3;

	private String getAuthDAta(String url, List<BasicNameValuePair> peramPairs) {

		authSignaturesGenerator3.setUrl(url);

		String GeneratedPerams = null;

		GeneratedPerams = "OAuth "
				+ authSignaturesGenerator3.OAUTH_CONSUMER_KEY
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.getcKey())
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_NONCE
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.currentOnonce)
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_SIGNATURE_METHOD
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.HMAC_SHA1)
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_TIMESTAMP
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.currentTimeStamp)
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_TOKEN
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.getAccesToken())
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_VERSION
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3.VERSION_1_0)
				+ "\", "
				+ authSignaturesGenerator3.OAUTH_SIGNATURE
				+ "=\""
				+ URLEncoder.encode(authSignaturesGenerator3
						.getOauthSignature(peramPairs)) + "\"";

		String authenticateString = GeneratedPerams;

		String authData = authenticateString;

		return authData;

	}

	public String readResponse(HttpURLConnection connection) {

		try {

			String jsonString = null;

			InputStream linkinStream = connection.getInputStream();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int j = 0;

			while ((j = linkinStream.read()) != -1) {

				baos.write(j);

			}

			byte[] data = baos.toByteArray();

			jsonString = new String(data);

			return jsonString;

		} catch (IOException e) {

			e.printStackTrace();

			return null;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		uri = data.getData();

		if (requestCode == 10) {

			Bitmap bitmap;

			filpathUtils = FileUtils.getPath(getActivity(), uri);

			new uploadIt().execute();

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
