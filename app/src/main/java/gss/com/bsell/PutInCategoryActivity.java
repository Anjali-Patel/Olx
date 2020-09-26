package gss.com.bsell;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import gss.com.bsell.Asynktask.DiscoverDataAsynk;
import gss.com.bsell.Model.AllCategoryModel;
import gss.com.bsell.Utility.CommonUtils;

public class PutInCategoryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<AllCategoryModel> categoryList = new ArrayList<AllCategoryModel>();
    JSONObject json;
    String amount;
    ArrayList<String> Mylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_in_category);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
         amount = getIntent().getStringExtra("amount");
         Mylist = getIntent().getStringArrayListExtra("imagelist");

        mRecyclerView =  findViewById(R.id.all_listview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MoreCategoryAdapter(getApplicationContext(), categoryList);
        mRecyclerView.setAdapter(mAdapter);
        getCategory();


        
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
    public void getCategory() {
        categoryList.clear();
        try {
            String Url= CommonUtils.APP_URL+"category";
            json = new DiscoverDataAsynk().execute(Url).get();
            Log.e("MoreData",json.toString());
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
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public class MoreCategoryAdapter extends RecyclerView.Adapter<MoreCategoryAdapter.MyViewHolder> {
        private Context context1;
        private ArrayList<AllCategoryModel> discoverItemsArrayList;

        public MoreCategoryAdapter(Context context, ArrayList<AllCategoryModel> discoverItemsModelArrayList) {
            context1 = context;
            discoverItemsArrayList = discoverItemsModelArrayList;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
           public TextView more_textView;
           public ImageView more_imageView;
            public MyViewHolder(View v) {
                super(v);
                more_textView = (TextView) v.findViewById(R.id.more_textView);
                more_imageView = (ImageView) v.findViewById(R.id.more_imageView);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_data_listitems, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final AllCategoryModel model = discoverItemsArrayList.get(position);
            holder.more_textView.setText(model.getDescription());
            Picasso.with(context1).load(model.getImage()).placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round).into(holder.more_imageView);

            holder.more_textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context1,AddDescriptionActivity.class);
                    i.putExtra("amount", amount);
                    i.putExtra("imagelist", Mylist);
//                    i.putExtra("category", model.getDescription());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context1.startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return discoverItemsArrayList.size();
        }
    }
}
