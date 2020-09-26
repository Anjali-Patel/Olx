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
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import gss.com.bsell.Asynktask.DiscoverDataAsynk;
import gss.com.bsell.Model.AllCategoryModel;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.webrequest.RestJsonClient;

public class AddressCategorySelectionActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "LocationData";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    ArrayList<AllCategoryModel> categoryList = new ArrayList<AllCategoryModel>();
    JSONObject json;
    TextView current_location;
    SharedPreferenceUtils preferances;
    Toolbar toolbar;

    LocationManager locationManager;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_category_selection);

        preferances = SharedPreferenceUtils.getInstance(this);
        mRecyclerView =  findViewById(R.id.cat_add_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(new GridLayoutManager(AddressCategorySelectionActivity.this, 3));
        mAdapter = new MoreCategoryAdapter(getApplicationContext(), categoryList);
        mRecyclerView.setAdapter(mAdapter);
        current_location = findViewById(R.id.current_location);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();
                startActivity(new Intent(AddressCategorySelectionActivity.this, MainActivity.class));
                finish();
//                Intent i = new Intent();
//
//                preferances.setValue(CommonUtils.LATTITUTE, lat);
//                preferances.setValue(CommonUtils.LONGITUDE, lon);
//
//                i.putExtra("location",convertLatLongToAddress(lat,lon));
//                setResult(RESULT_OK, i);
//
//                //preferances.setValue(CommonUtils.CurrentLocation, convertLatLongToAddress(lat,lon));
//
//                finish();
            }
        });


        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }


        setupToolBar();
        getCategory();


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName());
                Intent i = new Intent();
                i.putExtra("location",place.getName());
                setResult(RESULT_OK, i);
                preferances.setValue(CommonUtils.CurrentLocation, place.getName().toString());
                LatLng LocationLatLog = place.getLatLng();
//                double lat = LocationLatLog.latitude;
//                double log = LocationLatLog.longitude;
//                preferances.setValue(CommonUtils.LATTITUTE, lat);
//                preferances.setValue(CommonUtils.LONGITUDE, log);

                finish();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
            }
        });

//        try {
//            Intent intent =
//                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                            .build(this);
//            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//        } catch (GooglePlayServicesRepairableException e) {
//            // TODO: Handle the error.
//        } catch (GooglePlayServicesNotAvailableException e) {
//            // TODO: Handle the error.
//        }
    }


    void getLocation() {
        try {


            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);


        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlaceAutocomplete.getPlace(this, data);
//                Log.i(TAG, "Place: " + place.getName());
//            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                Status status = PlaceAutocomplete.getStatus(this, data);
//                // TODO: Handle the error.
//                Log.i(TAG, status.getStatusMessage());
//
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
    public void setupToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("BSell");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getCategory() {
        categoryList.clear();
        try {
            json = new DiscoverDataAsynk().execute().get();
            Log.e("MoreData",json.toString());
            String success = json.getString("success");
            if (success.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject newJson = jsonArray.getJSONObject(i);
                    AllCategoryModel model = new AllCategoryModel();
                    model.setImage(newJson.getString("icon"));
                    model.setDescription(newJson.getString("category_name"));
                    model.setId(newJson.getString("category_id"));
                    categoryList.add(model);
                }
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

               preferances.setValue(CommonUtils.LATTITUTE, location.getLatitude());
               preferances.setValue(CommonUtils.LONGITUDE, location.getLongitude());

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            preferances.setValue(CommonUtils.CurrentLocation, addresses.get(0).getSubLocality());

            Log.e( "lat",""+location.getLatitude());
            Log.e( "lon",""+location.getLongitude());
            Log.e( "Address",""+addresses.get(0).getSubLocality());
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }


    public String convertLatLongToAddress(double lat, double lon){
        String currentAddress = null;
        Address obj = null;
        String add = null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            obj = addresses.get(0);
            currentAddress = obj.getAddressLine(0);
            currentAddress = currentAddress + ", " + obj.getLocality();
            currentAddress = currentAddress + ", " + obj.getSubAdminArea();
            currentAddress = currentAddress + ", " + obj.getPostalCode();
            currentAddress = currentAddress + ", " + obj.getAdminArea();
            currentAddress = currentAddress + ", " + obj.getCountryName();
            currentAddress = currentAddress + ", " + obj.getSubLocality();

            Log.e("Address",obj.getSubLocality()+""+obj.getLocality()+"\n"+obj.getAdminArea());
//            add = obj.getLocality()+", "+obj.getSubLocality();
            add = obj.getSubLocality();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("QWERTY",e.getMessage());
        }

        return add;
    }

    public class MoreCategoryAdapter extends RecyclerView.Adapter<MoreCategoryAdapter.MyViewHolder> {
        private Context context1;
        private ArrayList<AllCategoryModel> discoverItemsArrayList;

        public MoreCategoryAdapter(Context context, ArrayList<AllCategoryModel> discoverItemsModelArrayList) {
            context1 = context;
            discoverItemsArrayList = discoverItemsModelArrayList;
        }

        public  class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView more_textView;
            public ImageView more_imageView;
            public CardView card_category;
            public MyViewHolder(View v) {
                super(v);
                more_textView = (TextView) v.findViewById(R.id.more_textView);
                more_imageView = (ImageView) v.findViewById(R.id.more_imageView);
                card_category = (CardView) v.findViewById(R.id.card_category);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_categoty_list, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final AllCategoryModel model = discoverItemsArrayList.get(position);
            holder.more_textView.setText(model.getDescription());



            String image = model.getImage();


            if (image.equalsIgnoreCase("")){
                Picasso.with(context1).load(R.mipmap.ic_launcher_round).placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round).into(holder.more_imageView);

            }
            else{
                Picasso.with(context1).load(CommonUtils.CATEGORY_ICON_URL+image).placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round).into(holder.more_imageView);


            }
            

            holder.card_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(AddressCategorySelectionActivity.this, CategoryWiseProducts.class);
                    i.putExtra("category_id",model.getId());
                    i.putExtra("category_name",model.getDescription());
                    startActivity(i);

                }
            });
        }

        @Override
        public int getItemCount() {
            return discoverItemsArrayList.size();
        }
    }
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale || shouldProvideRationale2) {

        } else {
            ActivityCompat.requestPermissions(AddressCategorySelectionActivity.this,
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
                String Url = (CommonUtils.APP_URL)+"get_categories";

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("category_id", ""));

                jsonResponse = RestJsonClient.post(Url, nameValuePairs);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

        }
    }
}
