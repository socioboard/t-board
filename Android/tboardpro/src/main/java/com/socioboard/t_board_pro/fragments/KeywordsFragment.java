package com.socioboard.t_board_pro.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.tboardpro.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeywordsFragment extends Fragment {

    public KeywordsFragment() {
        // Required empty public constructor
    }
    View view;
    TextView save;
    ImageView edit,cancelbtn;
    ListView list_user;
    FragmentKeywordLocation.KeywordAdapter keywordAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainSingleTon.mixpanelAPI.track("Fragment KeywordsFragment oncreate called");
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_keywords, container, false);
        init();

        return view;
    }
    void init()
    {
        save=(TextView) view.findViewById(R.id.save);
        edit=(ImageView) view.findViewById(R.id.add_keywords);
        list_user=(ListView) view.findViewById(R.id.keyword_list);
        cancelbtn=(ImageView) view.findViewById(R.id.cancelbtn);
        keywordAdapter=new FragmentKeywordLocation.KeywordAdapter(MainSingleTon.KeywordsDatas,getActivity(),false);
        list_user.setAdapter(keywordAdapter);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new FragmentKeywordLocation();
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_content, fragment).commit();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new FragmentKeywordLocation();
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_content, fragment).addToBackStack(null).commit();
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        if(MainSingleTon.KeywordsDatas.size()>0)
        {
            save.setVisibility(View.GONE);
            list_user.setVisibility(View.VISIBLE);
            keywordAdapter.notifyDataSetChanged();
        }
    }

}
