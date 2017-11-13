package com.socioboard.t_board_pro.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.socioboard.t_board_pro.adapters.ToFollowingAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterTimeLineRequest2;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentUsersFollowingToMe extends Fragment implements
		OnScrollListener {

	private static int count;
	View rootView;
	ListView listView;
	Bitmap userImage, userbannerImage;
	public static ToFollowingAdapter myFollowersAdapter;
	public static RelativeLayout reloutProgress;
	Activity aActivity;
	boolean isAlreadyScrolling = true, isneedTostop = false;
	ViewGroup viewGroup;
	Handler handler = new Handler();
	TboardproLocalData tboardproLocalData;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		MainSingleTon.mixpanelAPI.track("Fragment UserFollowingToMe oncreate called");

		rootView = inflater.inflate(R.layout.fragment_my_followers, container,false);
		aActivity = getActivity();
		reloutProgress = (RelativeLayout) rootView.findViewById(R.id.reloutProgress);

		LoadAd();
		listView = (ListView) rootView.findViewById(R.id.listViewMyfolowers);
		listView.setOnScrollListener(this);


		tboardproLocalData=new TboardproLocalData(getActivity());
		tboardproLocalData.getBlackList(MainSingleTon.currentUserModel.getUserid());
		addFooterView();
		viewGroup.setVisibility(View.INVISIBLE);
		if (MainSingleTon.listMyfollowersIDs.size() == 0) {

			cancelProgres();

		} else {

			if (MainSingleTon.myFollowers.size() > 0) {

			/*	myFollowersAdapter = new ToFollowingAdapter(getActivity(),
						MainSingleTon.myFollowers,
						FragmentUsersFollowingToMe.this.getActivity());

				listView.setAdapter(myFollowersAdapter);*/
				MainSingleTon.myFollowers.clear();
				new FollowingToMe().execute();

			} else {

				new FollowingToMe().execute();

			}
		}
		return rootView;
	}

	void LoadAd()
	{
		MobileAds.initialize(getActivity(), getString(R.string.adMob_app_id));
		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

	}

	protected void parseJsonResultPaged(String jsonResult)
	{

		myprint("parseResult  ");

		handler.post(new Runnable() {

			@Override
			public void run() {

				viewGroup.setVisibility(View.INVISIBLE);

			}

		});

		try {

			JSONObject jsonObject = new JSONObject(jsonResult);

			JSONArray jsonArray = jsonObject.getJSONArray("users");

			MainSingleTon.myfollowersNextCursor = jsonObject
					.getString(Const.next_cursor_str);

			myprint("************** MainSingleTon.myfollowersNextCursor "
					+ MainSingleTon.myfollowersNextCursor);

			for (int i = 0; i < jsonArray.length(); ++i) {

				JSONObject jsonObject2 = jsonArray.getJSONObject(i);

				myprint("jsonObject2 " + i + " = " + jsonObject2);

				final ToFollowingModel myFollowersModel = new ToFollowingModel();

				myFollowersModel.setFollowingStatus(jsonObject2.getString(
						Const.following).contains("true"));

				myFollowersModel.setId(jsonObject2.getString(Const.id_str));

				myFollowersModel.setNoFollowers(jsonObject2
						.getString(Const.followers_count));

				myFollowersModel.setNoToFollowing(jsonObject2
						.getString(Const.friends_count));

				myFollowersModel.setNoTweets(jsonObject2
						.getString(Const.listed_count));

				myFollowersModel.setTweeet_str("");

				myFollowersModel.setUserImagerUrl(jsonObject2
						.getString(Const.profile_image_url));

				myFollowersModel.setUserName("@"
						+ jsonObject2.getString(Const.screen_name));
				final String userName="@"+ jsonObject2.getString(Const.screen_name);
				myprint(myFollowersModel);

				final int indexed = i;

				if (FragmentUsersFollowingToMe.this.getActivity() != null) {

					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {

							int listCount = listView.getCount();
							listView.setScrollY(listCount);


							count=0;
							System.out.println("   SSSSSSS  "+MainSingleTon.WhiteListdatas.size());
							if(MainSingleTon.BlackListdatas.size()>0)
							{

								for (int i=0;i<MainSingleTon.BlackListdatas.size();i++)
								{
									String ac[]=MainSingleTon.BlackListdatas.get(i).split(" ");
									System.out.println("   SSSSSSS 222 "+MainSingleTon.BlackListdatas.get(i)+" "+userName+" "+ac[0]);
									if(userName.equalsIgnoreCase(ac[0]))
									{
										System.out.println("AAAAAAA   "+i+" "+count);
										break;
									}else {
										count++;
									}
								}
							}else {
								myFollowersAdapter.tweetModels.add(myFollowersModel);
								MainSingleTon.myFollowers.add(indexed,myFollowersModel);
							}
							if(count==MainSingleTon.BlackListdatas.size()&&count>0)
							{
								myFollowersAdapter.tweetModels.add(myFollowersModel);
								MainSingleTon.myFollowers.add(indexed,myFollowersModel);
							}

						//	myFollowersAdapter.tweetModels.add(myFollowersModel);
						//	MainSingleTon.myFollowers.add(indexed,myFollowersModel);
							myFollowersAdapter.notifyDataSetChanged();

						}

					});
				}
			}

		} catch (JSONException e) {

			e.printStackTrace();

		}

		isAlreadyScrolling = false;

	}

	private void addFooterView() {

		LayoutInflater inflater = getActivity().getLayoutInflater();

		viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
				listView, false);

		listView.addFooterView(viewGroup);

		myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

	}

	public class FollowingToMe extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			showProgress();

			TwitterTimeLineRequest2 twitterTimeLineRequest = new TwitterTimeLineRequest2(
					MainSingleTon.currentUserModel,
					new TwitterRequestCallBack() {

						@Override
						public void onSuccess(String jsonResult) {

							myprint("onSuccess jsonResult " + jsonResult);

							parseJsonResult(jsonResult);

						}

						@Override
						public void onFailure(Exception e) {

							myprint("onFailure e " + e);
						}

						@Override
						public void onSuccess(JSONObject jsonObject) {

						}
					});

			twitterTimeLineRequest
					.doInBackground(MainSingleTon.users_following_to_me);

			return null;

		}

	}

	public class FetchReqPaged extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String urlTimeline = MainSingleTon.users_following_to_me;

			TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
					MainSingleTon.currentUserModel,
					new TwitterRequestCallBack() {

						@Override
						public void onSuccess(String jsonResult) {

							myprint("onSuccess jsonResult " + jsonResult);
							parseJsonResultPaged(jsonResult);
						}

						@Override
						public void onFailure(Exception e) {

							myprint("onFailure e " + e);

							handler.post(new Runnable() {

								@Override
								public void run() {

									viewGroup.setVisibility(View.INVISIBLE);
									isAlreadyScrolling = false;

								}
							});

						}

						@Override
						public void onSuccess(JSONObject jsonObject) {

						}

					});

			List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

			peramPairs.add(new BasicNameValuePair(Const.cursor,
					MainSingleTon.myfollowersNextCursor));

			twitterUserGETRequest.executeThisRequest(urlTimeline, peramPairs);

			return null;
		}

	}

	protected void parseJsonResult(String jsonResult) {

		myprint("parseJsonResult  FragmentUsersFollowingToMe  ");

		try {

			JSONObject jsonObject = new JSONObject(jsonResult);

			JSONArray jsonArray = jsonObject.getJSONArray("users");

			MainSingleTon.myfollowersNextCursor = jsonObject
					.getString(Const.next_cursor_str);

			myprint("************** MainSingleTon.myfollowersNextCursor "
					+ MainSingleTon.myfollowersNextCursor);

			for (int i = 0; i < jsonArray.length(); ++i) {

				JSONObject jsonObject2 = jsonArray.getJSONObject(i);

				myprint("jsonObject2 " + i + " = " + jsonObject2);

				ToFollowingModel myFollowersModel = new ToFollowingModel();

				myFollowersModel.setFollowingStatus(jsonObject2.getString(
						Const.following).contains("true"));

				myFollowersModel.setId(jsonObject2.getString(Const.id_str));

				myFollowersModel.setNoFollowers(jsonObject2
						.getString(Const.followers_count));

				myFollowersModel.setNoToFollowing(jsonObject2
						.getString(Const.friends_count));

				myFollowersModel.setNoTweets(jsonObject2
						.getString(Const.listed_count));

				myFollowersModel.setTweeet_str("");

				myFollowersModel.setUserImagerUrl(jsonObject2
						.getString(Const.profile_image_url));

				myFollowersModel.setUserName("@"
						+ jsonObject2.getString(Const.screen_name));

				myprint(myFollowersModel);
				count=0;
				final String userName="@"+ jsonObject2.getString(Const.screen_name);
				System.out.println("   SSSSSSS  111"+MainSingleTon.BlackListdatas.size());


				if(MainSingleTon.BlackListdatas.size()>0)
				{

					for (int a=0;a<MainSingleTon.BlackListdatas.size();a++)
					{
						String ac[]=MainSingleTon.BlackListdatas.get(a).split(" ");
						System.out.println("   SSSSSSS 222 "+MainSingleTon.BlackListdatas.get(a)+" "+userName+" "+ac[0]);
						if(userName.equalsIgnoreCase(ac[0]))
						{
							System.out.println("AAAAAAA   "+a+" "+count);
							break;
						}else {
							count++;
						}
					}
				}else {
					MainSingleTon.myFollowers.add(myFollowersModel);
				}
				if(count==MainSingleTon.BlackListdatas.size()&&count>0)
				{
					MainSingleTon.myFollowers.add(myFollowersModel);


				}
				System.out.println("........... "+MainSingleTon.BlackListdatas);


				//MainSingleTon.myFollowers.add(myFollowersModel);

				myprint(myFollowersModel);

			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (FragmentUsersFollowingToMe.this.getActivity() != null) {

						myFollowersAdapter = new ToFollowingAdapter(
								getActivity(), MainSingleTon.myFollowers,
								FragmentUsersFollowingToMe.this.getActivity());

						listView.setAdapter(myFollowersAdapter);

					}
				}
			});

		} catch (JSONException e) {

			e.printStackTrace();

		}

		isAlreadyScrolling = false;

		cancelProgres();

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

	void showProgress() {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				reloutProgress.setVisibility(View.VISIBLE);
			}

		});

	}

	void cancelProgres() {

		aActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				reloutProgress.setVisibility(View.INVISIBLE);
			}
		});
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		/* maybe add a padding */

		boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

		if (loadMore) {

			myprint("YESSSSSSSSSSSSS load MOOOOOOOOOREE");

			if (isAlreadyScrolling) {

				// DO NOTHING
				myprint("BUT isAlreadyScrolling ");

			} else {

				isAlreadyScrolling = true;

				//myprint(myFollowersAdapter.getItem(myFollowersAdapter.getCount() - 1));

				if (MainSingleTon.myfollowersNextCursor.length() == 1) {

					myprint("MainSingleTon.myfollowersNextCursor.length() == 1 DONt LOad");

				} else {

					viewGroup.setVisibility(View.VISIBLE);

					new FetchReqPaged().execute();

				}

			}

		} else {

			myprint("NOOOOOOOOO DONt LOad");

		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
