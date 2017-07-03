package com.socioboard.t_board_pro.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.adapters.TweetsAdapter;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TweetModel;
import com.socioboard.tboardpro.R;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHashKeywords extends Fragment implements TwitterRequestCallBack,AbsListView.OnScrollListener {


    public FragmentHashKeywords() {
        // Required empty public constructor
    }
    TweetsAdapter twtAdpr;
    View view;
    boolean isAlreadyScrolling = true;
    ViewGroup viewGroup;
    Handler handler=new Handler();
    TextView save;
    ImageView edit;
    ListView list_user;
    RelativeLayout reloutProgress;
    ArrayList<TweetModel> listTaggedTweets = new ArrayList<TweetModel>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_fragment_hash_keywords, container, false);
        init();

        return view;
    }
    void init()
    {
        listTaggedTweets.clear();
        save=(TextView) view.findViewById(R.id.save);
        edit=(ImageView) view.findViewById(R.id.edit);
        list_user=(ListView) view.findViewById(R.id.list_user);
        reloutProgress = (RelativeLayout) view.findViewById(R.id.reloutProgress);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new KeywordsFragment();
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_content, fragment).commit();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new KeywordsFragment();
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_content, fragment).addToBackStack(null).commit();
            }
        });
        addFooterView();
        View view = getActivity().getCurrentFocus();


        if (view != null) {

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }


       /* try {
        List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

        TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(MainSingleTon.currentUserModel, FragmentHashKeywords.this);

        peramPairs.add(new BasicNameValuePair(Const.count, "10"));

        peramPairs.add(new BasicNameValuePair(Const.include_entities, "false"));



            peramPairs.add(new BasicNameValuePair(Const.q,
                    URLEncoder.encode(MainSingleTon.KeywordsDatas.get(0).getKeyword(), "UTF-8")));
            twitterUserGETRequest.executeThisRequest(
                    MainSingleTon.tweetsSearch, peramPairs);

        } catch (Exception e) {

            e.printStackTrace();
        }*/

        executeKeyword();

        list_user.setOnScrollListener(FragmentHashKeywords.this);

    }

    public class FetchReqPaged extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String madMaxId = params[0].toString();

            String urlTimeline = MainSingleTon.tweetsSearch;

            TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
                    MainSingleTon.currentUserModel,
                    new TwitterRequestCallBack() {

                        @Override
                        public void onSuccess(String jsonResult) {
                            System.out.println("onSuccess jsonResult " + jsonResult);
                            parseJsonResultPaged(jsonResult);
                            viewGroup.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onFailure(Exception e) {

                            System.out.println("onFailure e " + e);

                            handler.post(new Runnable() {

                                @Override
                                public void run() {

                                    viewGroup.setVisibility(View.INVISIBLE);

                                }
                            });
                        }

                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            viewGroup.setVisibility(View.INVISIBLE);
                        }
                    });

            List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

            peramPairs.add(new BasicNameValuePair(Const.max_id, madMaxId));

            peramPairs.add(new BasicNameValuePair(Const.count, "10"));

            peramPairs.add(new BasicNameValuePair(Const.include_entities,
                    "false"));
            try {

                peramPairs.add(new BasicNameValuePair(Const.q, URLEncoder.encode(MainSingleTon.KeywordsDatas.get(0).getKeyword(), "UTF-8")));

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }

            twitterUserGETRequest.executeThisRequest(urlTimeline, peramPairs);

            return null;
        }

    }
    protected void parseJsonResult(String jsonResult) {

        System.out.println("parseJsonResult  FragmentHashTagSearch");

        try {

            JSONObject jsonObject = new JSONObject(jsonResult);

            JSONArray jsonArray = jsonObject.getJSONArray(Const.statuses);

            if (jsonArray.length() == 0) {

                cancelProgres();

            } else {

                //isAlreadyScrolling = false;

            }

            for (int i = 0; i < jsonArray.length(); ++i) {

                TweetModel tweetModel = new TweetModel();

                try {

                    JSONObject jsonObjectk2 = jsonArray.getJSONObject(i);

                    System.out.println("jsonObjectk2  " + jsonObjectk2);

                    tweetModel
                            .setTweeet_str(jsonObjectk2.getString(Const.text));

                    tweetModel.setIsfavourated(jsonObjectk2
                            .getBoolean(Const.favorited));

                    tweetModel.setRetweeted(jsonObjectk2
                            .getBoolean(Const.retweeted));

                    tweetModel.setTweetTime(jsonObjectk2
                            .getString(Const.created_at));

                    tweetModel.setFavCount(new Long(jsonObjectk2
                            .getString(Const.favorite_count)));

                    tweetModel.setRetweetCount(new Long(jsonObjectk2
                            .getString(Const.retweet_count)));

                    tweetModel.setTweetId(jsonObjectk2.getString(Const.id));

                    JSONObject jsonObject3 = jsonObjectk2
                            .getJSONObject(Const.user);

                    tweetModel.setUserImagerUrl(jsonObject3
                            .getString(Const.profile_image_url));

                    tweetModel.setUserName("@"
                            + jsonObject3.getString(Const.screen_name));

                    tweetModel.setFullName(jsonObject3.getString(Const.name));

                    tweetModel.setUserID(jsonObject3.getString(Const.id));

                    tweetModel.setFollowing(jsonObject3.getBoolean(Const.following));

                    if (jsonObjectk2.has("extended_entities")) {

                        JSONObject jsonObjectEntities = jsonObjectk2
                                .getJSONObject("extended_entities");

                        System.out.println("***** jsonObjectEntities  *****"
                                + jsonObjectEntities);

                        System.out
                                .println("***** jsonObjectk2.has(Const.media) *****");

                        JSONArray jsonArray2Media = jsonObjectEntities
                                .getJSONArray(Const.media);

                        System.out.println("***** jsonArray2Media *****");

                        JSONObject jsonObjectMedia = jsonArray2Media
                                .getJSONObject(0);

                        System.out.println("***** jsonObjectMedia *****"
                                + jsonObjectMedia);

                        tweetModel.setMediaImagerUrl(jsonObjectMedia
                                .getString(Const.media_url));

                    } else {

                        System.out
                                .println("***** Noooooo jsonObjectk2.has(Const.media) *****");

                        tweetModel.setMediaImagerUrl("");

                    }

                    listTaggedTweets.add(tweetModel);

                    System.out.println(tweetModel);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (FragmentHashKeywords.this.getActivity() != null) {
                    list_user.setVisibility(View.VISIBLE);
                    save.setVisibility(View.GONE);
                    twtAdpr = new TweetsAdapter(listTaggedTweets, getActivity());

                    list_user.setAdapter(twtAdpr);

                    cancelProgres();
                    System.out.println("list_user.setAdapter(twtAdpr);");

                }

            }
        });



    }
    private void addFooterView() {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        viewGroup = (ViewGroup) inflater.inflate(R.layout.progress_layout,
                list_user, false);

        list_user.addFooterView(viewGroup);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                viewGroup.setVisibility(View.INVISIBLE);

            }
        },3000);
        System.out.println("addFooterView++++++++++++++++++++++++++++++++++++++++++++++ DONt LOad");

    }

    protected void parseJsonResultPaged(String jsonResult) {
        viewGroup.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                viewGroup.setVisibility(View.INVISIBLE);

            }
        },5000);

        try {

            JSONObject jsonObject = new JSONObject(jsonResult);

            JSONArray jsonArray = jsonObject.getJSONArray(Const.statuses);

            for (int i = 0; i < jsonArray.length(); ++i) {

                final TweetModel tweetModel = new TweetModel();

                try {

                    JSONObject jsonObjectk2 = jsonArray.getJSONObject(i);

                    tweetModel
                            .setTweeet_str(jsonObjectk2.getString(Const.text));

                    tweetModel.setIsfavourated(jsonObjectk2
                            .getBoolean(Const.favorited));

                    tweetModel.setRetweeted(jsonObjectk2
                            .getBoolean(Const.retweeted));

                    tweetModel.setTweetTime(jsonObjectk2
                            .getString(Const.created_at));

                    tweetModel.setFavCount(new Long(jsonObjectk2
                            .getString(Const.favorite_count)));

                    tweetModel.setRetweetCount(new Long(jsonObjectk2
                            .getString(Const.retweet_count)));

                    tweetModel.setTweetId(jsonObjectk2.getString(Const.id));

                    JSONObject jsonObject3 = jsonObjectk2
                            .getJSONObject(Const.user);

                    tweetModel.setUserImagerUrl(jsonObject3
                            .getString(Const.profile_image_url));

                    tweetModel.setUserName("@"
                            + jsonObject3.getString(Const.screen_name));

                    tweetModel.setUserID(jsonObject3.getString(Const.id));

                    tweetModel.setFullName(jsonObject3.getString(Const.name));

                    tweetModel.setFollowing(jsonObject3
                            .getBoolean(Const.following));

                    if (jsonObjectk2.has("extended_entities")) {

                        JSONObject jsonObjectEntities = jsonObjectk2
                                .getJSONObject("extended_entities");

                        System.out.println("***** jsonObjectEntities  *****"
                                + jsonObjectEntities);

                        System.out
                                .println("***** jsonObjectk2.has(Const.media) *****");

                        JSONArray jsonArray2Media = jsonObjectEntities
                                .getJSONArray(Const.media);

                        System.out.println("***** jsonArray2Media *****");

                        JSONObject jsonObjectMedia = jsonArray2Media
                                .getJSONObject(0);

                        System.out.println("***** jsonObjectMedia *****"
                                + jsonObjectMedia);

                        tweetModel.setMediaImagerUrl(jsonObjectMedia.getString(Const.media_url));

                    } else {

                        System.out
                                .println("***** Noooooo jsonObjectk2.has(Const.media) *****");

                        tweetModel.setMediaImagerUrl("");

                    }

                    // listMyfollowers.add(tweetModel);

                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            if (FragmentHashKeywords.this.getActivity() != null) {

                                int listCount = list_user.getCount();

                                twtAdpr.tweetModels.add(tweetModel);

                                list_user.setScrollY(listCount);

                                twtAdpr.notifyDataSetChanged();

                            }

                        }
                    });

                    System.out.println(tweetModel);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {

            e.printStackTrace();

        }

        if (twtAdpr.getCount() == 0) {

        } else {

            //   isAlreadyScrolling = false;

        }

    }

    @Override
    public void onSuccess(String jsonResult) {
        // TODO Auto-generated method stub
        System.out.println("onSuccess jsonResult " + jsonResult);
        parseJsonResult(jsonResult);
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFailure(Exception e) {

        Toast.makeText(getActivity(), "Search Failed!", Toast.LENGTH_SHORT).show();
        System.out.println("onFailure e " + e);
        cancelProgres();

    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

		/* maybe add a padding */

        boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

        if (loadMore) {

            System.out.println("YESSSSSSSSSSSSS load MOOOOOOOOOREE");

            if (isAlreadyScrolling) {

                // DO NOTHING
                System.out.println("BUT isAlreadyScrolling ");

            } else {

                if (twtAdpr.getCount() < 15) {
                    return;
                }

                viewGroup.setVisibility(View.VISIBLE);

                isAlreadyScrolling = true;

                String madMaxId = ""
                        + twtAdpr.getItem(twtAdpr.getCount() - 1).getTweetId();

                System.out.println(twtAdpr.getItem(twtAdpr.getCount() - 1));

                new FetchReqPaged().execute(madMaxId);

            }

        } else {

            System.out.println("NOOOOOOOOO DONt LOad");

        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    void showProgress() {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                reloutProgress.setVisibility(View.VISIBLE);
            }

        });

    }

    void cancelProgres() {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                reloutProgress.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void executeKeyword()
    {
        if(MainSingleTon.KeywordsDatas.size()>0)
        {
            for (int i=0;i<MainSingleTon.KeywordsDatas.size();i++)
            {
                try {
                    List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();
                    TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(MainSingleTon.currentUserModel, FragmentHashKeywords.this);
                    peramPairs.add(new BasicNameValuePair(Const.count, "35"));
                    peramPairs.add(new BasicNameValuePair(Const.include_entities, "false"));
                    if(!MainSingleTon.KeywordsDatas.get(i).getFormatted_address().equalsIgnoreCase("Global")) {
                        peramPairs.add(new BasicNameValuePair("geocode", MainSingleTon.KeywordsDatas.get(i).getLat() + "," + MainSingleTon.KeywordsDatas.get(i).getLng() + ",200km"));
                    }
                    // peramPairs.add(new BasicNameValuePair("result_type","recent"));
                    peramPairs.add(new BasicNameValuePair(Const.q,URLEncoder.encode(MainSingleTon.KeywordsDatas.get(i).getKeyword(), "UTF-8")));
                    twitterUserGETRequest.executeThisRequest( MainSingleTon.tweetsSearch, peramPairs);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        }
    }
}
