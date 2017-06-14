package com.socioboard.t_board_pro.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.socioboard.t_board_pro.twitterapi.TwitterRequestCallBack;
import com.socioboard.t_board_pro.twitterapi.TwitterTimeLineRequest2;
import com.socioboard.t_board_pro.twitterapi.TwitterUserGETRequest;
import com.socioboard.t_board_pro.util.Const;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.tboardpro.R;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCompareList extends Fragment {
private static final String check="OAuth oauth_consumer_key=\"DC0sePOBbQ8bYdC8r4Smg\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\""+System.currentTimeMillis()+"\",oauth_nonce=\"-113187475\",oauth_version=\"1.0\",oauth_token=\"827103335199150081-dDDwqULD7h6PzLL5fNgbYSTUrosnx5v\",oauth_signature=\"rgrjZaogTUsucZbGFFiMJLFjKcE%3D\"";

    public FragmentCompareList() {
        // Required empty public constructor
    }
 View view;
 Button button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_fragment_compare_list, container, false);
        button=(Button)view.findViewById(R.id.compare_list);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onCompare(MainSingleTon.twitter_url_compare_url);

               //new ToFollowing().execute();



            }
        });

        return view;
    }
 private void onCompare(final String URL)
 {
     RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
     JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
             URL, null,
             new Response.Listener<JSONObject>() {

                 @Override
                 public void onResponse(JSONObject response) {
                     System.out.println("Json object response" + response);
                 }
             },
             new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                     Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                     Log.d("Error", "......Response Error ..... " + error.toString());

                 }
             }
     ) {

         @Override
         public Map<String, String> getHeaders() throws AuthFailureError {
             Map<String, String> params = new HashMap<String, String>();

             System.out.println("l,lmlml   "+check);
             params.put("Authorization", check);
             params.put("Host", "api.twitter.com");
             params.put("X-Target-URI", "https://api.twitter.com");
             params.put("Connection", "Keep-Alive");
             params.put("Content-Type", "application/json; charset=utf-8");
             return params;
         }

     };
     requestQueue.add(jsonObjReq);

 }

    class ToFollowing extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
              /*  TwitterTimeLineRequest2 twitterTimeLineRequest = new TwitterTimeLineRequest2(MainSingleTon.currentUserModel, new TwitterRequestCallBack() {

                    @Override
                    public void onSuccess(String jsonResult) {
                        System.out.println("onSuccess jsonResult " + jsonResult);
                        //  parseJsonResult(jsonResult);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        System.out.println("onFailure e " + e);
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        System.out.println("onSuccess JSONObject " + jsonObject);
                    }

                });

                twitterTimeLineRequest.doInBackground(MainSingleTon.twitter_url_compare_url);

                return null;*/

                TwitterUserGETRequest twitterUserGETRequest = new TwitterUserGETRequest(
                        MainSingleTon.currentUserModel,
                        new TwitterRequestCallBack() {

                            @Override
                            public void onSuccess(String jsonResult) {

                                System.out.println("onSuccess jsonResult " + jsonResult);


                            }

                            @Override
                            public void onFailure(Exception e) {

                                System.out.println("onFailure e " + e);


                            }

                            @Override
                            public void onSuccess(JSONObject jsonObject) {

                                System.out.println("onSuccess JSONObject " + jsonObject);

                            }

                        });

                String userswithComma = "";

             

                System.out.println("Sizes ovlpFollwers.size() " );

                System.out.println("Sizes mutalFolloersIds.size() " );



                    List<BasicNameValuePair> peramPairs = new ArrayList<BasicNameValuePair>();

                    peramPairs.add(new BasicNameValuePair(Const.user_id, userswithComma));

                    peramPairs.add(new BasicNameValuePair(Const.include_entities,
                            "false"));

                    twitterUserGETRequest.executeThisRequest(
                            MainSingleTon.userShowIds, peramPairs);



                return null;



            }
        }


}
