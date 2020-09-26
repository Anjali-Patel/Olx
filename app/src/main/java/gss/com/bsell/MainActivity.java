package gss.com.bsell;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import lockscreenads.utils.MyService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static lockscreenads.ApplicationClass.KEY_isServiceOn;

public class MainActivity extends AppCompatActivity{
    BottomBar bottomBar;
    Fragment fragment;

    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    TextView toolbar_title;
    SharedPreferenceUtils preferances;
    String count = "0";

    int CurrentVersion;

    double lat, lon;
    TextView current_location;
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    private static final String TAG = "LocationData";
    LocationManager locationManager;
    boolean GpsStatus;

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private String sub_cat = "", sub_catid = "", category = "", cat_id = "", sub_cat_2id = "", sub_cat2 = "", sub_cat3_id = "", sub_cat3 = "", addType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferances = SharedPreferenceUtils.getInstance(this);
        bottomBar = findViewById(R.id.bottomBar);
        count =  preferances.getStringValue(CommonUtils.NOTIFICATIONCOUNT,"");

        checkUpdate();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            openOverlaySettings();
        }*/

        if(getIntent().hasExtra("chatFragment")) {
            bottomBar.selectTabAtPosition(1);
        }
        else if(getIntent().hasExtra("sellFragment")) {
            bottomBar.selectTabAtPosition(2);
        }
        else if(getIntent().hasExtra("moreFragment")) {
            bottomBar.selectTabAtPosition(4);
        }

        if (!count.equalsIgnoreCase("")){
            bottomBar.getTabAtPosition(1).setBadgeCount(Integer.parseInt(count));
        }
        else{

        }

        requestForCameraPermission();

        if (isOnline()) {
//            getLocation();
//            checkPermissionForSDK();
//            checkGPSLocation();
            setUpUi();
        }
        else{
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }



       /* if (preferances.getBoolanValue(KEY_isServiceOn, false)) {
            Intent intent = new Intent(MainActivity.this, MyService.class);
            intent.setAction(MyService.ACTION);
            intent.setAction(MyService.ACTION_OFF);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            preferances.setValue(KEY_isServiceOn, true);
        }*/
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void openOverlaySettings() {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        try {
            startActivityForResult(intent, 11);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String convertLatLongToAddress(double lat, double lon) {
        Address obj = null;
        String add = null;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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


    public void setUpUi() {


        if (getIntent().getExtras() != null) {

            Intent i = getIntent();

            if (i.hasExtra("addType"))
                addType = i.getStringExtra("addType");

            if (!CommonUtils.isNullOrEmpty(addType)) {
                if (addType.equals("sell")) {
                    bottomBar.selectTabAtPosition(2);
                } else if (addType.equals("buy")) {
                    bottomBar.selectTabAtPosition(3);
                }


            }
        }
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {


                if (tabId == R.id.tab_home) {

                    CommonUtils.SELECTEDCATEGORY.clear();
                    CommonUtils.SELECTEDIMAGES.clear();
                    CommonUtils.SELECTEDIMAGESINBASE64.clear();


                    fragment = new DiscoverFragment();
                    String count =  preferances.getStringValue(CommonUtils.NOTIFICATIONCOUNT,"");
                    if (!count.equalsIgnoreCase("")){
                        bottomBar.getTabAtPosition(1).setBadgeCount(Integer.parseInt(count));
                    }
                    else{
                        bottomBar.getTabAtPosition(1).removeBadge();
                    }

                    if (fragment != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentContainer, fragment).commit();
                    }
                } else if (tabId == R.id.tab_chat) {

                    CommonUtils.SELECTEDCATEGORY.clear();
                    CommonUtils.SELECTEDIMAGES.clear();
                    CommonUtils.SELECTEDIMAGESINBASE64.clear();

                    fragment = new ChatFragment();

                    preferances.setValue(CommonUtils.NOTIFICATIONCOUNT, "");

                    if (fragment != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentContainer, fragment).commit();
                    }
                } else if (tabId == R.id.tab_buy) {


                    String count =  preferances.getStringValue(CommonUtils.NOTIFICATIONCOUNT,"");
                    if (!count.equalsIgnoreCase("")){
                        bottomBar.getTabAtPosition(1).setBadgeCount(Integer.parseInt(count));
                    }
                    else{
                        bottomBar.getTabAtPosition(1).removeBadge();
                    }

                    fragment = new BuyFragment();


                    if (getIntent().getExtras() != null) {

                        Intent i = getIntent();
                        if (i.hasExtra("addType"))
                            addType = i.getStringExtra("addType");

                        if (!CommonUtils.isNullOrEmpty(addType)) {
                            if (addType.equals("buy")) {
                                sub_cat = i.getStringExtra("sub_cat");
                                sub_catid = i.getStringExtra("sub_catid");
                                category = i.getStringExtra("category");
                                cat_id = i.getStringExtra("cat_id");
                                sub_cat_2id = i.getStringExtra("sub_cat2_id");
                                sub_cat2 = i.getStringExtra("sub_cat2");
                                sub_cat3_id = i.getStringExtra("sub_cat3_id");
                                sub_cat3 = i.getStringExtra("sub_cat3");


                                Bundle bundle = new Bundle();
                                bundle.putString("sub_cat", sub_cat);
                                bundle.putString("sub_catid", sub_catid);
                                bundle.putString("category", category);
                                bundle.putString("cat_id", cat_id);
                                bundle.putString("sub_cat2_id", sub_cat_2id);
                                bundle.putString("sub_cat2", sub_cat2);
                                bundle.putString("sub_cat3_id", sub_cat3_id);
                                bundle.putString("sub_cat3", sub_cat3);
                                fragment.setArguments(bundle);

                            }
                        }
                    }
//                    fragment = new BuyFragment();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentContainer, fragment).commit();
                    }
                } else if (tabId == R.id.tab_sell) {


                    fragment = new SellFragment();

                    String count =  preferances.getStringValue(CommonUtils.NOTIFICATIONCOUNT,"");
                    if (!count.equalsIgnoreCase("")){
                        bottomBar.getTabAtPosition(1).setBadgeCount(Integer.parseInt(count));
                    }
                    else{
                        bottomBar.getTabAtPosition(1).removeBadge();
                    }

                    if (getIntent().getExtras() != null) {

                        Intent i = getIntent();
                        if (i.hasExtra("addType"))
                            addType = i.getStringExtra("addType");

                        if (!CommonUtils.isNullOrEmpty(addType)) {
                            if (addType.equals("sell")) {
                                sub_cat = i.getStringExtra("sub_cat");
                                sub_catid = i.getStringExtra("sub_catid");
                                category = i.getStringExtra("category");
                                cat_id = i.getStringExtra("cat_id");
                                sub_cat_2id = i.getStringExtra("sub_cat2_id");
                                sub_cat2 = i.getStringExtra("sub_cat2");
                                sub_cat3_id = i.getStringExtra("sub_cat3_id");
                                sub_cat3 = i.getStringExtra("sub_cat3");


                                Bundle bundle = new Bundle();
                                bundle.putString("sub_cat", sub_cat);
                                bundle.putString("sub_catid", sub_catid);
                                bundle.putString("category", category);
                                bundle.putString("cat_id", cat_id);
                                bundle.putString("sub_cat2_id", sub_cat_2id);
                                bundle.putString("sub_cat2", sub_cat2);
                                bundle.putString("sub_cat3_id", sub_cat3_id);
                                bundle.putString("sub_cat3", sub_cat3);
                                fragment.setArguments(bundle);

//                                }
                            }
                        }
                    }
//                    fragment = new BuyFragment();
                    if (fragment != null) {

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentContainer, fragment).commit();
                    }

                } else if (tabId == R.id.tab_more) {

                    CommonUtils.SELECTEDCATEGORY.clear();
                    CommonUtils.SELECTEDIMAGES.clear();
                    CommonUtils.SELECTEDIMAGESINBASE64.clear();

                    fragment = new MoreDataFragment();

                    String count =  preferances.getStringValue(CommonUtils.NOTIFICATIONCOUNT,"");
                    if (!count.equalsIgnoreCase("")){
                        bottomBar.getTabAtPosition(1).setBadgeCount(Integer.parseInt(count));
                    }
                    else{
                        bottomBar.getTabAtPosition(1).removeBadge();
                    }
                    if (fragment != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentContainer, fragment).commit();
                    }
                }
            }
        });
    }


    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    @Override
    public void onBackPressed() {

        if (bottomBar.getCurrentTabPosition() != 0) {
            bottomBar.selectTabAtPosition(0);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.create();
            builder.setMessage("Want to Exit ?");

            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                }
            });


            builder.show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (bottomBar.getCurrentTabPosition() == 2) {
//            bottomBar.selectTabAtPosition(0);
//        }
//        drawer.closeDrawers();
    }

//    public void LogoutApp(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.create();
//        builder.setMessage("Are you sure you want to logout?");
//        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.show();
//    }

    public void requestForCameraPermission() {
        final String permission = android.Manifest.permission.CAMERA;
        final String permissionreadstorage = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                showPermissionRationaleDialog("Test", permission);
            } else {
                requestForPermission(permission);
            }
        } else if (ContextCompat.checkSelfPermission(MainActivity.this, permissionreadstorage) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionreadstorage)) {
                showPermissionRationaleDialog("Test", permissionreadstorage);
            } else {
                requestForPermission(permissionreadstorage);
            }
        } else {

        }
    }

    private void showPermissionRationaleDialog(final String message, final String permission) {
        MainActivity.this.requestForPermission(permission);
    }

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, REQUEST_CAMERA_PERMISSION);
    }


    private void checkPermissionForSDK() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int Permission1 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
            Log.e("LogPermission", Permission1 + "/m");
            int Permission2 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE);
            Log.e("LogPermission", Permission2 + "/m");
            int Permission3 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            Log.e("LogPermission", Permission3 + "/m");
            int Permission4 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
            Log.e("LogPermission", Permission4 + "/m");
            int Permission5 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);
            Log.e("LogPermission", Permission5 + "/m");
            int Permission6 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.e("LogPermission", Permission6 + "/m");
            int Permission7 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.e("LogPermission", Permission7 + "/m");
            ArrayList<String> list = new ArrayList<>();

            if (Permission1 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.CAMERA);
            }
            if (Permission2 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.CALL_PHONE);
            }
            if (Permission3 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (Permission4 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (Permission5 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (Permission6 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (Permission7 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (list.size() > 0) {
                ActivityCompat.requestPermissions(MainActivity.this, list.toArray(new String[list.size()]), Constants.MULTI_PERMISSION);
            }
        }
    }


    public void GPSStatus() {
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void checkUpdate() {

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            CurrentVersion = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String url = CommonUtils.APP_URL+"check_version";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("app_name", "Bsell")
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
                            int Version = Integer.parseInt(json.getString("version"));


                            if (Version > CurrentVersion) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.create();
                                builder.setMessage("New app version is available, Please update your app");

                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                });
                                builder.setNegativeButton("Update Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=gss.com.bsell&hl=en"));
                                        startActivity(intent);
                                    }
                                });


                                builder.show();
                            } else {

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
