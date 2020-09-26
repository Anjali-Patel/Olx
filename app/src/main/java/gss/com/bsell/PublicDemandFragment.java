package gss.com.bsell;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import gss.com.bsell.Adapter.DiscoverItemsAdapter;
import gss.com.bsell.Model.DiscoverItemsModel;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.webrequest.RestJsonClient;

public class PublicDemandFragment extends Fragment {

    RelativeLayout relativeLayout;

    //    ListView listView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<DiscoverItemsModel> DiscoverItemsModelArrayList = new ArrayList<>();
    ArrayList<DiscoverItemsModel> DiscoverItemsModelArrayListTmp = new ArrayList<>();
    JSONObject json;
    RelativeLayout changeAddressCat;
    TextView toolbar_title,tvCategories;
    public static final int ACTIVITY_REQUEST_CODE = 111;
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private AdView mAdView;
    SharedPreferenceUtils preferances;
    private String userdemand="1";
    private MyViewPager viewPager;
    private EditText et_Search;
    String IsDelete = "0";

    public PublicDemandFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_public_demand, container, false);
//        AdView adView = new AdView(getActivity());

        preferances = SharedPreferenceUtils.getInstance(getActivity());
        viewPager = (MyViewPager) getActivity().findViewById(R.id.viewpager);

        et_Search=v.findViewById(R.id.input_search);
        tvCategories=v.findViewById(R.id.categories);
        mRecyclerView = v.findViewById(R.id.discover_listview);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        toolbar_title = v.findViewById(R.id.toolbar_title);
        changeAddressCat = v.findViewById(R.id.changeAddressCat);
        toolbar_title.setText(preferances.getStringValue(CommonUtils.CurrentLocation,""));
        changeAddressCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddressCategorySelectionActivity.class);
                startActivityForResult(i, ACTIVITY_REQUEST_CODE);
            }
        });
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DiscoverItemsAdapter(getActivity(), DiscoverItemsModelArrayList,userdemand, IsDelete);
        mRecyclerView.setAdapter(mAdapter);

        viewPager.getAdapter().notifyDataSetChanged();
        getcontacts();
        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        requestPermissions();

        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String strText = s.toString().trim();
                if(strText.length() != 0){
                    DiscoverItemsModelArrayListTmp.clear();
                    for(int i = 0; i<DiscoverItemsModelArrayList.size();i++){
                        if(DiscoverItemsModelArrayList.get(i).getDescription().toLowerCase().contains(strText.toLowerCase())){
                            DiscoverItemsModelArrayListTmp.add(DiscoverItemsModelArrayList.get(i));
                        }
                    }
                    mAdapter = new DiscoverItemsAdapter(getActivity(), DiscoverItemsModelArrayListTmp, userdemand, IsDelete);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }else{
                    mAdapter = new DiscoverItemsAdapter(getActivity(), DiscoverItemsModelArrayList, userdemand, IsDelete);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strText = s.toString().trim();
                if(strText.length() != 0){
                    DiscoverItemsModelArrayListTmp.clear();
                    for(int i = 0; i<DiscoverItemsModelArrayList.size();i++){
                        if(DiscoverItemsModelArrayList.get(i).getDescription().toLowerCase().contains(strText.toLowerCase())){
                            DiscoverItemsModelArrayListTmp.add(DiscoverItemsModelArrayList.get(i));
                        }
                    }
                    mAdapter = new DiscoverItemsAdapter(getActivity(), DiscoverItemsModelArrayListTmp, userdemand, IsDelete);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }else{
                    mAdapter = new DiscoverItemsAdapter(getActivity(), DiscoverItemsModelArrayList, userdemand, IsDelete);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

            }
        });

        return v;


    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    public void getcontacts() {
        //   http://demo1.geniesoftsystem.com/newweb/BSell/assets/upload/photos/
        DiscoverItemsModelArrayList.clear();
        try {
//            String data = "{\"success\":\"success\",\"data\":[{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/1.jpg\",\"description\":\"this is brand new sofa set\",\"values\":\"₹55\"},\n" +
//                    "            {\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/2.jpg\",\"description\":\"New car no damage no scratches\",\"values\":\"₹500\"},\n" +
//                    "            {\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/3.jpg\",\"description\":\"Old used car\",\"values\":\"₹4054\"},\n" +
//                    "            {\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/4.jpg\",\"description\":\"Used Mobile in best condition\",\"values\":\"₹200\"},\n" +
//                    "            {\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/1.jpg\",\"description\":\"Used Mobile in best condition\",\"values\":\"₹200\"},\n" +
//                    "            {\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/2.jpg\",\"description\":\"Used Mobile in best condition\",\"values\":\"₹200\"},\n" +
//                    "            {\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/5.jpg\",\"description\":\"house for rent\",\"values\":\"₹454\"}]}";
//            json = new JSONObject(data);
//            Log.e("OBJECT", json.toString());
//            String url = "http://demo1.geniesoftsystem.com/newweb/BSell/index.php/Api/discoverfragment";
//            String url = "http://demo1.geniesoftsystem.com/newweb/BSell/index.php/product/studio";

            json = new DiscoverDataDemandAsynk().execute(userdemand).get();
            String success = json.getString("success");
            if (success.equalsIgnoreCase("1")) {
                JSONArray jsonArray = null;
                jsonArray = json.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject newJson = jsonArray.getJSONObject(i);
                    DiscoverItemsModel model = new DiscoverItemsModel();
                    model.setImage(newJson.getString("product_image"));
                    model.setDescription(newJson.getString("description"));
                    model.setValue(newJson.getString("price"));
                    model.setLocation(newJson.getString("location"));
                    model.setProductId(newJson.getString("id"));
                    model.setTitle(newJson.getString("product_name"));


                    DiscoverItemsModelArrayList.add(model);
                }

                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        String Category="";
        String Name="";
        if (data != null)
            if (requestcode == ACTIVITY_REQUEST_CODE) {
                if(data.hasExtra("location"))
                    Name = data.getStringExtra("location");

                if(data.hasExtra("category"))
                    Category= data.getStringExtra("category");

                toolbar_title.setText(Name);
                tvCategories.setText("Category "+Category);

            }
    }
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale || shouldProvideRationale2) {

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {

            }
        }
    }


    public class DiscoverDataDemandAsynk extends AsyncTask<String, String, JSONObject> {
        private JSONObject jsonResponse;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String url= CommonUtils.APP_URL+"discoverfragment";
                List<NameValuePair> nameValuePairs=new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("userdemand",params[0]));
                json = RestJsonClient.post(url,nameValuePairs);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    public void refreshAdapter(){
        getcontacts();
    }



}
