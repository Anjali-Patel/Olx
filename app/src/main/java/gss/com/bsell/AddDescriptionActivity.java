package gss.com.bsell;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.categories.CategoriesPosting;
import gss.com.bsell.webrequest.RestJsonClient;

public class AddDescriptionActivity extends AppCompatActivity {
    ImageView backdrop;
    String amount, category="";
    ArrayList<String> Mylist;
    Toolbar toolbar;
    EditText et_category, et_price, et_description, et_location;
    HttpEntity resEntity;
    Button post_Ad;
    ProgressBar progressBar;
    private  String url=CommonUtils.APP_URL+"uploadproduct";
    private String user_demands="0";
    private     SharedPreferenceUtils preferences;
    private String addType="";
    private String  sub_cat="",sub_catid="",cat_id="",sub_cat_2id="",sub_cat2="",sub_cat3_id="",sub_cat3="";
    private TextView tvCategory;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_description);

        preferences = SharedPreferenceUtils.getInstance(getApplicationContext());
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        amount = getIntent().getStringExtra("amount");
        Mylist = getIntent().getStringArrayListExtra("imagelist");
//        category = getIntent().getStringExtra("category");


        if(getIntent().getExtras()!=null ) {

            Intent i = getIntent();

            if(i.hasExtra("addType"))
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
                }
            }
        }




        backdrop = findViewById(R.id.backdrop);
        et_category = findViewById(R.id.et_category);
//        et_category.setText("Select Category");
        et_price = findViewById(R.id.et_price);
        et_price.setText(amount);
        et_description = findViewById(R.id.et_description);
        et_location = findViewById(R.id.et_location);
        tvCategory = findViewById(R.id.tvCategory);

        post_Ad=findViewById(R.id.post_Ad);
        progressBar=findViewById(R.id.progress_bar);

        StringBuilder sb=new StringBuilder();
        if(!isNullOrEmpty(category)){
            sb.append("Category \n"+category+"\n");
        }
        if(!isNullOrEmpty(sub_cat)){
            sb.append("subCategory \n"+sub_cat+"\n");
        }
        if(!isNullOrEmpty(sub_cat2)){
            sb.append("brand \n"+sub_cat2+"\n");
        }
        if(!isNullOrEmpty(sub_cat3)){
            sb.append("model \n"+sub_cat3+"\n");
        }

        if(!isNullOrEmpty(sb.toString()))
            tvCategory.setText(sb.toString()+"");

        setupToolBar();
        Picasso.with(getApplicationContext()).load("file:" + Mylist.get(0)).into(backdrop);

        post_Ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              new PostAddAsync().execute(preferences.getStringValue(CommonUtils.USERID,""),et_category.getText().toString()
                      ,et_price.getText().toString()
                      ,et_description.getText().toString()
                    ,   preferences.getStringValue(CommonUtils.CurrentLocation,""),user_demands,"","");
            }
        });


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName());
//                Intent i = new Intent();
//                i.putExtra("location",place.getAddress());
//                setResult(RESULT_OK, i);
                preferences.setValue(CommonUtils.CurrentLocation, place.getName().toString());
//                finish();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
            }
        });

        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddDescriptionActivity.this,CategoriesPosting.class);
                intent.putExtra("adType","sell");
                intent.putExtra("amount",et_price.getText().toString());
                intent.putExtra("imagelist",Mylist);

                startActivity(intent);
            }
        });


    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
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
//    private void doFileUpload(){
//
//        File file1 = new File(selectedPath1);
//        File file2 = new File(selectedPath2);
//        String urlString = "http://10.0.2.2/upload_test/upload_media_test.php";
//        try
//        {
//            HttpClient client = new DefaultHttpClient();
//            HttpPost post = new HttpPost(urlString);
//            FileBody bin1 = new FileBody(file1);
//            FileBody bin2 = new FileBody(file2);
//            MultipartEntity reqEntity = new MultipartEntity();
//            reqEntity.addPart("uploadedfile1", bin1);
//            reqEntity.addPart("uploadedfile2", bin2);
//            reqEntity.addPart("user", new StringBody("User"));
//            post.setEntity(reqEntity);
//            HttpResponse response = client.execute(post);
//            resEntity = response.getEntity();
//            final String response_str = EntityUtils.toString(resEntity);
//            if (resEntity != null) {
//                Log.i("RESPONSE",response_str);
//                runOnUiThread(new Runnable(){
//                    public void run() {
//                        try {
//                            Toast.makeText(getApplicationContext(),"Upload Complete. Check the server uploads directory.", Toast.LENGTH_LONG).show();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        }
//        catch (Exception ex){
//            Log.e("Debug", "error: " + ex.getMessage(), ex);
//        }
//    }

    class PostAddAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;


        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("userid", params[0]));
                nameValuePairs.add(new BasicNameValuePair("cat", params[1]));
                if (!params[2].equals(""))
                    nameValuePairs.add(new BasicNameValuePair("amount", params[2]));
                nameValuePairs.add(new BasicNameValuePair("discription", params[3]));
                nameValuePairs.add(new BasicNameValuePair("location", params[4]));
                nameValuePairs.add(new BasicNameValuePair("user_demands", params[5]));

                nameValuePairs.add(new BasicNameValuePair("product_name", params[6]));
                nameValuePairs.add(new BasicNameValuePair("mobile", params[7]));
                for (int i=0;i<Mylist.size();i++){
                    nameValuePairs.add(new BasicNameValuePair("img"+(i+1), Mylist.get(i)));

                }


//                if (imageurl != null)
//                    nameValuePairs.add(new BasicNameValuePair("img1", imageurl));
                Log.d("datap", nameValuePairs.toString());
                json = RestJsonClient.postAdv(url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
//            progress_bar.setVisibility(View.VISIBLE);

//            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
//            progress_bar.setVisibility(View.GONE);

//            mSwipeRefreshLayout.setRefreshing(false);
            startActivity(new Intent(AddDescriptionActivity.this,MainActivity.class));

        }

    }
}
