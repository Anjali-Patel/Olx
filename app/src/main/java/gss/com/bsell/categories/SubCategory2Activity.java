package gss.com.bsell.categories;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import gss.com.bsell.Adapter.SubSubCategoryAdapter;
import gss.com.bsell.Model.CategoriesModel;
import gss.com.bsell.R;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.webrequest.RestJsonClient;

public class SubCategory2Activity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<CategoriesModel> categoryList = new ArrayList<>();
    JSONObject json;
    private String catType="subsub";
    private String sub_category_id ="",sub_cat="",category_id="",category="";
    private String adType="";
    private String amount="";
    private ArrayList<String> Mylist=new ArrayList<>();
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_posting);

        sub_category_id =getIntent().getStringExtra("sub_category_id");
        sub_cat=getIntent().getStringExtra("sub_category");
        category_id=getIntent().getStringExtra("category_id");
        category=getIntent().getStringExtra("category");
        adType=getIntent().getStringExtra("adType");
        amount = getIntent().getStringExtra("amount");
        Mylist = getIntent().getStringArrayListExtra("imagelist");

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        getSupportActionBar().setTitle("Brand");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mRecyclerView =  findViewById(R.id.all_listview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SubSubCategoryAdapter((Activity)this, categoryList, catType,sub_cat,sub_category_id,category,category_id,adType,amount,Mylist);
        mRecyclerView.setAdapter(mAdapter);
        getSubSubCategory();


    }

    private void getSubSubCategory() {
        categoryList.clear();
        try {
//            String data ="{\"success\":\"success\",\"data\":[{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"this is brand new sofa set\",\"values\":\"₹55\"},\n" +
//                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"New car no damage no scratches\",\"values\":\"₹5000\"},\n" +
//                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"Old used card 1,00,000KM\",\"values\":\"₹4054\"},\n" +
//                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"Used Mobile in best condition\",\"values\":\"₹200\"},\n" +
//                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"house for rent\",\"values\":\"₹1145\"},\n" +
//                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"house for rent\",\"values\":\"₹454\"},\n" +
//                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"house for rent\",\"values\":\"₹454\"},\n" +
//                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"house for rent\",\"values\":\"₹9785\"},\n" +
//                    "{\"image_url\":\"http://demo1.geniesoftsystem.com/newweb/Sagar/BSell/\",\"description\":\"house for rent\",\"values\":\"₹10,300\"}]}";
//            json = new JSONObject(data);
//            model.setImage(newJson.getString("image_url")+Category+"/"+i+".jpg");

//            String Url="http://139.59.15.90/bsell/index.php/Api/"+catType+"category";
            String Url= CommonUtils.APP_URL+"get_categories";
            json = new SubSubCategoryTask().execute(Url, sub_category_id).get();
            Log.e("subcat",json.toString());
            String success = json.getString("success");
            if (success.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject newJson = jsonArray.getJSONObject(i);
                    CategoriesModel model = new CategoriesModel();
                    model.setIcon(newJson.getString("icon"));
                    model.setCat(newJson.getString("category_name"));
                    model.setCat_id(newJson.getString("category_id"));
                    categoryList.add(model);
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

        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }

    public  class SubSubCategoryTask extends AsyncTask<String, String, JSONObject> {
        private JSONObject jsonResponse;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs=new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("category_id",params[1]));
//                jsonResponse = RestJsonClient.connect(params[0]);
                jsonResponse = RestJsonClient.post(params[0],nameValuePairs);


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
//            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

//            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
