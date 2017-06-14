package com.socioboard.t_board_pro.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socioboard.t_board_pro.ui.Items;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.t_board_pro.util.Utils;
import com.socioboard.tboardpro.R;

import java.util.ArrayList;

public class DrawerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Items> navDrawerItems;
    TboardproLocalData tboardproLocalData;
    String follower;
    long following;

    public DrawerAdapter(Context context, ArrayList<Items> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        tboardproLocalData = new TboardproLocalData(context);
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView text;

        TextView item_count;

        following = MainSingleTon.myfollowersCount;


        if (position == 0) {

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item_profile,
                    parent, false);

        } else {

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, parent,
                    false);

        }

        ProgressBar progressBar = (ProgressBar) convertView
                .findViewById(R.id.progressBar1);

        ImageView image = (ImageView) convertView.findViewById(R.id.item_icon);

        text = (TextView) convertView.findViewById(R.id.item_text);

        text.setText(navDrawerItems.get(position).getTitle());

        item_count = (TextView) convertView.findViewById(R.id.item_count);

        if (position == 0) {

            String stringBtmp = MainSingleTon.currentUserModel.getUserimage();

            Bitmap bitmap = null;

            if (stringBtmp != null) {

                bitmap = Utils.decodeBase64(stringBtmp);

                image.setImageBitmap(bitmap);

            }

            text.setText("@" + MainSingleTon.currentUserModel.getUsername());

            TextView textFullName = (TextView) convertView
                    .findViewById(R.id.textView1);

            textFullName.setText(MainSingleTon.fullUserDetailModel
                    .getFullName());

        } else {

            image.setImageResource(navDrawerItems.get(position).getIcon());

        }

        switch (position) {

            case 0:
                break;

            case 1:

                if (MainSingleTon.tweetsCount == -1) {

                    progressBar.setVisibility(View.VISIBLE);
                    item_count.setVisibility(View.INVISIBLE);

                } else {

                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.tweetsCount);
                }

                break;

            case 2:
                progressBar.setVisibility(View.INVISIBLE);
                item_count.setVisibility(View.INVISIBLE);
                break;

            case 3:
                if (MainSingleTon.followingCount == -1) {
                    progressBar.setVisibility(View.VISIBLE);
                    item_count.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.followingCount);
                }
                break;

            case 4:

                if (MainSingleTon.myfollowersCount == -1) {
                    progressBar.setVisibility(View.VISIBLE);
                    item_count.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.myfollowersCount);
                }
                break;

            case 5:

                if (MainSingleTon.recentsFollowersCount == -1) {
                    progressBar.setVisibility(View.VISIBLE);
                    item_count.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.recentsFollowersCount);
                }
                break;

            case 6:

                progressBar.setVisibility(View.INVISIBLE);
                item_count.setVisibility(View.INVISIBLE);
                item_count.setText("");
                break;
            case 7:

                progressBar.setVisibility(View.INVISIBLE);
                item_count.setVisibility(View.INVISIBLE);
                item_count.setText("");
                break;

            case 8:

                if (MainSingleTon.favoritesCount == -1) {
                    progressBar.setVisibility(View.VISIBLE);
                    item_count.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.favoritesCount);
                }
                break;

            case 9:

                progressBar.setVisibility(View.INVISIBLE);
                item_count.setVisibility(View.INVISIBLE);
                item_count.setText("");
                break;

            case 10:

                if (!MainSingleTon.secondaryCountLoaded) {
                    progressBar.setVisibility(View.VISIBLE);
                    item_count.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.fansIds.size());
                }

                break;

            case 11:
                if (!MainSingleTon.secondaryCountLoaded) {
                    progressBar.setVisibility(View.VISIBLE);
                    item_count.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.mutualsIds.size());
                }
                break;

            case 12:
                if (!MainSingleTon.secondaryCountLoaded) {
                    progressBar.setVisibility(View.VISIBLE);
                    item_count.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.nonFollowersIds.size());
                }
                break;

            case 13:
                progressBar.setVisibility(View.INVISIBLE);
                item_count.setVisibility(View.INVISIBLE);
                item_count.setText("");
                break;

            case 14:
                progressBar.setVisibility(View.INVISIBLE);
                item_count.setVisibility(View.VISIBLE);
                item_count.setText(""
                        + tboardproLocalData.getAllSchedulledTweet().size());

                break;

            case 15:
                progressBar.setVisibility(View.INVISIBLE);
                item_count.setVisibility(View.INVISIBLE);
                item_count.setText("");

                break;

            case 16:
                if (MainSingleTon.recentsUnFollowersCount > 0) {
                      progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.recentsUnFollowersCount);
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.INVISIBLE);
                }
                break;

            case 17:
                if (MainSingleTon.WhiteListdatas.size() > 0) {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.WhiteListdatas.size());


                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.INVISIBLE);
                }
                break;
            case 18:
                if (MainSingleTon.BlackListdatas.size() > 0) {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.VISIBLE);
                    item_count.setText("" + MainSingleTon.BlackListdatas.size());


                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    item_count.setVisibility(View.INVISIBLE);
                }
                break;
            case 19:
                /*if (MainSingleTon.BlackListdatas.size()>0) {
					progressBar.setVisibility(View.INVISIBLE);
					item_count.setVisibility(View.VISIBLE);
					item_count.setText("" + MainSingleTon.BlackListdatas.size());


				} else {*/
                progressBar.setVisibility(View.INVISIBLE);
                item_count.setVisibility(View.INVISIBLE);
                //}
                break;

            default:

        }

        return convertView;
    }

}
