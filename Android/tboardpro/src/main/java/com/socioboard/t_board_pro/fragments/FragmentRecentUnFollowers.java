package com.socioboard.t_board_pro.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.socioboard.t_board_pro.MainActivity;
import com.socioboard.t_board_pro.adapters.ToUnFollowingAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
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


public class FragmentRecentUnFollowers extends Fragment implements AbsListView.OnScrollListener {
    int id;
    View rootView;
    Activity aActivity;
    TboardproLocalData tboardproLocalData;
    RelativeLayout reloutProgress;
    ListView listView;
    ViewGroup viewGroup;
    public ToUnFollowingAdapter toUnFollowingAdp;
    ArrayList<String> rescentsIds = new ArrayList<String>();
    ArrayList<ToFollowingModel> rscFollowers = new ArrayList<ToFollowingModel>();
    boolean isAlreadyScrolling = true;

    Handler handler = new Handler();

    MainActivity mainActivity ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        aActivity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_fragment_recent_un_followers, container, false);

        tboardproLocalData = new TboardproLocalData(getActivity());

        reloutProgress = (RelativeLayout) rootView.findViewById(R.id.reloutProgress);

        listView = (ListView) rootView.findViewById(R.id.listViewToUnFollowing);

        listView.setOnScrollListener(this);

        addFooterView();

        mainActivity = new MainActivity();

        checkJsonData();//13/06/2017

        viewGroup.setVisibility(View.INVISIBLE);

        showProgress();

        return rootView;
    }


    void cancelProgres() {

        aActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                reloutProgress.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void addFooterView() {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
                listView, false);

        //listView.addFooterView(viewGroup);

        myprint("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

        viewGroup.setVisibility(View.INVISIBLE);

        showProgress();

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


    public void rescentUnFollowers()
    {
        System.out.println("inside rescenUnfollowers");
        TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(MainSingleTon.currentUserModel, new TwitterRequestCallBack() {
            @Override
            public void onSuccess(String jsonResult) {
                myprint("onSuccess jsonResult===== " + jsonResult);

                parseJsonResult(jsonResult);
            }

            @Override
            public void onSuccess(JSONObject jsonObject) {
                myprint("onSuccess JSONObject " + jsonObject);
            }

            @Override
            public void onFailure(Exception e) {


                cancelProgres();

            }
        });


        String userswithComma = "";

        System.out.println("rscFollowers.size() " + rscFollowers.size());
        System.out.println("rescentsIds.size() " + rescentsIds.size());


        for (int i = 0; i < rescentsIds.size(); i++) {


            userswithComma = userswithComma + "," + rescentsIds.get(i);

            myprint(i + "++++++++++ i first " + userswithComma);

        }
        System.out.println("unfollower list=" + userswithComma);

        System.out.println("Const.Unfollowuser_id" + Const.Unfollowuser_id);
        List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

        peramPairs
                .add(new BasicNameValuePair(Const.Unfollowuser_id, userswithComma));

        peramPairs.add(new BasicNameValuePair(Const.unfollowinclude_entities,
                "false"));

        twitterUserGETRequest.executeThisRequest(MainSingleTon.userShowIds,
                peramPairs);

        System.out.println("Const.Unfollowuser_id" + Const.Unfollowuser_id);
        System.out.println("Const.unfollowinclude_entities" + Const.unfollowinclude_entities);

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        /* maybe add a padding */

        boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

        if (loadMore) {

            myprint("YESSSSSSSSSSSSS load MOOOOOOOOOREE");

            if (isAlreadyScrolling) {

                myprint("BUT isAlreadyScrolling ");

            } else {

                viewGroup.setVisibility(View.VISIBLE);

                isAlreadyScrolling = true;

                myprint(toUnFollowingAdp.getItem(toUnFollowingAdp.getCount() - 1));

                new FragmentRecentUnFollowers.FetchReqPaged().execute();

            }

        } else {

            myprint("NOOOOOOOOO DONt LOad");

        }

    }

    protected void parseJsonResult(String jsonResult)
    {

        myprint("parseJsonResult==  " + jsonResult);

        try {

            JSONArray jsonArray = new JSONArray(jsonResult);

            for (int i = 0; i < jsonArray.length(); ++i) {

                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                myprint("jsonObject2 " + i + " = " + jsonObject2);

                ToFollowingModel followingModel = new ToFollowingModel();

                followingModel.setFollowingStatus(true);

                followingModel.setId(jsonObject2.getString(Const.id_str));

                followingModel.setNoFollowers(jsonObject2
                        .getString(Const.followers_count));

                followingModel.setNoToFollowing(jsonObject2
                        .getString(Const.friends_count));

                followingModel.setNoTweets(jsonObject2
                        .getString(Const.listed_count));

                followingModel.setTweeet_str("");

                followingModel.setUserImagerUrl(jsonObject2
                        .getString(Const.profile_image_url));

                followingModel.setUserName("@"
                        + jsonObject2.getString(Const.screen_name));

                rscFollowers.add(followingModel);

                myprint(followingModel);

                System.out.println("followingModel=" + followingModel);

            }

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (FragmentRecentUnFollowers.this.getActivity() != null) {

                        toUnFollowingAdp = new ToUnFollowingAdapter(getActivity(),
                                rscFollowers, FragmentRecentUnFollowers.this
                                .getActivity());

                        System.out.println("toUnfollwingAdp==" + toUnFollowingAdp);


                        listView.setAdapter(toUnFollowingAdp);

                        isAlreadyScrolling = false;

                    }
                }
            });

        } catch (JSONException e) {

            e.printStackTrace();

        }
        cancelProgres();
    }


    public class FetchReqPaged extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

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

                        }

                        @Override
                        public void onSuccess(JSONObject jsonObject) {

                            myprint("onSuccess JSONObject " + jsonObject);

                        }

                    });

            String userswithComma = "";

            if (rscFollowers.size() >= rescentsIds.size()) {

                handler.post(new Runnable() {

                    @Override
                    public void run() {

                        viewGroup.setVisibility(View.INVISIBLE);

                    }
                });

            } else {

                for (int i = rscFollowers.size(); i < (rscFollowers.size() + 99); ++i) {

                    if (i == rscFollowers.size()) {

                        userswithComma = rescentsIds.get(i);

                    } else {

                        try {

                            userswithComma = "," + rescentsIds;

                        } catch (Exception e) {
                            break;
                        }

                    }

                }

                List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

                peramPairs.add(new BasicNameValuePair(Const.user_id,
                        userswithComma));

                peramPairs.add(new BasicNameValuePair(Const.include_entities,
                        "false"));

                twitterUserGETRequest.executeThisRequest(
                        MainSingleTon.userShowIds, peramPairs);

            }

            return null;
        }

    }

    protected void parseJsonResultPaged(String jsonResult) {

        myprint("parseJsonResult  ");

        handler.post(new Runnable() {

            @Override
            public void run() {

                viewGroup.setVisibility(View.INVISIBLE);

            }
        });

        try {

            JSONObject jsonObject = new JSONObject(jsonResult);

            JSONArray jsonArray = jsonObject.getJSONArray("users");

            for (int i = 0; i < jsonArray.length(); ++i) {

                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                myprint("jsonObject2 " + i + " = " + jsonObject2);

                final ToFollowingModel followingModel = new ToFollowingModel();

                followingModel.setFollowingStatus(jsonObject2.getString(
                        Const.following).contains("true"));

                followingModel.setId(jsonObject2.getString(Const.id_str));

                followingModel.setNoFollowers(jsonObject2
                        .getString(Const.followers_count));

                followingModel.setNoToFollowing(jsonObject2
                        .getString(Const.friends_count));

                followingModel.setNoTweets(jsonObject2
                        .getString(Const.listed_count));

                followingModel.setTweeet_str("");

                followingModel.setUserImagerUrl(jsonObject2
                        .getString(Const.profile_image_url));

                followingModel.setUserName("@"
                        + jsonObject2.getString(Const.screen_name));

                myprint(followingModel);

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (FragmentRecentUnFollowers.this.getActivity() != null) {

                            int listCount = listView.getCount();

                            toUnFollowingAdp.tweetModels.add(followingModel);

                            listView.setScrollY(listCount);

                            toUnFollowingAdp.notifyDataSetChanged();

                            rscFollowers = toUnFollowingAdp.tweetModels;

                        }
                    }
                });

            }

        } catch (JSONException e) {

            e.printStackTrace();

        }

        isAlreadyScrolling = false;

    }

    public ArrayList<String> differenciate(ArrayList<String> a, ArrayList<String> b) {


        // difference a-b
        List<String> c = new ArrayList<String>();
        c.clear();

        ArrayList<String> intersection = new ArrayList(a);
        intersection.retainAll(b);
        System.out.println("intersection=" + intersection);

        a.removeAll(intersection);

        System.out.println("value of new add users" + a);
        for (String n : a) {
            System.out.println(n);

        }

        System.out.println("c==++" + a);

        return (ArrayList<String>) a;
    }


    private void checkJsonData()
    {
        TwitterUserGETRequest userGETRequest = new TwitterUserGETRequest(
                MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                // TODO Auto-generated method stub
                System.out.println("kihdnsikhdiwo 11111 "+jsonObject);
            }

            @Override
            public void onSuccess(String jsonResult) {
                System.out.println("kihdnsikhdiwo 22222 "+jsonResult);



                MainSingleTon.listMyfollowersIDs.clear();

                myprint("sdfsdfMainSingleTon.listMyfollowersIDs.size"
                        + MainSingleTon.listMyfollowersIDs.size());

                try {

                    JSONObject jsonObject = new JSONObject(jsonResult);

                    JSONArray jsonArray;

                    try {

                        jsonArray = new JSONArray(jsonObject
                                .getString("ids"));

                        for (int i = 0; i < jsonArray.length(); ++i) {

                            MainSingleTon.listMyfollowersIDs
                                    .add(jsonArray.getString(i).trim());

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                    myprint("MainSingleTon.listMyfollowersIDs.sizesdfsdfsdfsdf"
                            + MainSingleTon.listMyfollowersIDs.size());

                    MainSingleTon.myfollowersCount = MainSingleTon.listMyfollowersIDs
                            .size();


                    updataFollowerTable(jsonResult);//13/06/2017
                    //call(jsonResult);


                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }



            @Override
            public void onFailure(Exception e) {
                // TODO Auto-generated method stub

            }

        });

        String url = MainSingleTon.users_following_to_me_Ids;

        List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

        peramPairs.add(new BasicNameValuePair(Const.cursor, "-1"));

        peramPairs.add(new BasicNameValuePair(Const.count, "5000"));

        userGETRequest.executeThisRequest(url, peramPairs);

    }

    public void call(String result)
    {
            System.out.println("dfsdfsdfdfs");
            System.out.println("resultsfdsfdf"+result);
            updataFollowerTable(result);
    }




    ////////////////////////////////////////////////////////////////////13/06/2017

    public void updataFollowerTable(String jsonResult)
    {
        System.out.println("sdfsdfdsf"+jsonResult);
        String jsonDBResult = tboardproLocalData.getAllFollowersIDsTemp(MainSingleTon.currentUserModel.getUserid());

        System.out.println("inside updatefollower"+jsonDBResult);

        ArrayList<String> unfollowtemptableid = new ArrayList<>();

        System.out.println("MainSingleTon.listMyfollowersIDs.size()"+MainSingleTon.listMyfollowersIDs.size());

        //MainSingleTon.myfollowersCount = MainSingleTon.listMyfollowersIDs.size();


        if(jsonDBResult==null)
        {
            tboardproLocalData.addFollwersIdsTemp(jsonResult,MainSingleTon.currentUserModel.getUserid());
            System.out.println("inside updatefollowerg");
        }
        else
        {
            ArrayList<String> oldIds = new ArrayList<String>();

            try {

                JSONObject jsonObjectTMp = new JSONObject(jsonDBResult);

                JSONArray jsonArrayTmp;

                try {

                    jsonArrayTmp = new JSONArray(jsonObjectTMp
                            .getString("ids"));

                    for (int i = 0; i < jsonArrayTmp.length(); i++) {

                        oldIds.add(jsonArrayTmp.getString(i));

                        System.out.println("AllFollowers ID Store in Local Database ="+oldIds);

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }

            ArrayList<String> tmpIds =  diff(oldIds,MainSingleTon.listMyfollowersIDs);//find diffrence between store old id's and new fetch id

            System.out.println("tempIDs"+tmpIds);
            System.out.println("tempIDssdf"+tmpIds.size());


            if(tmpIds.size()>0)
            {

                String  data=tboardproLocalData.getUnFollowersIDs(MainSingleTon.currentUserModel.getUserid());
                System.out.println("kjniunjmui "+data);

                if(data==null)
                {
                    System.out.println("jhnvbubujyhbg ");
                    tboardproLocalData.insertUnFollwersIds(tmpIds,MainSingleTon.currentUserModel.getUserid());

                    MainSingleTon.recentsUnFollowersCount = tmpIds.size();
                    tboardproLocalData.updateFollowersTableDataTemp(MainSingleTon.currentUserModel.getUserid(),
                            tboardproLocalData.KEY_followers_ids,jsonResult);
                    System.out.println("adding data = "+data);
                }
                else if(data.length()==2)
                {
                    tboardproLocalData.updateUnFollowersTableData(MainSingleTon.currentUserModel.getUserid(),tboardproLocalData.KEY_unfollowers_ids,tmpIds);
                    System.out.println("dafsdfdsfsdfs"+tmpIds);

                    tboardproLocalData.updateFollowersTableDataTemp(MainSingleTon.currentUserModel.getUserid(),
                            tboardproLocalData.KEY_followers_ids,jsonResult);
                }
                else
                {
                    ArrayList<String> unfollowarraydata = new ArrayList<String>();
                    try {

                        System.out.println("inside else part dfdfsdf ");

                        JSONArray jsonArrayTmp = new JSONArray(tboardproLocalData.getUnFollowersIDs(MainSingleTon.currentUserModel
                                .getUserid()));
                        System.out.println("inside else part  "+jsonArrayTmp);

                        {
                            for (int i = 0; i < jsonArrayTmp.length(); i++) {

                                unfollowarraydata.add(jsonArrayTmp.getString(i));

                                System.out.println("inside for loop");
                            }

                            System.out.println("unfollower data size=="+unfollowarraydata.size());
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                    if(unfollowarraydata.size()>0)
                    {

                        ArrayList<String> storeID=unfollowarraydata;
                        System.out.println("unfollower data size=="+unfollowarraydata.size());
                        for(int i=0;i<tmpIds.size();i++)
                        {
                            int k=0;
                            for(int j=0;j<unfollowarraydata.size();j++)
                            {
                                if((tmpIds.get(i).equals(unfollowarraydata.get(j))))
                                {
                                    k++;
                                }
                            }

                            if(k==0)
                            {
                                storeID.add(tmpIds.get(i));
                                System.out.println("store in storeID"+storeID);
                            }
                        }

                        System.out.println("unfollower id store in unfollwer table is =="+storeID);
                        tboardproLocalData.updateUnFollowersTableData(MainSingleTon.currentUserModel.getUserid(),tboardproLocalData.KEY_unfollowers_ids,storeID);

                        MainSingleTon.recentsUnFollowersCount = storeID.size();
                        tboardproLocalData.updateFollowersTableDataTemp(MainSingleTon.currentUserModel.getUserid(),
                                tboardproLocalData.KEY_followers_ids,jsonResult);
                    }
                }
            }else {
                System.out.println("temp id is Zero");
            }
        }

        try {

            JSONObject jsonObjectTMp = new JSONObject(jsonDBResult);

            JSONArray jsonArrayTmp;

            try {

                jsonArrayTmp = new JSONArray(jsonObjectTMp
                        .getString("ids"));

                for (int i = 0; i < jsonArrayTmp.length(); i++) {

                    unfollowtemptableid.add(jsonArrayTmp.getString(i));

                    System.out.println("AllFollowers ID Store in unfollwertemp Table ="+unfollowtemptableid);

                }

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }


        if(MainSingleTon.listMyfollowersIDs.size()>unfollowtemptableid.size())//here cheack new user follow me and then if new userId present in unfollower_table then remove userid from unfollower table
        {
            System.out.println("new folldgfdower="+MainSingleTon.listMyfollowersIDs);
            System.out.println("new folldgfdowedfsdfr="+unfollowtemptableid);
            ArrayList<String> newfollower = comdiff(MainSingleTon.listMyfollowersIDs,unfollowtemptableid);//find new follower by comparating follower store in follower_temp table
            System.out.println("new follower="+newfollower);
            if(newfollower.size()>0)
            {

                System.out.println("new follower found");
                ArrayList<String> unfollowerId = new ArrayList<>();

                if(tboardproLocalData.getUnFollowersIDs(MainSingleTon.currentUserModel.getUserid())==null)
                {
                    System.out.println("unfollower_table is null");
                    tboardproLocalData.updateFollowersTableDataTemp(MainSingleTon.currentUserModel.getUserid(), tboardproLocalData.KEY_followers_ids,jsonResult);
                    System.out.println("new follower update into follower_temp Table---sdfsdff");
                }else {
                    try {

                        JSONArray jsonArrayTmp = new JSONArray(tboardproLocalData.getUnFollowersIDs(MainSingleTon.currentUserModel
                                .getUserid()));

                        for (int i = 0; i < jsonArrayTmp.length(); i++) {

                            unfollowerId.add(jsonArrayTmp.getString(i));

                        }

                        System.out.println("unfollow user=="+unfollowerId);

                        MainSingleTon.recentsUnFollowersCount = unfollowerId.size();

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }


                    ArrayList<String> freshdata = common(newfollower,unfollowerId);


                    tboardproLocalData.updateUnFollowersTableData(MainSingleTon.currentUserModel.getUserid(),tboardproLocalData.KEY_unfollowers_ids,freshdata);
                    System.out.println("new follower ");

                    MainSingleTon.recentsUnFollowersCount = freshdata.size();

                    tboardproLocalData.updateFollowersTableDataTemp(MainSingleTon.currentUserModel.getUserid(), tboardproLocalData.KEY_followers_ids,jsonResult);
                    System.out.println("new follower update into follower_temp Table");

                }


            }
            else {
                System.out.println("no new userid found in existing table_unfollower");

                tboardproLocalData.updateFollowersTableDataTemp(MainSingleTon.currentUserModel.getUserid(), tboardproLocalData.KEY_followers_ids,jsonResult);
                System.out.println("new follower update into follower_temp Table");
            }
        }
        else
        {
            System.out.println("no new follower is added");
        }


        fetchUnfollowerUserFromUnfollowerTable();
    }

    public ArrayList<String>common(ArrayList<String> a, ArrayList<String>b)
    {

        System.out.println("a===="+a);
        System.out.println("b===="+b);
        ArrayList<String> intersection = new ArrayList(a);
        intersection.retainAll(b);
        System.out.println("intersection="+intersection);
        b.removeAll(intersection);
        System.out.println("b===+_="+b);
        return b;
    }
    public ArrayList<String> comdiff(ArrayList<String> a, ArrayList<String> b)
    {
        // difference a-b

        ArrayList<String> intersection = new ArrayList(a);
        intersection.retainAll(b);
        System.out.println("intersection=gdgdg"+intersection);

        a.removeAll(intersection);

        System.out.println("value of new add users"+a);
        for (String n : a) {
            System.out.println(n);

        }

        System.out.println("c==+sfdf+"+a);

        return a;
    }

    public ArrayList<String> diff(ArrayList<String> a, ArrayList<String> b)
    {
        // difference a-b

        ArrayList<String> intersection = new ArrayList(a);
        intersection.retainAll(b);
        System.out.println("intersection="+intersection);

        a.removeAll(intersection);

        System.out.println("value of new add users"+a);
        for (String n : a) {
            System.out.println(n);

        }

        System.out.println("c==++"+a);

        return a;
    }

    //////////////////////////////////////////////////////////////////////

    public void  fetchUnfollowerUserFromUnfollowerTable()
    {
                ArrayList<String> unfollowerIds = new ArrayList<String>();

        String unfollowerlist = tboardproLocalData.getUnFollowersIDs(MainSingleTon.currentUserModel.getUserid());
        if (unfollowerlist != null) {
            try {

                JSONArray jsonArrayTmp = new JSONArray(tboardproLocalData.getUnFollowersIDs(MainSingleTon.currentUserModel
                        .getUserid()));


                for (int i = 0; i < jsonArrayTmp.length(); i++) {

                    unfollowerIds.add(jsonArrayTmp.getString(i));

                }
                System.out.println("unfollowerIds==" + unfollowerIds);


            } catch (JSONException e) {

                e.printStackTrace();
            }

            rescentsIds = unfollowerIds;

            MainSingleTon.recentsUnFollowersCount = unfollowerIds.size();

            if (rescentsIds.size() == 0) {

                System.out.println("No Recent Unfollower");

                cancelProgres();

            } else {

                System.out.println("MainSingleTon.recentsUnFollowersCount==" + rescentsIds.size());

                rescentUnFollowers();
            }
        } else {
            System.out.println("Unfollower list is empty");
            cancelProgres();
        }

    }

}
