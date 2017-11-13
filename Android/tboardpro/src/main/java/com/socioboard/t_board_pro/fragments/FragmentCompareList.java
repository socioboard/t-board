package com.socioboard.t_board_pro.fragments;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestFollow;
import com.socioboard.t_board_pro.twitterapi.TwitterPostRequestUnFollow;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.tboardpro.R;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FragmentCompareList extends Fragment
{

    public FragmentCompareList() {

    }

    View view;
    Button button;

    String sourcename,targetname;
    RelativeLayout fieldlayout,userstatusRTL;

    String output,output1;

    String sid,tid,sscreen_name,tscreen_name;

    public static Boolean sfollowing,tfollowing;

    public static String sourceimgurl,targetimgurl;



    ImageView sourceImage, targetImage;

    ImageView sourcefollowingimg,sourcefollowingcrossimg,targetfollowingimg,targetfollowingcrossimg;

    TextView sourceUserName, targetUserName;

    TextView sourceFollowingTxt,sourceNotFollowingTxt,targetfollowingTxt,targetNotFollowingTxt;

    EditText sourceName,targetName;

    Button followImageButton, unfollowImageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        MainSingleTon.mixpanelAPI.track("Fragment CompareList oncreate called");

        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_fragment_compare_list, container, false);

        LoadAd();

        button=(Button)view.findViewById(R.id.compare_list);

        sourceName = (EditText)view.findViewById(R.id.source_name);

        targetName = (EditText)view.findViewById(R.id.target_name);

        sourceImage = (ImageView)view.findViewById(R.id.sourceImg);

        targetImage = (ImageView)view.findViewById(R.id.targetImg);

        sourceUserName = (TextView)view.findViewById(R.id.sourceUsername);

        targetUserName = (TextView)view.findViewById(R.id.targerUsername);

        fieldlayout = (RelativeLayout)view.findViewById(R.id.fieldlayout);

        userstatusRTL = (RelativeLayout)view.findViewById(R.id.userstatusRTL);

        sourcefollowingimg = (ImageView)view.findViewById(R.id.sourcefollowimg);

        sourcefollowingcrossimg = (ImageView)view.findViewById(R.id.sourcefollowimgcross);

        targetfollowingimg = (ImageView)view.findViewById(R.id.targetfollowing);

        targetfollowingcrossimg = (ImageView)view.findViewById(R.id.targetfollowimgcross);

        sourceFollowingTxt = (TextView)view.findViewById(R.id.sourcefollowtext);

        sourceNotFollowingTxt = (TextView)view.findViewById(R.id.sourcenotfollowtext);

        targetfollowingTxt = (TextView)view.findViewById(R.id.targetfollowtext);

        targetNotFollowingTxt = (TextView)view.findViewById(R.id.targetnotfollowtext);

        followImageButton = (Button) view.findViewById(R.id.followimgbtnplus);

        unfollowImageButton = (Button) view.findViewById(R.id.unfollowimgbtnminus);

        userstatusRTL.setVisibility(View.INVISIBLE);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sourcename = sourceName.getText().toString();

                targetname = targetName.getText().toString();

                if(sourcename.isEmpty() || targetname.isEmpty() )
                {
                    System.out.println("Please Enter name in both souce and target");
                    Toast.makeText(getActivity(),"Please Enter Name in both source and target",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    System.out.println("inside execution");

                    new getFriendship().execute();

                    new getFriendship().execute();


                }

                System.out.println("inside on click");
            }
        });



        followImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(),"Follow",Toast.LENGTH_LONG).show();

                followImageButton.setVisibility(View.INVISIBLE);
                unfollowImageButton.setVisibility(View.VISIBLE);



                sourceFollowingTxt.setVisibility(View.VISIBLE);
                sourcefollowingimg.setVisibility(View.VISIBLE);


                sourcefollowingcrossimg.setVisibility(View.INVISIBLE);
                sourceNotFollowingTxt.setVisibility(View.INVISIBLE);



                TwitterPostRequestFollow twitterPostRequestFollow = new TwitterPostRequestFollow(
                        MainSingleTon.currentUserModel,
                        new TwitterRequestCallBack() {

                            @Override
                            public void onSuccess(JSONObject jsonObject) {

                            }

                            @Override
                            public void onSuccess(String jsonResult) {



                                ++MainSingleTon.followingCount;

                                MainSingleTon.toFollowingModelsIDs
                                        .add(tid);

                                MainActivity.isNeedToRefreshDrawer = true;

                            }

                            @Override
                            public void onFailure(Exception e) {


                            }

                        });
                twitterPostRequestFollow.executeThisRequest(tid);
            }
        });


        unfollowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Unfollow",Toast.LENGTH_LONG).show();
                unfollowImageButton.setVisibility(View.INVISIBLE);
                followImageButton.setVisibility(View.VISIBLE);

                sourceFollowingTxt.setVisibility(View.INVISIBLE);
                sourcefollowingimg.setVisibility(View.INVISIBLE);


                sourcefollowingcrossimg.setVisibility(View.VISIBLE);
                sourceNotFollowingTxt.setVisibility(View.VISIBLE);


                TwitterPostRequestUnFollow twitterPostRequestUnFollow = new TwitterPostRequestUnFollow(
                        MainSingleTon.currentUserModel,
                        new TwitterRequestCallBack() {

                            @Override
                            public void onSuccess(JSONObject jsonObject) {



                            }

                            @Override
                            public void onSuccess(String jsonResult) {



                                --MainSingleTon.followingCount;

                                MainSingleTon.toFollowingModelsIDs
                                        .remove(tid);

                                MainActivity.isNeedToRefreshDrawer = true;


                            }

                            @Override
                            public void onFailure(Exception e) {


                            }

                        });

                twitterPostRequestUnFollow.executeThisRequest(tid);
            }
        });

        return  view;
    }


    void LoadAd()
    {
        MobileAds.initialize(getActivity(), getString(R.string.adMob_app_id));
        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }



    public void call()
    {

        userstatusRTL.setVisibility(View.VISIBLE);
        System.out.println("inside call method");


        sourceUserName.setText("@"+sscreen_name);
        targetUserName.setText("@"+tscreen_name);


        if(MainSingleTon.currentUserModel.getUserid().equals(sid))
        {
            followImageButton.setVisibility(View.VISIBLE);
            unfollowImageButton.setVisibility(View.VISIBLE);

            System.out.println("MainSingleTon.currentUserModel.getUserid()="+MainSingleTon.currentUserModel.getUserid());


            if(sfollowing==null)
            {
                System.out.println("inside sfollowing null==="+sfollowing);

                System.out.println("!sfollowing"+sfollowing);

                new getFriendship().execute();
            }else if(!sfollowing)
            {
                sourceFollowingTxt.setVisibility(View.INVISIBLE);
                sourcefollowingimg.setVisibility(View.INVISIBLE);
                unfollowImageButton.setVisibility(View.INVISIBLE);

                sourcefollowingcrossimg.setVisibility(View.VISIBLE);
                sourceNotFollowingTxt.setVisibility(View.VISIBLE);
                followImageButton.setVisibility(View.VISIBLE);


                System.out.println("sfollowing=sdfsd"+sfollowing);
            }else
            {
                sourceFollowingTxt.setVisibility(View.VISIBLE);
                sourcefollowingimg.setVisibility(View.VISIBLE);
                unfollowImageButton.setVisibility(View.VISIBLE);

                sourcefollowingcrossimg.setVisibility(View.INVISIBLE);
                sourceNotFollowingTxt.setVisibility(View.INVISIBLE);
                followImageButton.setVisibility(View.INVISIBLE);

            }


            if(tfollowing==null)
            {
                System.out.println("inside tfollowing null=="+tfollowing);
                System.out.println("!tfollowing"+tfollowing);
                new getFriendship().execute();
            }else if(!tfollowing)
            {
                targetfollowingTxt.setVisibility(View.INVISIBLE);
                targetfollowingimg.setVisibility(View.INVISIBLE);

                targetfollowingcrossimg.setVisibility(View.VISIBLE);
                targetNotFollowingTxt.setVisibility(View.VISIBLE);
                System.out.println("!tfollowingsdfsdf"+tfollowing);

            }else {
                targetfollowingTxt.setVisibility(View.VISIBLE);
                targetfollowingimg.setVisibility(View.VISIBLE);

                targetfollowingcrossimg.setVisibility(View.INVISIBLE);
                targetNotFollowingTxt.setVisibility(View.INVISIBLE);
            }




        }else {
            followImageButton.setVisibility(View.INVISIBLE);
            unfollowImageButton.setVisibility(View.INVISIBLE);
            System.out.println("id=sdfsdfsdf" + sid);



            if(sfollowing==null)
            {
                System.out.println("inside sfollowing null==="+sfollowing);

                System.out.println("!sfollowing"+sfollowing);

                new getFriendship().execute();
            }else if(!sfollowing)
            {
                sourceFollowingTxt.setVisibility(View.INVISIBLE);
                sourcefollowingimg.setVisibility(View.INVISIBLE);
                //unfollowImageButton.setVisibility(View.INVISIBLE);

                sourcefollowingcrossimg.setVisibility(View.VISIBLE);
                sourceNotFollowingTxt.setVisibility(View.VISIBLE);
                //followImageButton.setVisibility(View.VISIBLE);


                System.out.println("sfollowing=sdfsd"+sfollowing);
            }else
            {
                sourceFollowingTxt.setVisibility(View.VISIBLE);
                sourcefollowingimg.setVisibility(View.VISIBLE);
                //unfollowImageButton.setVisibility(View.VISIBLE);

                sourcefollowingcrossimg.setVisibility(View.INVISIBLE);
                sourceNotFollowingTxt.setVisibility(View.INVISIBLE);
                //followImageButton.setVisibility(View.INVISIBLE);

            }



            if(tfollowing==null)
            {
                System.out.println("inside tfollowing null=="+tfollowing);
                System.out.println("!tfollowing"+tfollowing);
                new getFriendship().execute();
            }else if(!tfollowing)
            {
                targetfollowingTxt.setVisibility(View.INVISIBLE);
                targetfollowingimg.setVisibility(View.INVISIBLE);

                targetfollowingcrossimg.setVisibility(View.VISIBLE);
                targetNotFollowingTxt.setVisibility(View.VISIBLE);
                System.out.println("!tfollowingsdfsdf"+tfollowing);

            }else {
                targetfollowingTxt.setVisibility(View.VISIBLE);
                targetfollowingimg.setVisibility(View.VISIBLE);

                targetfollowingcrossimg.setVisibility(View.INVISIBLE);
                targetNotFollowingTxt.setVisibility(View.INVISIBLE);
            }


        }



        for(int i=0;i<2;i++)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(getActivity()).load(sourceimgurl).into(sourceImage);
                    Picasso.with(getActivity()).load(targetimgurl).into(targetImage);
                }
            },1200);
        }

    }



    class getFriendship extends AsyncTask<Void,Void,Void>
    {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("processing....");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            TwitterUserGETRequest twitterGetFriendship = new TwitterUserGETRequest(
                    MainSingleTon.currentUserModel, new TwitterRequestCallBack() {
                @Override
                public void onSuccess(String jsonResult) {

                    System.out.println("found json data---------"+jsonResult);


                    callJsonParsar(jsonResult);
                }

                @Override
                public void onSuccess(JSONObject jsonObject) {

                    System.out.println("found json data");

                }

                @Override
                public void onFailure(Exception e) {

                    System.out.println("onFailure"+e);
                }
            });


            List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

            peramPairs.add(new BasicNameValuePair(Const.sourcename,
                    sourcename));

            peramPairs.add(new BasicNameValuePair(Const.targetname,
                    targetname));

            twitterGetFriendship.executeThisRequest(
                    MainSingleTon.get_friendships, peramPairs);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            call();
            this.dialog.dismiss();

        }


    }

    public void callJsonParsar(String jsonResult)
    {

        try
        {
            JSONObject jsonObject1 = new JSONObject(jsonResult);

            JSONObject jsonObject2 = new JSONObject(jsonObject1.getString("relationship"));

            try {

                JSONObject source = new JSONObject(jsonObject2.getString("source"));

                for(int i=0;i<source.length();i++)
                {

                    String id = source.getString("id");

                    sid = id;

                    sscreen_name = source.getString("screen_name");

                    sfollowing = source.getBoolean("following");

                    output = "id="+id+"\n"+"screen_name="+sscreen_name+"\n"+"following="+sfollowing;

                }
                System.out.println("output"+output);

                getUserImage(sid);


            }catch (JSONException f)
            {
                f.printStackTrace();
            }


            try {

                JSONObject target = new JSONObject(jsonObject2.getString("target"));

                for(int i=0;i<target.length();i++)
                {

                    String id = target.getString("id");

                    tid=id;

                    tscreen_name = target.getString("screen_name");

                    tfollowing = target.getBoolean("following");

                    output1 = "id="+id+"\n"+"screen_name="+tscreen_name+"\n"+"following="+tfollowing;
                }

                System.out.println("output1"+output1);

                getUserImageTarget(tid);

            }catch (JSONException f)
            {
                f.printStackTrace();
            }



        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    public void getUserImage(String id)
    {
        TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(MainSingleTon.currentUserModel, new TwitterRequestCallBack() {
            @Override
            public void onSuccess(String jsonResult) {
                System.out.println("onSuccess jsonResultsdfdsf " + jsonResult);

                parseJsonResultPaged(jsonResult);

            }

            @Override
            public void onSuccess(JSONObject jsonObject) {
                System.out.println("JSONObject="+jsonObject);

            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

        peramPairs.add(new BasicNameValuePair(Const.user_id,
                id));

        peramPairs.add(new BasicNameValuePair(Const.include_entities,
                "false"));

        twitterUserGETRequest.executeThisRequest(
                MainSingleTon.userShowIds, peramPairs);

    }


    protected void parseJsonResultPaged(String jsonResult)
    {

        System.out.println("parseJsonResult==  " + jsonResult);

        try {

            JSONArray jsonArray = new JSONArray(jsonResult);

            for (int i = 0; i < jsonArray.length(); ++i) {

                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                System.out.println("jsonObject2 " + i + " = " + jsonObject2);

                sourceimgurl=jsonObject2.getString(Const.profile_image_url);
            }


        } catch (JSONException e) {

            e.printStackTrace();

        }
    }




    public void getUserImageTarget(String id)
    {
        TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(MainSingleTon.currentUserModel, new TwitterRequestCallBack() {
            @Override
            public void onSuccess(String jsonResult) {
                System.out.println("onSuccess jsonResultsdfdsf " + jsonResult);

                parseJsonResultPagedTarget(jsonResult);

            }

            @Override
            public void onSuccess(JSONObject jsonObject) {
                System.out.println("JSONObject="+jsonObject);

            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

        peramPairs.add(new BasicNameValuePair(Const.user_id,
                id));

        peramPairs.add(new BasicNameValuePair(Const.include_entities,
                "false"));

        twitterUserGETRequest.executeThisRequest(
                MainSingleTon.userShowIds, peramPairs);

    }


    protected void parseJsonResultPagedTarget(String jsonResult) {

        System.out.println("parseJsonResult==  " + jsonResult);

        try {

            JSONArray jsonArray = new JSONArray(jsonResult);

            for (int i = 0; i < jsonArray.length(); ++i) {

                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                System.out.println("jsonObject2 " + i + " = " + jsonObject2);

                targetimgurl = jsonObject2.getString(Const.profile_image_url);
            }

        } catch (JSONException e) {

            e.printStackTrace();

        }
    }

}
