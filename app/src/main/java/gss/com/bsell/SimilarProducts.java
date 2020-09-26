package gss.com.bsell;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class SimilarProducts extends AppCompatActivity {

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
    private MyViewPager viewPager;
    private EditText et_Search;
    String category_id;
    String IsDelete = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_products);

        preferances = SharedPreferenceUtils.getInstance(SimilarProducts.this);
        viewPager = (MyViewPager) SimilarProducts.this.findViewById(R.id.viewpager);

        et_Search= findViewById(R.id.input_search);
        tvCategories= findViewById(R.id.categories);
        mRecyclerView = findViewById(R.id.discover_listview);
        mRecyclerView.setHasFixedSize(true);
        toolbar_title = findViewById(R.id.toolbar_title);
        changeAddressCat = findViewById(R.id.changeAddressCat);
//        toolbar_title.setText("Similar Products");

        mLayoutManager = new LinearLayoutManager(SimilarProducts.this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(SimilarProducts.this, 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DiscoverItemsAdapter(SimilarProducts.this, DiscoverItemsModelArrayList,category_id, IsDelete);
        mRecyclerView.setAdapter(mAdapter);

        category_id = getIntent().getStringExtra("category_id");

        getcontacts();

    }


    public void getcontacts() {
        //   http://demo1.geniesoftsystem.com/newweb/BSell/assets/upload/photos/
        DiscoverItemsModelArrayList.clear();
        try {

            json = new DiscoverDataDemandAsynk().execute(category_id).get();
            String success = json.getString("status");
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
                    model.setProductSenderId(newJson.getString("userid"));
                    model.setTitle(newJson.getString("product_name"));
                    model.setCatagoryId(newJson.getString("category"));


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

    public class DiscoverDataDemandAsynk extends AsyncTask<String, String, JSONObject> {
        private JSONObject jsonResponse;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String url= CommonUtils.APP_URL+"similarproduct";
                List<NameValuePair> nameValuePairs=new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("category_id",params[0]));
                json = RestJsonClient.post(url,nameValuePairs);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }
}
