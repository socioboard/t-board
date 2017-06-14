package com.socioboard.t_board_pro.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.AnyUserProfileDialog;
import com.socioboard.t_board_pro.lazylist.ImageLoader;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.ToFollowingModel;
import com.socioboard.tboardpro.R;

import java.util.ArrayList;

/**
 * Created by GLB-122 on 6/9/2017.
 */

public class ToUnFollowingAdapter extends BaseAdapter
{
    public Context context;

    public ArrayList<ToFollowingModel> tweetModels;

    Activity activity;

    ImageLoader imageLoader;

    ProgressDialog progressDialog;

    TboardproLocalData tboardproLocalData;

    ArrayList<String> sendingids;

    ArrayList<String> sentIds;

    public ToUnFollowingAdapter(Context context,ArrayList<ToFollowingModel> tweetModels,Activity activity)
    {
        this.context = context;

        this.tweetModels = tweetModels;

        imageLoader = new ImageLoader(context);

        this.activity = activity;

        tboardproLocalData = new TboardproLocalData(context);

        sendingids = tboardproLocalData
                .getAllSendingIDs(MainSingleTon.currentUserModel.getUserid());

        sentIds = tboardproLocalData
                .getAllSentIDs(MainSingleTon.currentUserModel.getUserid());

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                progressDialog = new ProgressDialog(
                        ToUnFollowingAdapter.this.context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
            }
        });
    }

    @Override
    public int getCount() {return tweetModels.size();
    }

    @Override
    public Object getItem(int position) {
        return tweetModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.to_un_following_item,viewGroup,false);
        }
        final ToFollowingModel toFollowingModel = (ToFollowingModel) getItem(position);

        ImageView profilePic = (ImageView) convertView
                .findViewById(R.id.profile_pic);

        profilePic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String newUser = toFollowingModel.getUserName();

                AnyUserProfileDialog anyUserProfile = new AnyUserProfileDialog(
                        activity, newUser.replace("@", ""), toFollowingModel
                        .getId());

            }
        });

        TextView userName = (TextView) convertView
                .findViewById(R.id.followerName);

        userName.setText(toFollowingModel.getUserName());

        userName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String newUser = toFollowingModel.getUserName();

                AnyUserProfileDialog anyUserProfile = new AnyUserProfileDialog(
                        activity, newUser.replace("@", ""), toFollowingModel
                        .getId());

            }
        });

        TextView noOfTweets = (TextView) convertView
                .findViewById(R.id.textViewNotweets);

        noOfTweets.setText(toFollowingModel.getNoTweets());

        TextView noOfFollowings = (TextView) convertView
                .findViewById(R.id.textViewNoOfFollowings);

        noOfFollowings.setText(toFollowingModel.getNoToFollowing());

        TextView noOfFollowers = (TextView) convertView
                .findViewById(R.id.textViewNoofFollowers);

        noOfFollowers.setText(toFollowingModel.getNoFollowers());

//        final Button buttonFollow = (Button) convertView
//                .findViewById(R.id.buttonFollow);
//
//        final Button buttonUnfollow = (Button) convertView
//                .findViewById(R.id.buttonUnfollow);

//        if (toFollowingModel.isFollowingStatus()) {
//
//            buttonFollow.setVisibility(View.INVISIBLE);
//
//            buttonUnfollow.setVisibility(View.VISIBLE);
//
//        } else {
//
//            buttonFollow.setVisibility(View.VISIBLE);
//
//            buttonUnfollow.setVisibility(View.INVISIBLE);
//
//        }
//
//        if (MainSingleTon.currentUserModel.getUserid().contains(
//                toFollowingModel.getId())) {
//
//            buttonUnfollow.setVisibility(View.INVISIBLE);
//            buttonFollow.setVisibility(View.INVISIBLE);
//
//        }

        final int pos = position;

//        buttonFollow.setOnClickListener(new View.OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v) {
//
//                if (MainSingleTon.isNeedTOstopFollowing) {
//
//                    myToastS("You have Exceeded Follow Limit Today");
//
//                } else {
//
//                    myprint("buttonFollow " + getItem(pos));
//
//                    tweetModels.remove(pos);
//
//                    notifyDataSetChanged();
//
//                    TwitterPostRequestFollow twitterPostRequestFollow = new TwitterPostRequestFollow(
//                            MainSingleTon.currentUserModel,
//                            new TwitterRequestCallBack() {
//
//                                @Override
//                                public void onSuccess(JSONObject jsonObject) {
//
//                                }
//
//                                @Override
//                                public void onSuccess(String jsonResult) {
//
//                                    addDMStatus(toFollowingModel);
//
//                                    myprint("buttonFollow onSuccess");
//
//                                    ++MainSingleTon.followingCount;
//
//                                    MainSingleTon.toFollowingModelsIDs
//                                            .add(toFollowingModel.getId());
//
//                                    MainActivity.isNeedToRefreshDrawer = true;
//
//                                }
//
//                                @Override
//                                public void onFailure(Exception e) {
//
//                                    myprint("buttonFollow onFailure" + e);
//
//                                }
//
//                            });
//
//                    twitterPostRequestFollow.executeThisRequest(toFollowingModel.getId());
//
//                }
//            }
//        });
//
//        buttonUnfollow.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//
//                myprint("buttonUnfollow " + getItem(pos));
//
//                if (MainSingleTon.isNeedTOstopFollowing) {
//
//                    myToastS("You have Exceeded Follow Limit Today");
//
//                } else {
//
//                    tweetModels.remove(pos);
//
//                    notifyDataSetChanged();
//
//                    TwitterPostRequestUnFollow twitterPostRequestUnFollow = new TwitterPostRequestUnFollow(
//                            MainSingleTon.currentUserModel,
//                            new TwitterRequestCallBack() {
//
//                                @Override
//                                public void onSuccess(JSONObject jsonObject) {
//
//                                }
//
//                                @Override
//                                public void onSuccess(String jsonResult) {
//
//                                    myprint("buttonUnfollow onSuccess");
//
//                                    --MainSingleTon.followingCount;
//
//                                    MainSingleTon.toFollowingModelsIDs
//                                            .remove(toFollowingModel.getId());
//
//                                    MainActivity.isNeedToRefreshDrawer = true;
//
//                                }
//
//                                @Override
//                                public void onFailure(Exception e) {
//
//                                    myprint("buttonUnfollow onFailure" + e);
//                                }
//
//                            });
//
//                    twitterPostRequestUnFollow.executeThisRequest(toFollowingModel.getId());
//                }
//            }
//        });

        imageLoader.DisplayImage(toFollowingModel.getUserImagerUrl(),
                profilePic);

        return convertView;
    }

    public void myprint(Object msg) {

        System.out.println(msg.toString());

    }

    private void addDMStatus(final ToFollowingModel toFollowingModel)
    {

        if (MainSingleTon.autoDmfirstime.contains("yes")) {

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    new AlertDialog.Builder(activity)
                            .setTitle("Direct Message")
                            .setMessage(
                                    "A thanks message will be sent to those users. who are following you back!")
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {

                                            SharedPreferences.Editor editor = context
                                                    .getSharedPreferences(
                                                            "twtboardpro",
                                                            Context.MODE_PRIVATE)
                                                    .edit();

                                            editor.putString("autoDmfirstime",
                                                    "no");

                                            editor.putBoolean("autodm", true);

                                            MainSingleTon.autodm = true;

                                            MainSingleTon.autoDmfirstime = "no";

                                            editor.commit();

                                            addDMStatus(toFollowingModel);
                                        }
                                    })
                            .setNegativeButton(android.R.string.no,
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {

                                            SharedPreferences.Editor editor = context
                                                    .getSharedPreferences(
                                                            "twtboardpro",
                                                            Context.MODE_PRIVATE)
                                                    .edit();

                                            editor.putString("autoDmfirstime",
                                                    "no");

                                            editor.putBoolean("autodm", false);

                                            editor.commit();

                                            MainSingleTon.autoDmfirstime = "no";

                                            MainSingleTon.autodm = false;

                                        }

                                    }).setIcon(R.drawable.ic_launcher).show();
                }
            });

        } else {

            myprint("Not to diss[ay fvnefisdfnvko nvkjn");

            if (MainSingleTon.autodm) {

                if (sendingids.contains(toFollowingModel.getId())
                        || sentIds.contains(toFollowingModel.getId())) {

                } else {

                    tboardproLocalData.addNewDMsendingId(
                            toFollowingModel.getId(),
                            MainSingleTon.currentUserModel.getUserid());

                    sendingids.add(toFollowingModel.getId());

                }

            } else {

            }
        }
    }

    void myToastS(final String toastMsg)
    {

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
