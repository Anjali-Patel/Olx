package gss.com.bsell;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

public class SelectionScreen extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_NUMBERS_PERMISSION = 2;
    private static final String TAG = "LocationData";
    TextView emailLogin, PhoneLogin;
    ImageView GoogleLogin, FacebookLogin, img_background, iv_sim1, iv_sim2;
    SharedPreferenceUtils preferances;
    String mPhoneNumber, FCMToken;
    JSONObject json;
    String Userid;

    double lat, lon;
    TextView current_location;
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    LocationManager locationManager;
    boolean GpsStatus;

    private String url = CommonUtils.APP_URL+"user_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_selection_screen);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        preferances = SharedPreferenceUtils.getInstance(this);

        iv_sim1 = findViewById(R.id.iv_sim1);
        iv_sim2 = findViewById(R.id.iv_sim2);

        Userid = preferances.getStringValue(CommonUtils.USERID, "");
        FCMToken = preferances.getStringValue(CommonUtils.FCMTOCKEN, "");

        if (isOnline()) {
            checkAllNecessaryPermissions();
        } else {
            Toast.makeText(SelectionScreen.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

//         iv_sim1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (isOnline()) {
//                    startActivity(new Intent(SelectionScreen.this, AddPhoneNumber.class));
//                    finish();
//                }
//                else{
//                    Toast.makeText(SelectionScreen.this, "No Internet Connection", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//
//        iv_sim2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                requestForMobileNumberPermission();
//                checkLogin();
//
//            }
//        });

    }

    public void checkAllNecessaryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (
                (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                        (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) ||
                        (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                        (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                        (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},111);
        } else if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            gpsMethodDialog();
        } else {
            getLocation();
            if (Userid != "") {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(SelectionScreen.this, AddPhoneNumber.class));
                finish();
            }
        }
    }

//    private void checkPermissionForSDK() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            int Permission1 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.CAMERA);
//            Log.e("LogPermission", Permission1 + "/m");
//            int Permission2 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.CALL_PHONE);
//            Log.e("LogPermission", Permission2 + "/m");
//            int Permission3 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION);
//            Log.e("LogPermission", Permission3 + "/m");
//            int Permission4 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.ACCESS_FINE_LOCATION);
//            Log.e("LogPermission", Permission4 + "/m");
//            int Permission5 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.READ_PHONE_STATE);
//            Log.e("LogPermission", Permission5 + "/m");
//            int Permission6 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            Log.e("LogPermission", Permission6 + "/m");
//            int Permission7 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE);
//            Log.e("LogPermission", Permission7 + "/m");
//            ArrayList<String> list = new ArrayList<>();
//
//            if (Permission1 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.CAMERA);
//            }
//            if (Permission2 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.CALL_PHONE);
//            }
//            if (Permission3 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
//            if (Permission4 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
//            if (Permission5 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.READ_PHONE_STATE);
//            }
//            if (Permission6 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            }
//            if (Permission7 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//            }
//
//            if (list.size() > 0) {
//                ActivityCompat.requestPermissions(SelectionScreen.this, list.toArray(new String[list.size()]), Constants.MULTI_PERMISSION);
//            }else {
//                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                    gpsMethodDialog();
//                }else {
//                    getLocation();
//                    if (Userid != "") {
//                        startActivity(new Intent(this, MainActivity.class));
//                        finish();
//
//                    } else {
//                        startActivity(new Intent(SelectionScreen.this, AddPhoneNumber.class));
//                        finish();
//                    }
//
//                }
//            }
//        }
//    }

//    void checkGPSLocation() {
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, "Please Enable GPS to High Accuracy", Toast.LENGTH_LONG).show();
//
//
//            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivityForResult(intent, 147);
//        }
//        else{
//            getLocation();
//
//            if (Userid != "") {
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//
//            } else {
//                startActivity(new Intent(SelectionScreen.this, AddPhoneNumber.class));
//                finish();
//            }
//
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 147) {
//            Toast.makeText(this, "Please Enable GPS to High Accuracy", Toast.LENGTH_LONG).show();
//            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivityForResult(intent, 147);
//            } else {
//                getLocation();
//
//                if (Userid != "") {
//                    startActivity(new Intent(this, MainActivity.class));
//                    finish();
//
//                } else {
//                    startActivity(new Intent(SelectionScreen.this, AddPhoneNumber.class));
//                    finish();
//                }
//            }
//
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                checkAllNecessaryPermissions();
                if (Userid != "") {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SelectionScreen.this, AddPhoneNumber.class));
                    finish();
                }
            } else {
                if (Userid != "") {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SelectionScreen.this, AddPhoneNumber.class));
                    finish();
                }
                Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gpsMethodDialog() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) ex;
                                resolvableApiException.startResolutionForResult(SelectionScreen.this, 2);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });
    }


    void getLocation() {
        try {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);

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
        locationManager.removeUpdates(this);
        Log.e("lat", "" + lat);
        Log.e("lon", "" + lon);

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            preferances.setValue(CommonUtils.CurrentLocation, addresses.get(0).getSubLocality());
            Log.e("Address : ", "" + addresses.get(0).getSubLocality());

        } catch (Exception e) {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }


//    public void checkLogin(){
//
//                   TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                     mPhoneNumber = tMgr.getLine1Number();
//                     String phone = mPhoneNumber;
//
//        if (phone == null){
//            startActivity(new Intent(this,AddPhoneNumber.class));
//            finish();
//        }
//        else if (phone.equalsIgnoreCase("")){
//            startActivity(new Intent(this,AddPhoneNumber.class));
//            finish();
//        }
//
//        else if (phone == ""){
//            startActivity(new Intent(this,AddPhoneNumber.class));
//            finish();
//        }
//        else{
//            LogUserIn();
//        }
//
//
//
//
//    }


    public void requestForMobileNumberPermission() {
        final String permissionreadNumber = Manifest.permission.READ_PHONE_STATE;

        if (ContextCompat.checkSelfPermission(SelectionScreen.this, permissionreadNumber) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SelectionScreen.this, permissionreadNumber)) {
                showPermissionRationaleDialog("Test", permissionreadNumber);
            } else {
                requestForPermission(permissionreadNumber);
            }
        } else {

        }
    }

    private void showPermissionRationaleDialog(final String message, final String permission) {
        SelectionScreen.this.requestForPermission(permission);
    }

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions(SelectionScreen.this, new String[]{permission}, REQUEST_NUMBERS_PERMISSION);
    }

    /*
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == REQUEST_NUMBERS_PERMISSION) {
                if (grantResults.length <= 0) {
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        gpsMethodDialog();
                    }
                } else {
                    if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        gpsMethodDialog();
                    }
                }
            }
        }
    */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int Permission1 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.CAMERA);
        int Permission2 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.CALL_PHONE);
        int Permission4 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int Permission5 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int Permission6 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.READ_PHONE_STATE);
        int Permission7 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int Permission8 = ActivityCompat.checkSelfPermission(SelectionScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (Permission1 == PackageManager.PERMISSION_GRANTED && Permission2 == PackageManager.PERMISSION_GRANTED && Permission4 == PackageManager.PERMISSION_GRANTED
                && Permission5 == PackageManager.PERMISSION_GRANTED && Permission6 == PackageManager.PERMISSION_GRANTED && Permission7 == PackageManager.PERMISSION_GRANTED &&
                Permission8 == PackageManager.PERMISSION_GRANTED) {
            checkAllNecessaryPermissions();
        }
    }

    public void GPSStatus() {
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void LogUserIn() {

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("mobile_phone", mPhoneNumber)
                .add("fcm_token", FCMToken)
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);

                            String success = json.getString("response");
                            String message = json.getString("message");
                            String id = json.getString("userid");

                            preferances.setValue(CommonUtils.ISREGISTERED, true);
                            preferances.setValue(CommonUtils.USERMOBILE, mPhoneNumber);
                            preferances.setValue(CommonUtils.USERID, id);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    class PostDemandAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            startActivity(new Intent(SelectionScreen.this, MainActivity.class));
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mobile_phone", mPhoneNumber));
                nameValuePairs.add(new BasicNameValuePair("fcm_token", FCMToken));

                Log.d("datap", nameValuePairs.toString());
                json = RestJsonClient.postAdv(url, nameValuePairs);

                String success = json.getString("response");
                String message = json.getString("message");
                String id = json.getString("userid");

                preferances.setValue(CommonUtils.ISREGISTERED, true);
                preferances.setValue(CommonUtils.USERMOBILE, mPhoneNumber);
                preferances.setValue(CommonUtils.USERID, id);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;

        }

    }


}
