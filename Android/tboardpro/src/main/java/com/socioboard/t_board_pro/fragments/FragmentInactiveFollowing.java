package com.socioboard.t_board_pro.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.socioboard.t_board_pro.adapters.TweetsAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.tboardpro.R;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


public class FragmentInactiveFollowing extends Fragment {


    View rootView;
    Activity aActivity;
    TweetsAdapter tweetsAdapter;
    ListView listView;
    Timer timer = new Timer();
    Handler handler = new Handler();
    ArrayList<String> followingId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        aActivity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_fragment_inactive_following, container, false);
        followingId = new ArrayList<>();

        listView = (ListView)rootView.findViewById(R.id.listviewinactivefollowing);
        System.out.println("To following id"+ MainSingleTon.toFollowingModelsIDs);
        System.out.println("To following size"+ MainSingleTon.toFollowingModelsIDs.size());
        for(int i=0;i<MainSingleTon.toFollowingModelsIDs.size();i++)
        {
            System.out.println("datasfds"+MainSingleTon.toFollowingModelsIDs.get(i));
            followingId.add(MainSingleTon.toFollowingModelsIDs.get(i));
            FetchTimelineLatestPaged(MainSingleTon.toFollowingModelsIDs.get(i));
        }
        System.out.println("following id"+followingId);
        return rootView;
    }

    public void FetchTimelineLatestPaged(String... params) {

        String madMaxId = params[0].toString();

        System.out.println("madMaxId======"+madMaxId);

        String urlTimeline = "https://api.twitter.com/1.1/statuses/user_timeline.json";

        TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
                MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

            @Override
            public void onSuccess(String jsonResult) {

                System.out.println("onSuccess jsonResultdfgdfgfgdgdf " + jsonResult);

                //MainActivity.HideActionBarProgress();

                //parseJsonResultPagedLatest(jsonResult);

            }

            @Override
            public void onFailure(Exception e) {

                System.out.println("onFailure e " + e);

                //MainActivity.HideActionBarProgress();
            }

            @Override
            public void onSuccess(JSONObject jsonObject) {

            }

        });

        List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

        peramPairs.add(new BasicNameValuePair(Const.since_id, madMaxId));

        peramPairs.add(new BasicNameValuePair(Const.count, "1"));

        peramPairs.add(new BasicNameValuePair(Const.include_entities, "false"));

        twitterUserGETRequest.executeThisRequest(urlTimeline, peramPairs);

    }

}
