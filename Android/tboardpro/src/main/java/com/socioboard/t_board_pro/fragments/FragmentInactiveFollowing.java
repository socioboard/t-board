package com.socioboard.t_board_pro.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.adapters.ToFollowingAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.socioboard.t_board_pro.util.Const.user_id;


public class FragmentInactiveFollowing extends Fragment implements AbsListView.OnScrollListener {


    View rootView;

    Activity aActivity;

    public ToFollowingAdapter toFollowingAdp;

    ListView listView;

    RelativeLayout reloutProgress;

    TboardproLocalData tboardproLocalData;

    Button button;

    Handler handler = new Handler();

    String time,text;

    ArrayList<ToFollowingModel> adapterModels = new ArrayList<ToFollowingModel>();

    boolean isAlreadyScrolling = true;

    int i,id,month=0,month1=0;

    boolean running=false;

    SimpleDateFormat sdf;

    long t;

    TextView showmonthtextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        MainSingleTon.mixpanelAPI.track("Fragment InactiveFollowing oncreate called");

        aActivity = getActivity();

        sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

        Calendar calendar = Calendar.getInstance();

        t = calendar.getTimeInMillis();

        rootView = inflater.inflate(R.layout.fragment_fragment_inactive_following, container, false);

        reloutProgress = (RelativeLayout)rootView.findViewById(R.id.reloutProgress);

        button = (Button)rootView.findViewById(R.id.buttonSelectMonth);

        listView = (ListView)rootView.findViewById(R.id.listviewinactivefollowing);

        showmonthtextView = (TextView)rootView.findViewById(R.id.countTextView);

        System.out.println("To following id"+ MainSingleTon.toFollowingModelsIDs);

        init();

        return rootView;
    }

    public void init()
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(FragmentInactiveFollowing.this.getContext());

                dialog.setContentView(R.layout.inactive_following_dialog);

                dialog.setTitle("Select Month");

                Button Applybutton = (Button)dialog.findViewById(R.id.applybutton);

                dialog.setCancelable(true);

                final RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.radiogrp);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if(checkedId==R.id.onemonthradio)
                        {
                            month1 =1;
                            month=30;
                        }else if(checkedId==R.id.threemonthradio)
                        {
                            month1=3;
                            month=90;
                        }else if(checkedId==R.id.sixmonthradio)
                        {
                            month1=6;
                            month=180;
                        }
                    }
                });

                Applybutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(radioGroup.getCheckedRadioButtonId()==-1)
                        {
                            Toast.makeText(getContext(),"Select Month",Toast.LENGTH_SHORT).show();
                        }else
                        {

                            dialog.cancel();

                            adapterModels.clear();

                            for(i=0;i<MainSingleTon.toFollowingModelsIDs.size();i++)
                            {
                                new FetchUserTimeLine().execute(MainSingleTon.toFollowingModelsIDs.get(i));

                                //FetchUserTimeLine2(MainSingleTon.toFollowingModelsIDs.get(i));
                            }

                            showProgress();

                            showmonthtextView.setText("Inactive From "+month1+" Months");

                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    cancelProgres();
                                }
                            },22000);
                        }

                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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


    public class FetchUserTimeLine extends AsyncTask<String,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

                String madMaxId = params[0];

                System.out.println("madMaxId======"+madMaxId);

                TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
                        MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

                    @Override
                    public void onSuccess(String jsonResult) {
                        parseJsonResult(jsonResult);
                    }

                    @Override
                    public void onFailure(Exception e) {

                        System.out.println("onFailure e " + e);

                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {

                    }

                });

                List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

                peramPairs.add(new BasicNameValuePair(user_id,madMaxId));

                twitterUserGETRequest.executeThisRequest(MainSingleTon.userAccountData, peramPairs);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }


    public void parseJsonResult(String jsonresult)
    {
        ToFollowingModel fullUserDetailModel = new ToFollowingModel();

        try
        {
            JSONObject jsonObject = new JSONObject(jsonresult);

            fullUserDetailModel.setFollowingStatus(jsonObject
                    .getString(Const.following)
                    .contains("true"));

            fullUserDetailModel.setUserImagerUrl(jsonObject
                    .getString(Const.profile_image_url));

            fullUserDetailModel.setNoFollowers(jsonObject
                    .getString(Const.followers_count));

            fullUserDetailModel.setNoToFollowing(jsonObject
                    .getString(Const.friends_count));

            fullUserDetailModel.setUserName(jsonObject
                    .getString(Const.screen_name));

//            fullUserDetailModel.setFullName(jsonObject
//                    .getString(Const.name));

            fullUserDetailModel.setId(jsonObject
                    .getString(Const.id_str));

            fullUserDetailModel.setNoTweets(jsonObject
                    .getString(Const.statuses_count));


            JSONObject jsonObject1 = jsonObject.getJSONObject("status");

            for(int i=0;i<jsonObject1.length();i++)
            {
                 time = jsonObject1.getString("created_at");
                 text = jsonObject1.getString("text");

            }



            long  timeInMilliseconds=0;

            try {

                Date mDate = sdf.parse(time);

                timeInMilliseconds = mDate.getTime();

                System.out.println("Date in milli :: " + timeInMilliseconds);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


            long millisecond = t -  timeInMilliseconds;

            long days = TimeUnit.MILLISECONDS.toDays(millisecond);

            System.out.println("millisecond-=-= "+millisecond);

            System.out.println("days-=-=-="+days);

            if(days>month)
            {
                adapterModels.add(fullUserDetailModel);

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (FragmentInactiveFollowing.this.getActivity() != null)
                        {

                            toFollowingAdp = new ToFollowingAdapter(getActivity(),
                                    adapterModels, FragmentInactiveFollowing.this.getActivity());

                            listView.setAdapter(toFollowingAdp);

                            isAlreadyScrolling = false;
                        }
                    }
                });
            }

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = true;
    }
}

