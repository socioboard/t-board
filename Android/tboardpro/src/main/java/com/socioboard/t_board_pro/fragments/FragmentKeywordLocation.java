package com.socioboard.t_board_pro.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.t_board_pro.util.AppLocationService;
import com.socioboard.t_board_pro.util.LocationModel;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.t_board_pro.util.TboardproLocalData;
import com.socioboard.tboardpro.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentKeywordLocation extends Fragment {
    AppLocationService appLocationService;
    TextView save;
    RelativeLayout location_fetch;
    public static TextView location;
    ImageView cancelbtn;
    ProgressBar progressBar1;
    ListView keyword_list;
    EditText editText1;
    public static KeywordAdapter keywordAdapter;

    public static TboardproLocalData tboardproLocalData;
    private static final String TAG="FragmentKeywordLocation";
    public FragmentKeywordLocation() {
        // Required empty public constructor
    }

    View view;
    Context ctx;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_keyword_location, container, false);
        ctx = getActivity();
        MainSingleTon.KeywordsDatas.clear();
        tboardproLocalData=new TboardproLocalData(ctx);
        location_fetch = (RelativeLayout) view.findViewById(R.id.location_fetch);
        location=(TextView)view.findViewById(R.id.location);

        save=(TextView)view.findViewById(R.id.save);
        keyword_list=(ListView)view.findViewById(R.id.keyword_list);
        editText1=(EditText)view.findViewById(R.id.editText1);
        progressBar1=(ProgressBar)view.findViewById(R.id.progressBar1);
        location_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new LocationFragment();
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_content, fragment).commit();
            }
        });
        cancelbtn = (ImageView) view.findViewById(R.id.cancelbtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        tboardproLocalData.getKeywordsList(MainSingleTon.currentUserModel.getUserid());
        keywordAdapter=new KeywordAdapter(MainSingleTon.KeywordsDatas,ctx,true);
        keyword_list.setAdapter(keywordAdapter);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editText1.getText().toString().trim().isEmpty()) {
                    progressBar1.setVisibility(View.VISIBLE);
                    LocationModel locationModel = new LocationModel();
                    locationModel.setUserId(MainSingleTon.currentUserModel.getUserid());
                    locationModel.setKeyword(editText1.getText().toString().trim());
                    if (MainSingleTon.location_current.trim().equalsIgnoreCase("Global")) {
                        locationModel.setLat(35.981262);
                        locationModel.setLng(-115.0980413);
                    }
                    else {
                        locationModel.setLat(MainSingleTon.locationModels.get(0).getLat());
                        locationModel.setLng(MainSingleTon.locationModels.get(0).getLng());
                    }
                    locationModel.setFormatted_address(MainSingleTon.location_current);
                    tboardproLocalData.addKeywordsList(locationModel);
                    //  tboardproLocalData.getKeywordsList(MainSingleTon.currentUserModel.getUserid());

                    System.out.println("add Successfully");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            keywordAdapter.notifyDataSetChanged();
                            progressBar1.setVisibility(View.GONE);
                            editText1.setText("");
                        }
                    }, 350);

                }else {
                    Toast.makeText(getActivity(), "Please enter keyword", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (MainSingleTon.KeywordsDatas.size()>0)
        {
            keywordAdapter.notifyDataSetChanged();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(MainSingleTon.location_current.length()>2)
                    location.setText(MainSingleTon.location_current);
            }
        }, 300);


        return view;
    }



    public static class KeywordAdapter extends BaseAdapter {

        private Context context;

        boolean status;
        private ArrayList<LocationModel> navDrawerItems;


        public KeywordAdapter(ArrayList<LocationModel> navDrawerItems, Context mainActivity,boolean status) {
            this.context = mainActivity;
            this.navDrawerItems = navDrawerItems;
            this.status=status;
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = mInflater.inflate(R.layout.keywords_item_xml, parent, false);

            }
            final LocationModel locationModel=navDrawerItems.get(position);
            ImageView delete_item = (ImageView) convertView.findViewById(R.id.delete_icon);
            TextView keywords = (TextView) convertView.findViewById(R.id.keyword);
            TextView location = (TextView) convertView.findViewById(R.id.location);
            if(status)
            {
                delete_item.setVisibility(View.VISIBLE);
            }else {
                delete_item.setVisibility(View.GONE);
            }
            keywords.setText(locationModel.getKeyword());
            location.setText(locationModel.getFormatted_address());
            delete_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean status=tboardproLocalData.deleteKeywordsListData(locationModel.getKeyword(),locationModel.getUserId());
                    if(status)
                    {
                        tboardproLocalData.getKeywordsList(locationModel.getUserId());
                        keywordAdapter.notifyDataSetChanged();
                    }
                }
            });

            return convertView;
        }

    }






}
