package com.socioboard.t_board_pro.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.socioboard.t_board_pro.util.AppLocationService;
import com.socioboard.t_board_pro.util.LocationAddress;
import com.socioboard.t_board_pro.util.LocationModel;
import com.socioboard.t_board_pro.util.MainSingleTon;
import com.socioboard.tboardpro.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private static final String TAG="LocationFragment";
    EditText edsearchView1;
    Button current_location;
    ImageView cancelbtn,button1Search;
    ListView location_list;
    Context ctx;
    private static  String a;
    ArrayList<String> searcharrayList=new ArrayList<>();
    private static Location location;
    public LocationFragment() {
        // Required empty public constructor
    }
    AppLocationService appLocationService;
    ArrayAdapter arrayAdapter;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_location, container, false);
        ctx=getActivity();
        button1Search=(ImageView)view.findViewById(R.id.button1Search);
        current_location=(Button)view.findViewById(R.id.current_location);
        edsearchView1=(EditText)view.findViewById(R.id.edsearchView1);
        location_list=(ListView)view.findViewById(R.id.location_list);
        cancelbtn=(ImageView)view.findViewById(R.id.cancelbtn);

        arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, searcharrayList);
        location_list.setAdapter(arrayAdapter);
        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLocationFetch(true);
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        edsearchView1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                a=edsearchView1.getText().toString();
                a=a.trim();
                if(a.length()>0)
                {
                    System.out.println("  jhvhjv111"+a);
                    onLocationFetch(false);
                    location_list.setVisibility(View.VISIBLE);
                    current_location.setVisibility(View.GONE);
                }else if (searcharrayList.size()<=0)
                {
                    System.out.println("  jhvhjv2222"+a);
                    location_list.setVisibility(View.GONE);
                    current_location.setVisibility(View.VISIBLE);
                }
                else {
                    location_list.setVisibility(View.GONE);
                    current_location.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        location_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainSingleTon.current_location_id=i;

                // LocationModel  locationModel=new LocationModel();

                LocationAddress.getAddressFromLocation(MainSingleTon.locationModels.get(i).getLat(),MainSingleTon.locationModels.get(i).getLng(),getActivity(),new Handler());
                MainSingleTon.location_current=MainSingleTon.locationModels.get(i).getFormatted_address();
                getFragmentManager().popBackStackImmediate();
            }
        });



        return view;
    }
    private void onLocationFetch(boolean check) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET},10);
                return;
            }
            else {
                if(check)
                    locationFetch();
                else{
                    MyProfileRequest(a);
                }
            }
        }else {
            if (check)
                locationFetch();
            else {
                MyProfileRequest(a);
            }
        }

    }

    private void locationFetch() {

        appLocationService = new AppLocationService(getActivity());
        // fetch current lat & long from network
        Location nwLocation = appLocationService
                .getLocation(LocationManager.NETWORK_PROVIDER);
        if (nwLocation != null) {

            final double latitude = nwLocation.getLatitude();
            final double longitude = nwLocation.getLongitude();

            System.out.println("nknx   "+latitude+" "+longitude);

            System.out.println("njxnujsxhn "+latitude+" "+longitude);

            List<Address> addresses=getGeocoderAddress(ctx,latitude,longitude);

            int size=addresses.size();
            String addresses1=getAddressLine(ctx,latitude,longitude);
            final String getLocality=getLocality(ctx,latitude,longitude);
            System.out.println("nknk "+addresses+" \n"+addresses1+" \n"+getLocality+" "+size);
            // MainSingleTon.location_current=getLocality;
            MainSingleTon.location_current=LocationAddress.getAddressFromLocation(latitude,longitude,getActivity(),new Handler());

//            MainSingleTon.location_current=MainSingleTon.locationModels.get(0).getFormatted_address();

            if (MainSingleTon.location_current!=null)
                getFragmentManager().popBackStackImmediate();
            else {
                MainSingleTon.location_current=LocationAddress.getAddressFromLocation(latitude,longitude,getActivity(),new Handler());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().popBackStackImmediate();
                    }
                }, 300);

            }



        }else {
            showSettingsAlert("Location");
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println("mnkjnd onRequestPermissionsResult");

        if (requestCode==MY_PERMISSIONS_REQUEST_LOCATION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                appLocationService = new AppLocationService(getActivity());
                // fetch current lat & long from network
                Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
                location =nwLocation;
                if (nwLocation != null) {

                    double latitude = nwLocation.getLatitude();
                    double longitude = nwLocation.getLongitude();


                    System.out.println("njxnujsxhn "+latitude+" "+longitude);


                    List<Address> addresses=getGeocoderAddress(ctx,latitude,longitude);

                    String addresses1=getAddressLine(ctx,latitude,longitude);
                    String getLocality=getLocality(ctx,latitude,longitude);
                    int size=addresses.size();
                    Address address=addresses.get(1);
                    String aa=address.getCountryName();
                    aa+=" "+address.getAdminArea()+" "+address.getLocality();
                    System.out.println("nknk "+addresses+" \n"+addresses1+" \n"+getLocality+" "+size+" \n"+address);

                    System.out.println("bcjbv  "+aa);
                    MainSingleTon.location_current=getLocality;
                    getFragmentManager().popBackStackImmediate();

                } else {
                    showSettingsAlert("Location");
                }
            } else {
                Toast.makeText(getActivity(),"Permission not granted",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void showSettingsAlert(String provider)
    {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }


    public List<Address> getGeocoderAddress(Context context, double latitude, double longitude) {
        List<Address> addresses=null;



        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

        try {
            /**
             * Geocoder.getFromLocation - Returns an array of Addresses
             * that are known to describe the area immediately surrounding the given latitude and longitude.
             */
            addresses = geocoder.getFromLocation(latitude, longitude,10);

            //return addresses;
        } catch (IOException e) {
            //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to Geocoder", e);
        }
        // }

        return addresses;
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    public String getAddressLine(Context context,double latitude,double longitude) {
        List<Address> addresses = getGeocoderAddress(context,latitude, longitude);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        } else {
            return null;
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    public String getLocality(Context context,double latitude,double longitude) {

        List<Address> addresses = getGeocoderAddress(context,latitude,longitude);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        }
        else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    public String getPostalCode(Context context,double latitude,double longitude) {
        List<Address> addresses = getGeocoderAddress(context,latitude,longitude);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        } else {
            return null;
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    public String getCountryName(Context context,double latitude,double longitude) {
        List<Address> addresses = getGeocoderAddress(context,latitude,longitude);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }
    private void MyProfileRequest(final String keyword)
    {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://maps.google.com/maps/api/geocode/json?address="+keyword+"&sensor=false",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        searcharrayList.clear();
                        System.out.println("response" + response);
                        MainSingleTon.locationModels.clear();
                        try {
                            JSONObject json2 = new JSONObject(response);
                            JSONArray jsonArray = json2.getJSONArray("results");

                            System.out.println("nknkn v "+jsonArray);
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                System.out.println("mnkdnsvkn "+jsonObject);
                                searcharrayList.add(jsonObject.getString("formatted_address"));
                                JSONObject jsonArray1=jsonObject.getJSONObject("geometry");
                                JSONObject jsonObject1=jsonArray1.getJSONObject("location");
                                double v=jsonObject1.getDouble("lat");
                                double v1=jsonObject1.getDouble("lng");
                                LocationModel mLocation=new LocationModel();
                                mLocation.setLat(v);
                                mLocation.setLng(v1);
                                mLocation.setFormatted_address(jsonObject.getString("formatted_address"));
                                MainSingleTon.locationModels.add(mLocation);

                            }
                            arrayAdapter.notifyDataSetChanged();



                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }
}
