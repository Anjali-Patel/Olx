package gss.com.bsell;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import gss.com.bsell.Adapter.AllCategoryAdapter;
import gss.com.bsell.Adapter.AllChatAdapter;
import gss.com.bsell.Adapter.MoreCategoryAdapter;
import gss.com.bsell.Asynktask.DiscoverDataAsynk;
import gss.com.bsell.Model.AllCategoryModel;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import lockscreenads.ApplicationClass;
import lockscreenads.utils.MyService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.provider.Settings.EXTRA_CHANNEL_ID;
import static lockscreenads.ApplicationClass.KEY_isServiceOn;

public class MoreDataFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<AllCategoryModel> categoryList = new ArrayList<AllCategoryModel>();
    private AdView mAdView;
    Fragment fragment;
    private TextView Userid;
    private SharedPreferenceUtils preferances;
    SwitchCompat switchService;



    private Button profileButton, LogoutButton, Settings;


    //    LinearLayout more_service,more_properties,more_pet,more_job,
//            more_furniture,more_fashion,more_electronics,more_car,more_book,more_bike,more_mobile;
    JSONObject json;
    public MoreDataFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_more_data, container, false);
        preferances = SharedPreferenceUtils.getInstance(getActivity());


        profileButton = v.findViewById(R.id.profileButton);
        LogoutButton = v.findViewById(R.id.LogoutButton);
        Settings = v.findViewById(R.id.Settings);
        switchService = v.findViewById(R.id.switch_service);




        profileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

//                String manufacturer = android.os.Build.MANUFACTURER;
//                intent.setComponent(new ComponentName("com.coloros.safecenter",
//                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
//                startActivity(intent);

                Intent i = new Intent(getActivity(), UserProfileBSell.class);
                startActivity(i);
            }
        });

        Settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 26) {
                    Toast.makeText(getActivity(), "UnCheck Bsell Screen Saver", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent("android.settings.CHANNEL_NOTIFICATION_SETTINGS");
                    intent.putExtra("android.provider.extra.APP_PACKAGE", getActivity().getPackageName());
                    intent.putExtra(EXTRA_CHANNEL_ID, "BSell Screen Saver");
                    startActivity(intent);


                } else {

                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", getActivity().getPackageName());
                    intent.putExtra("app_uid", getActivity().getApplicationInfo().uid);
                    Toast.makeText(getActivity(), "Select Block all notification", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }

            }
        });


        LogoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.create();
                builder.setMessage("Are you sure you want to logout ?");

                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                preferances.setValue(CommonUtils.USERID, "");

                Intent i = new Intent(getActivity(), SelectionScreen.class);
                startActivity(i);
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.addCategory(Intent.CATEGORY_HOME);
//                        startActivity(intent);
                    }
                });


                builder.show();

            }
        });


//        more_mobile = v.findViewById(R.id.more_mobile);
//        more_bike = v.findViewById(R.id.more_bike);
//        more_book = v.findViewById(R.id.more_book);
//        more_car = v.findViewById(R.id.more_car);
//        more_electronics = v.findViewById(R.id.more_electronics);
//        more_fashion = v.findViewById(R.id.more_fashion);
//        more_furniture = v.findViewById(R.id.more_furniture);
//        more_job = v.findViewById(R.id.more_job);
//        more_pet = v.findViewById(R.id.more_pet);
//        more_properties = v.findViewById(R.id.more_properties);
//        more_service = v.findViewById(R.id.more_service);
//
//        more_mobile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Mobiles");
//                startActivity(i);
//            }
//        });
//        more_bike.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Bikes");
//                startActivity(i);
//            }
//        });
//        more_book.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Books");
//                startActivity(i);
//            }
//        });
//        more_car.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Car");
//                startActivity(i);
//            }
//        });
//        more_electronics.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Electronics");
//                startActivity(i);
//            }
//        });
//        more_fashion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Fashion");
//                startActivity(i);
//            }
//        });
//        more_furniture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Furniture");
//                startActivity(i);
//            }
//        });
//        more_job.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Jobs");
//                startActivity(i);
//            }
//        });
//        more_pet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Pets");
//                startActivity(i);
//            }
//        });
//        more_properties.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Properties");
//                startActivity(i);
//            }
//        });
//        more_service.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(),AllCategoryActivity.class);
//                i.putExtra("category","Services");
//                startActivity(i);
//            }
//        });
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        mRecyclerView =  v.findViewById(R.id.all_listview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MoreCategoryAdapter(getActivity(), categoryList);
        mRecyclerView.setAdapter(mAdapter);
        getCategory();
        switchService.setChecked(preferances.getBoolanValue(KEY_isServiceOn, false));
        switchService.setVisibility(View.GONE);
        switchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

              /*  if (b) {
                    ApplicationClass.isForStopService = false;
                    Intent intent = new Intent(getActivity(), MyService.class);
                    intent.setAction(MyService.ACTION);
                    intent.setAction(MyService.ACTION_OFF);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (getActivity() != null)
                            getActivity().startForegroundService(intent);

                    } else {
                        if (getActivity() != null)
                            getActivity().startService(intent);
                    }
                    preferances.setValue(KEY_isServiceOn, true);
                } else {
                    ApplicationClass.isForStopService = true;
                    Intent intent = new Intent(getActivity(), MyService.class);
                    if (getActivity() != null)
                        getActivity().stopService(intent);
                    preferances.setValue(KEY_isServiceOn, false);

                }*/

            }
        });

        return v;
    }
//    public void getCategory() {
//        categoryList.clear();
//        try {
////            String data ="{\"success\":\"success\",\"data\":[{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"this is brand new sofa set\",\"values\":\"₹55\"},\n" +
////                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"New car no damage no scratches\",\"values\":\"₹5000\"},\n" +
////                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"Old used card 1,00,000KM\",\"values\":\"₹4054\"},\n" +
////                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"Used Mobile in best condition\",\"values\":\"₹200\"},\n" +
////                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"house for rent\",\"values\":\"₹1145\"},\n" +
////                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"house for rent\",\"values\":\"₹454\"},\n" +
////                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"house for rent\",\"values\":\"₹454\"},\n" +
////                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"house for rent\",\"values\":\"₹9785\"},\n" +
////                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"house for rent\",\"values\":\"₹10,300\"}]}";
////            json = new JSONObject(data);
////            model.setImage(newJson.getString("image_url")+Category+"/"+i+".jpg");
//
//            String Url="http://139.59.15.90/bsell/index.php/Api/category";
//            json = new DiscoverDataAsynk().execute(Url).get();
//            Log.e("MoreData",json.toString());
//            String success = json.getString("success");
//            if (success.equalsIgnoreCase("1")) {
//                JSONArray jsonArray = json.getJSONArray("data");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject newJson = jsonArray.getJSONObject(i);
//                    AllCategoryModel model = new AllCategoryModel();
//                    model.setImage(newJson.getString("icon"));
//                    model.setDescription(newJson.getString("category"));
//                    model.setValue(newJson.getString("cat_id"));
//                    categoryList.add(model);
//                }
//                mAdapter.notifyDataSetChanged();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//    }

    public void getCategory() {

        categoryList.clear();

        String url = CommonUtils.APP_URL+"category";

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();



        Request request = new Request.Builder()
                .url(url).get().build();


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
                                JSONArray jsonArray = json.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject newJson = jsonArray.getJSONObject(i);
                                    AllCategoryModel model = new AllCategoryModel();
                                    model.setImage(newJson.getString("icon"));
                                    model.setDescription(newJson.getString("category"));
                                    model.setValue(newJson.getString("cat_id"));
                                    categoryList.add(model);
                                }
                                mAdapter.notifyDataSetChanged();
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
