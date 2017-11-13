package com.socioboard.t_board_pro.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.tboardpro.R;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FragmentFeedBack extends Fragment
{
    Button b;
    EditText e1,e2;
    View rooview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainSingleTon.mixpanelAPI.track("Fragment FeedBack oncreate called");

        System.out.println("dfsfsdf");
        rooview = inflater.inflate(R.layout.fragment_feedback, container, false);
        b=(Button)rooview.findViewById(R.id.button);
        e1=(EditText)rooview.findViewById(R.id.editText1);
        e2=(EditText)rooview.findViewById(R.id.editText2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/html");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"sumaiyabpatel@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback from App");
                i.putExtra(Intent.EXTRA_TEXT, "Name : "+e1.getText()+"\nMessage : "+e2.getText());
                try {
                    startActivity(Intent.createChooser(i, "Send feedback..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return null;
    }
}
