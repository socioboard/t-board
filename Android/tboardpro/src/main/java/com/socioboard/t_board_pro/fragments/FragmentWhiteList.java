package com.socioboard.t_board_pro.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.lazylist.ImageLoader;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWhiteList extends Fragment {

    View view;
    ImageLoader imageLoader;
    ListView listView;
    TboardproLocalData tboardproLocalData;
    ActiveMyOrdersAdopter activeMyOrdersAdopter;
    public static RelativeLayout reloutProgress1;
    public FragmentWhiteList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_fragment_white_list, container, false);
        MainSingleTon.WhiteListdatas.clear();
        reloutProgress1 = (RelativeLayout) view.findViewById(R.id.reloutProgress);
        listView = (ListView) view.findViewById(R.id.listViewToFollowing);
        tboardproLocalData=new TboardproLocalData(getActivity());
        tboardproLocalData.getWhiteList(MainSingleTon.currentUserModel.getUserid());
        imageLoader =new ImageLoader(getActivity());

        System.out.println("......"+MainSingleTon.WhiteListdatas);
        reloutProgress1.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(MainSingleTon.WhiteListdatas.size()>0)
                {

                    activeMyOrdersAdopter=new ActiveMyOrdersAdopter(getActivity(),MainSingleTon.WhiteListdatas);
                    listView.setAdapter(activeMyOrdersAdopter);

                    activeMyOrdersAdopter.notifyDataSetChanged();
                    reloutProgress1.setVisibility(View.GONE);
                }else {
                    Toast.makeText(getActivity(), "No Item in White List", Toast.LENGTH_SHORT).show();
                    reloutProgress1.setVisibility(View.GONE);
                }
            }
        }, 250);

        return view;
    }
    public class ActiveMyOrdersAdopter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        Holder holder=new Holder();
        ArrayList<String> activeMyOrdersModels;

        ActiveMyOrdersAdopter(Context context, ArrayList<String>activeMyOrdersModels)
        {
            this.activeMyOrdersModels=activeMyOrdersModels;
            this.context=context;
        }

        @Override
        public int getCount() {
            return activeMyOrdersModels.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class Holder
        {

            ImageView profile_pic;
            TextView followerName;
            Button buttonRemove;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (inflater == null)
                inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.white_list_item_xml, null);
                holder.followerName=(TextView) convertView.findViewById(R.id.followerName);
                holder.profile_pic=(ImageView)convertView.findViewById(R.id.profile_pic);
              holder.buttonRemove=(Button) convertView.findViewById(R.id.buttonRemove);
             final String ac[]=MainSingleTon.WhiteListdatas.get(position).split(" ");
              System.out.println("FragmentWhiteList   "+ac+" "+MainSingleTon.WhiteListdatas.get(position));
              holder.followerName.setText(ac[0]);
             imageLoader.DisplayImage(ac[1],   holder.profile_pic);
            holder.buttonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reloutProgress1.setVisibility(View.VISIBLE);
                   boolean aaa= tboardproLocalData.deleteData(ac[0],MainSingleTon.currentUserModel.getUserid());
                    System.out.println(" Delete Respone "+aaa);

                    tboardproLocalData.getWhiteList(MainSingleTon.currentUserModel.getUserid());
                    activeMyOrdersAdopter.notifyDataSetChanged();
                    reloutProgress1.setVisibility(View.GONE);

                }
            });
            return convertView;
        }
    }
}
