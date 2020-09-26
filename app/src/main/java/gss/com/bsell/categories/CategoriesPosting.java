package gss.com.bsell.categories;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import gss.com.bsell.Adapter.CategoriesPostingAdapter;
import gss.com.bsell.Asynktask.DiscoverDataAsynk;
import gss.com.bsell.Model.CategoriesModel;
import gss.com.bsell.R;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.webrequest.RestJsonClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CategoriesPosting extends AppCompatActivity {

    private RecyclerView categoriesList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<CategoriesModel> categoryList = new ArrayList<>();
    JSONObject json;
    private String catType="category";
    private String adType ="";
    private String amount="";
    private ArrayList<String> Mylist=new ArrayList<>();

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_posting);

        adType =getIntent().getExtras().getString("adType");


        amount = getIntent().getStringExtra("amount");
        Mylist = getIntent().getStringArrayListExtra("imagelist");

        getSupportActionBar().setTitle("Select Category");


        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        categoriesList =  findViewById(R.id.categoriesList);
        categoriesList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        categoriesList.setLayoutManager(mLayoutManager);
        mAdapter = new CategoriesPostingAdapter((Activity)this, categoryList,catType, adType, amount,Mylist);
        categoriesList.setAdapter(mAdapter);
        getCategory();
    }

//    public void getCategory() {
//        categoryList.clear();
//        try {
//
//            json = new DiscoverDataAsynk().execute().get();
//            Log.e("MoreData",json.toString());
//            String success = json.getString("success");
//            if (success.equalsIgnoreCase("1")) {
//                JSONArray jsonArray = json.getJSONArray("data");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject newJson = jsonArray.getJSONObject(i);
//                    CategoriesModel model = new CategoriesModel();
//                    model.setIcon(newJson.getString("icon"));
//                    model.setCat(newJson.getString("category_name"));
//                    model.setCat_id(newJson.getString("category_id"));
//                    model.setAttribute(newJson.getString("attribute_name"));
//                    model.setNext_cat_id(newJson.getString("next_category_id"));
//
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
//        outAnimation = new AlphaAnimation(1f, 0f);
//        outAnimation.setDuration(200);
//        progressBarHolder.setAnimation(outAnimation);
//        progressBarHolder.setVisibility(View.GONE);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { }


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
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

        }
    }

    public void getCategory() {

        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);

        categoryList.clear();

        String url = (CommonUtils.APP_URL)+"get_categories";

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();



        RequestBody formBody = new FormBody.Builder()
                .add("category_id", "")

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


                            String success = json.getString("success");

                            if (success.equalsIgnoreCase("1")) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject newJson = jsonArray.getJSONObject(i);
                                    CategoriesModel model = new CategoriesModel();
                                    model.setIcon(newJson.getString("icon"));
                                    model.setCat(newJson.getString("category_name"));
                                    model.setCat_id(newJson.getString("category_id"));
                                    model.setAttribute(newJson.getString("attribute_name"));
                                    model.setNext_cat_id(newJson.getString("next_category_id"));

                                    categoryList.add(model);
                                }

                                outAnimation = new AlphaAnimation(1f, 0f);
                                outAnimation.setDuration(200);
                                progressBarHolder.setAnimation(outAnimation);
                                progressBarHolder.setVisibility(View.GONE);

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
