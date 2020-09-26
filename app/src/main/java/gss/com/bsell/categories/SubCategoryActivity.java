package gss.com.bsell.categories;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
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
import gss.com.bsell.Adapter.SubCategoryAdapter;
import gss.com.bsell.Asynktask.DiscoverDataAsynk;
import gss.com.bsell.ChatFragment;
import gss.com.bsell.MainActivity;
import gss.com.bsell.Model.CategoriesModel;
import gss.com.bsell.Model.SelectedCategoryDataModel;
import gss.com.bsell.R;
import gss.com.bsell.SellFragment;
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

public class SubCategoryActivity extends AppCompatActivity {


    private RecyclerView categoriesList;
    private RecyclerView.LayoutManager mLayoutManager;
    Fragment fragment;
    EditText et_Search;


    SharedPreferences sharedpreferences;
    private SharedPreferenceUtils preferences;

    ArrayList<CategoriesModel> categoryList = new ArrayList<>();
    ArrayList<CategoriesModel> categoriesListTmp = new ArrayList<>();
    ArrayList<SelectedCategoryDataModel> selectedCategories = new ArrayList<>();

    JSONObject json;
    private String catType="sub";
    private String  category_id;
    private String category="";
    private String adType="";
    private String amount="";
    String editCategoryOperated, position;
    private ArrayList<String> Mylist=new ArrayList<>();

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_posting);

        preferences = SharedPreferenceUtils.getInstance(this);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        categoriesList =  findViewById(R.id.categoriesList);
        et_Search = findViewById(R.id.input_search);

        category_id=getIntent().getStringExtra("category_id");
        category=getIntent().getStringExtra("category");
        editCategoryOperated=getIntent().getStringExtra("editCategoryOperated");
        position=getIntent().getStringExtra("position");
        adType=getIntent().getStringExtra("adType");


        Mylist = getIntent().getStringArrayListExtra("selectedCategories");


       if (editCategoryOperated.equalsIgnoreCase("done")){
           ArrayList<SelectedCategoryDataModel> listdata = CommonUtils.SELECTEDCATEGORY;
           Intent i = new Intent(this, MainActivity.class);
           i.putExtra("addType", adType);
           startActivity(i);
       }
       else {
           getSubCategory();
       }


        SubCategoryAdapter adapter = new SubCategoryAdapter(this,categoryList, editCategoryOperated, position, adType);
        mLayoutManager = new LinearLayoutManager(this);
        categoriesList.setLayoutManager(mLayoutManager);
        categoriesList.setAdapter(adapter);



        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String strText = s.toString().trim();
                if (strText.length() != 0) {
                    categoriesListTmp.clear();
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getCat().toLowerCase().contains(strText.toLowerCase())) {
                            categoriesListTmp.add(categoryList.get(i));
                        }
                    }
                    SubCategoryAdapter adapter = new SubCategoryAdapter(SubCategoryActivity.this,categoriesListTmp, editCategoryOperated, position, adType);
                    mLayoutManager = new LinearLayoutManager(SubCategoryActivity.this);
                    categoriesList.setLayoutManager(mLayoutManager);
                    categoriesList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    SubCategoryAdapter adapter = new SubCategoryAdapter(SubCategoryActivity.this,categoryList, editCategoryOperated, position, adType);
                    mLayoutManager = new LinearLayoutManager(SubCategoryActivity.this);
                    categoriesList.setLayoutManager(mLayoutManager);
                    categoriesList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strText = s.toString().trim();
                if (strText.length() != 0) {
                    categoriesListTmp.clear();
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getCat().toLowerCase().contains(strText.toLowerCase())) {
                            categoriesListTmp.add(categoryList.get(i));
                        }
                    }
                    SubCategoryAdapter adapter = new SubCategoryAdapter(SubCategoryActivity.this,categoriesListTmp, editCategoryOperated, position, adType);
                    mLayoutManager = new LinearLayoutManager(SubCategoryActivity.this);
                    categoriesList.setLayoutManager(mLayoutManager);
                    categoriesList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    SubCategoryAdapter adapter = new SubCategoryAdapter(SubCategoryActivity.this,categoryList, editCategoryOperated, position, adType);
                    mLayoutManager = new LinearLayoutManager(SubCategoryActivity.this);
                    categoriesList.setLayoutManager(mLayoutManager);
                    categoriesList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }
        });


    }

//    public void getSubCategory() {
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
//                    getSupportActionBar().setTitle(newJson.getString("attribute_name"));
//                    model.setNext_cat_id(newJson.getString("next_category_id"));
//                    model.setCurrent_Cat_id(newJson.getString("prev_categgory_id"));
//
//                    categoryList.add(model);
//                    categoriesListTmp.add(model);
//                }
//            }
//
//            else if (success.equalsIgnoreCase("2")) {
//                ArrayList<SelectedCategoryDataModel> listdata = CommonUtils.SELECTEDCATEGORY;
//                preferences.setValue(CommonUtils.PRODUCTNAME, listdata.get(1).getCatData());
//                preferences.setValue(CommonUtils.SIMILARPRODUCTID, listdata.get(1).getCatId());
//
//                Intent i = new Intent(this, MainActivity.class);
//                i.putExtra("addType", adType);
//                startActivity(i);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//    }


    public class DiscoverDataAsynk extends AsyncTask<String, String, JSONObject> {
        private JSONObject jsonResponse;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String Url = (CommonUtils.APP_URL)+"get_categories";

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("category_id", category_id));

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

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();

        CommonUtils.SELECTEDCATEGORY.clear();

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("sellFragment","sellFragment");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }*/

    public void getSubCategory() {

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
                .add("category_id", category_id)

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
                                    getSupportActionBar().setTitle(newJson.getString("attribute_name"));
                                    model.setNext_cat_id(newJson.getString("next_category_id"));
                                    model.setCurrent_Cat_id(newJson.getString("prev_categgory_id"));

                                    categoryList.add(model);
                                    categoriesListTmp.add(model);
                                }

                                outAnimation = new AlphaAnimation(1f, 0f);
                                outAnimation.setDuration(200);
                                progressBarHolder.setAnimation(outAnimation);
                                progressBarHolder.setVisibility(View.GONE);

                                SubCategoryAdapter adapter = new SubCategoryAdapter(SubCategoryActivity.this,categoryList, editCategoryOperated, position, adType);
                                mLayoutManager = new LinearLayoutManager(SubCategoryActivity.this);
                                categoriesList.setLayoutManager(mLayoutManager);
                                categoriesList.setAdapter(adapter);
                            }

                            else if (success.equalsIgnoreCase("2")) {
                                ArrayList<SelectedCategoryDataModel> listdata = CommonUtils.SELECTEDCATEGORY;
                                preferences.setValue(CommonUtils.PRODUCTNAME, listdata.get(1).getCatData());
                                preferences.setValue(CommonUtils.SIMILARPRODUCTID, listdata.get(1).getCatId());

                                outAnimation = new AlphaAnimation(1f, 0f);
                                outAnimation.setDuration(200);
                                progressBarHolder.setAnimation(outAnimation);
                                progressBarHolder.setVisibility(View.GONE);

                                Intent i = new Intent(SubCategoryActivity.this, MainActivity.class);
                                i.putExtra("addType", adType);
                                startActivity(i);
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
