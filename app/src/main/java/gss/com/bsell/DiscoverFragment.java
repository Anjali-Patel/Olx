package gss.com.bsell;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import gss.com.bsell.Adapter.DiscoverItemsAdapter;
import gss.com.bsell.Asynktask.DiscoverDataAsynk;
import gss.com.bsell.CheckNetworkSpeed.CheckInternetSpeed;
import gss.com.bsell.Model.AllCategoryModel;
import gss.com.bsell.Model.DiscoverItemsModel;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.webrequest.RestJsonClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class DiscoverFragment extends Fragment implements LocationListener{



    //    ListView listView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<DiscoverItemsModel> DiscoverItemsModelArrayList = new ArrayList<>();
    ArrayList<DiscoverItemsModel> DiscoverItemsModelArrayListTmp = new ArrayList<>();
    JSONObject json;
    RelativeLayout changeAddressCat;
    TextView toolbar_title, categories;
    TextView test;
    public static final int ACTIVITY_REQUEST_CODE = 111;
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private AdView mAdView;
    SharedPreferenceUtils preferances;

    String URL = "http://192.168.1.155/newweb/BSell/assets/upload/photos/30305714-40Penguins.jpg";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String userdemand = "0";
    String IsDelete = "0";


    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;
    private EditText et_Search;
    String SelectedFlat, SelectedWing;
    private ImageView TestImage;

    double lat, lon;
    TextView current_location;
    LocationManager locationManager;
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    private static final String TAG = "LocationData";


    public DiscoverFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_discover_fragment, container, false);

        preferances = SharedPreferenceUtils.getInstance(getActivity());


        progressBarHolder = (FrameLayout) v.findViewById(R.id.progressBarHolder);
        et_Search = v.findViewById(R.id.input_search);

        categories = v.findViewById(R.id.categories);
        mRecyclerView = v.findViewById(R.id.discover_listview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setItemViewCacheSize(20);
        // use a linear layout manager
        toolbar_title = v.findViewById(R.id.toolbar_title);
        changeAddressCat = v.findViewById(R.id.changeAddressCat);
        toolbar_title.setText(preferances.getStringValue(CommonUtils.CurrentLocation, ""));
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
        mAdapter = new DiscoverItemsAdapter(getActivity(), DiscoverItemsModelArrayList, userdemand, IsDelete);
        mRecyclerView.setAdapter(mAdapter);

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        getLocation();

        try {
            CheckInternetSpeed internet = new CheckInternetSpeed();
            int s = Integer.parseInt(internet.ConnectionQuality(getActivity()));
            if (s < 3){
                Toast.makeText(getActivity(), "Your internet speed is low", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){

        }


        String mylocation = preferances.getStringValue(CommonUtils.CurrentLocation, "");

//        if (mylocation.equalsIgnoreCase("")){
//            Double LatNow = Double.parseDouble(preferances.getStringValue(CommonUtils.LATTITUTE, ""));
//            Double LonNow = Double.parseDouble(preferances.getStringValue(CommonUtils.LONGITUDE, ""));
//            toolbar_title.setText(convertLatLongToAddress(LatNow, LonNow));
//        }
//        else{
//            toolbar_title.setText(mylocation);
//        }



        GetAllProduct();

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        requestPermissions();


        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String strText = s.toString().trim();
                if (strText.length() != 0) {
                    DiscoverItemsModelArrayListTmp.clear();
                    for (int i = 0; i < DiscoverItemsModelArrayList.size(); i++) {
                        if (DiscoverItemsModelArrayList.get(i).getDescription().toLowerCase().contains(strText.toLowerCase())) {
                            DiscoverItemsModelArrayListTmp.add(DiscoverItemsModelArrayList.get(i));
                        }
                    }
                    mAdapter = new DiscoverItemsAdapter(getActivity(), DiscoverItemsModelArrayListTmp, userdemand, IsDelete);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
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
                if (strText.length() != 0) {
                    DiscoverItemsModelArrayListTmp.clear();
                    for (int i = 0; i < DiscoverItemsModelArrayList.size(); i++) {
                        if (DiscoverItemsModelArrayList.get(i).getDescription().toLowerCase().contains(strText.toLowerCase())) {
                            DiscoverItemsModelArrayListTmp.add(DiscoverItemsModelArrayList.get(i));
                        }
                    }
                    mAdapter = new DiscoverItemsAdapter(getActivity(), DiscoverItemsModelArrayListTmp, userdemand, IsDelete);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter = new DiscoverItemsAdapter(getActivity(), DiscoverItemsModelArrayList, userdemand, IsDelete);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

            }
        });


        return v;
    }


    void getLocation() {
        try {

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        preferances.setValue(CommonUtils.LATTITUTE, location.getLatitude());
        preferances.setValue(CommonUtils.LONGITUDE, location.getLongitude());
        lat = location.getLatitude();
        lon = location.getLongitude();

        Log.e( "lat",""+lat);
        Log.e( "lon",""+lon);

        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            preferances.setValue(CommonUtils.CurrentLocation, addresses.get(0).getSubLocality());
            Log.e( "Address : ",""+addresses.get(0).getSubLocality());

            //lat = Double.parseDouble(preferances.getStringValue(CommonUtils.LATTITUTE, ""));
            //lon = Double.parseDouble(preferances.getStringValue(CommonUtils.LONGITUDE, ""));
            toolbar_title.setText(convertLatLongToAddress(lat, lon));

        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }




    public String convertLatLongToAddress(double lat, double lon) {
        Address obj = null;
        String add = null;

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            obj = addresses.get(0);

            add = obj.getSubLocality();

            Log.e("Address", add);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("QWERTY", e.getMessage());
        }

        return add;
    }


    public void getcontacts() {

        String Mylat = preferances.getStringValue(CommonUtils.LATTITUTE, "");
        String Mylon = preferances.getStringValue(CommonUtils.LONGITUDE, "");
        if (Mylat == "" && Mylon == "") {
            DiscoverItemsModelArrayList.clear();
            try {

                String url = (CommonUtils.APP_URL) + "products";

                json = new DiscoverDataAsynk().execute(url, "").get();
                String success = json.getString("success");
                if (success.equalsIgnoreCase("1")) {
                    JSONObject json2 = json.getJSONObject("data");
                    JSONArray jsonArray = json2.getJSONArray("products");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject newJson = jsonArray.getJSONObject(i);

                        DiscoverItemsModel model = new DiscoverItemsModel();
                        model.setImage(newJson.getString("product_image"));
                        model.setTitle(newJson.getString("product_name"));
                        model.setValue(newJson.getString("price"));
                        model.setProductId(newJson.getString("id"));
                        model.setProductSenderId(newJson.getString("userid"));
                        model.setCatagoryId(newJson.getString("category"));
                        model.setUserDemand(newJson.getString("user_demands"));
                        model.setMobile(newJson.getString("mobile"));
                        model.setUsername(newJson.getString("username"));

                        DiscoverItemsModelArrayList.add(model);
                        DiscoverItemsModelArrayListTmp.add(model);


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

        } else {
            double lat1 = Double.valueOf(Mylat);
            double lon1 = Double.valueOf(Mylon);


            DiscoverItemsModelArrayList.clear();
            try {

                String url = (CommonUtils.APP_URL) + "products";
                json = new DiscoverDataAsynk().execute(url, "").get();
                String success = json.getString("success");
                if (success.equalsIgnoreCase("1")) {
                    JSONObject json2 = json.getJSONObject("data");
                    JSONArray jsonArray = json2.getJSONArray("products");
                    for (int i = 0; i < jsonArray.length(); i++) {


                        JSONObject newJson = jsonArray.getJSONObject(i);

                        String ProductLat = newJson.getString("lat");
                        String ProductLon = newJson.getString("longitude");
//                        double lat2 = 20;
//                        double lon2 = 73;
                        double lat2 = Double.valueOf(ProductLat);
                        double lon2 = Double.valueOf(ProductLon);
                        double theta = lon1 - lon2;
                        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
                        dist = Math.acos(dist);
                        dist = Math.toDegrees(dist);
                        dist = dist * 60 * 1.1515;
                        dist = dist * 1.609344;
                        dist = Math.round(dist);

                        DiscoverItemsModel model = new DiscoverItemsModel();
                        model.setImage(newJson.getString("product_image"));
                        model.setTitle(newJson.getString("product_name"));
                        model.setValue(newJson.getString("price"));
                        model.setProductId(newJson.getString("id"));
                        model.setProductSenderId(newJson.getString("userid"));
                        model.setCatagoryId(newJson.getString("category"));
                        model.setUserDemand(newJson.getString("user_demands"));
                        model.setMobile(newJson.getString("mobile"));
                        model.setUsername(newJson.getString("username"));

                        model.setLon(dist + "");

                        DiscoverItemsModelArrayList.add(model);
                        DiscoverItemsModelArrayListTmp.add(model);


                    }
                    Collections.sort(
                            DiscoverItemsModelArrayList,
                            new Comparator<DiscoverItemsModel>() {
                                public int compare(DiscoverItemsModel lhs, DiscoverItemsModel rhs) {
                                    return lhs.getLon().compareTo(rhs.getLon());
                                }
                            }
                    );

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

    }


//    @Override
//    public void onActivityResult(int requestcode, int resultcode, Intent data) {
//        String Category = "";
//        String Name = "";
//        if (data != null)
//            if (requestcode == ACTIVITY_REQUEST_CODE) {
//                if (data.hasExtra("location"))
//                    Name = data.getStringExtra("location");
//
//                if (data.hasExtra("category"))
//                    Category = data.getStringExtra("category");
//
//                toolbar_title.setText(Name);
//                categories.setText("Category " + Category);
//                GetAllProduct();
//            }
//
//    }

    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale || shouldProvideRationale2) {

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
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


    public class DiscoverDataAsynk extends AsyncTask<String, String, JSONObject> {
        private JSONObject jsonResponse;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("location", params[1]));
//                jsonResponse = RestJsonClient.connect(params[0]);
                jsonResponse = RestJsonClient.post(params[0], nameValuePairs);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
            //mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            // mSwipeRefreshLayout.setRefreshing(false);
        }
    }



    public void GetAllProduct() {

        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);

        String url = (CommonUtils.APP_URL) + "products";

        String Mylat = preferances.getStringValue(CommonUtils.LATTITUTE, "");
        String Mylon = preferances.getStringValue(CommonUtils.LONGITUDE, "");

        if (Mylat == "" && Mylon == "") {

            DiscoverItemsModelArrayList.clear();

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("location", "")
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();



            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    final String myResponse = responseBody.string();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject json = null;
                            try {
                                json = new JSONObject(myResponse);

                                String success = json.getString("success");

                                if (success.equalsIgnoreCase("1")) {
                                    JSONObject json2 = json.getJSONObject("data");
                                    JSONArray jsonArray = json2.getJSONArray("products");
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject newJson = jsonArray.getJSONObject(i);

                                        if (newJson.getString("price").equalsIgnoreCase("Product Price")) {

                                        } else {
                                            DiscoverItemsModel model = new DiscoverItemsModel();
                                            model.setImage(newJson.getString("product_image"));
                                            model.setTitle(newJson.getString("product_name"));
                                            model.setValue(newJson.getString("price"));
                                            model.setProductId(newJson.getString("id"));
                                            model.setProductSenderId(newJson.getString("userid"));
                                            model.setCatagoryId(newJson.getString("category"));
                                            model.setUserDemand(newJson.getString("user_demands"));
                                            model.setMobile(newJson.getString("mobile"));
                                            model.setUsername(newJson.getString("username"));
                                            model.setSimilarProductId(newJson.getString("sm_product"));
                                            model.setProductCatIcon(newJson.getString("icon"));

                                            DiscoverItemsModelArrayList.add(model);
                                            DiscoverItemsModelArrayListTmp.add(model);


                                        }
                                    }
                                    outAnimation = new AlphaAnimation(1f, 0f);
                                    outAnimation.setDuration(200);
                                    progressBarHolder.setAnimation(outAnimation);
                                    progressBarHolder.setVisibility(View.GONE);

                                    mAdapter.notifyDataSetChanged();

                                }

                                else {

                                    outAnimation = new AlphaAnimation(1f, 0f);
                                    outAnimation.setDuration(200);
                                    progressBarHolder.setAnimation(outAnimation);
                                    progressBarHolder.setVisibility(View.GONE);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
        else {

            final double lat1 = Double.valueOf(Mylat);
            final double lon1 = Double.valueOf(Mylon);

            DiscoverItemsModelArrayList.clear();


            OkHttpClient client = new OkHttpClient();


            RequestBody formBody = new FormBody.Builder()
                    .add("location", "")
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();



            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    final String myResponse = responseBody.string();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject json = null;
                            try {
                                json = new JSONObject(myResponse);
                                String success = json.getString("success");

                                if (success.equalsIgnoreCase("1")) {
                                    JSONObject json2 = json.getJSONObject("data");
                                    JSONArray jsonArray = json2.getJSONArray("products");
                                    for (int i = 0; i < jsonArray.length(); i++) {


                                        JSONObject newJson = jsonArray.getJSONObject(i);

                                        if (newJson.getString("price").equalsIgnoreCase("Product Price")){

                                        }
                                        else {
                                            String ProductLat = newJson.getString("lat");
                                            String ProductLon = newJson.getString("longitude");
                                            double lat2 = Double.valueOf(ProductLat);
                                            double lon2 = Double.valueOf(ProductLon);
                                            double theta = lon1 - lon2;
                                            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
                                            dist = Math.acos(dist);
                                            dist = Math.toDegrees(dist);
                                            dist = dist * 60 * 1.1515;
                                            dist = dist * 1.609344;
                                            dist = Math.round(dist);

                                            DiscoverItemsModel model = new DiscoverItemsModel();
                                            model.setImage(newJson.getString("product_image"));
                                            model.setTitle(newJson.getString("product_name"));
                                            model.setValue(newJson.getString("price"));
                                            model.setProductId(newJson.getString("id"));
                                            model.setProductSenderId(newJson.getString("userid"));
                                            model.setCatagoryId(newJson.getString("category"));
                                            model.setUserDemand(newJson.getString("user_demands"));
                                            model.setMobile(newJson.getString("mobile"));
                                            model.setUsername(newJson.getString("username"));
                                            model.setSimilarProductId(newJson.getString("sm_product"));
                                            model.setProductCatIcon(newJson.getString("icon"));

                                            model.setLon(dist + "");

                                            DiscoverItemsModelArrayList.add(model);
                                            DiscoverItemsModelArrayListTmp.add(model);
                                        }

                                    }
//                                    Collections.sort(
//                                            DiscoverItemsModelArrayList,
//                                            new Comparator<DiscoverItemsModel>() {
//                                                public int compare(DiscoverItemsModel lhs, DiscoverItemsModel rhs) {
//                                                    return lhs.getLon().compareTo(rhs.getLon());
//                                                }
//                                            }
//                                    );

                                    outAnimation = new AlphaAnimation(1f, 0f);
                                    outAnimation.setDuration(200);
                                    progressBarHolder.setAnimation(outAnimation);
                                    progressBarHolder.setVisibility(View.GONE);

                                    mAdapter.notifyDataSetChanged();

                                }
                                else {

                                    outAnimation = new AlphaAnimation(1f, 0f);
                                    outAnimation.setDuration(200);
                                    progressBarHolder.setAnimation(outAnimation);
                                    progressBarHolder.setVisibility(View.GONE);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

        }

    }

}
