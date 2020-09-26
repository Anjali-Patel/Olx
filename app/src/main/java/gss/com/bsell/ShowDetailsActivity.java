package gss.com.bsell;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import gss.com.bsell.Asynktask.DiscoverDataAsynk;
import gss.com.bsell.Model.DiscoverItemsModel;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.webrequest.RestJsonClient;

public class ShowDetailsActivity extends AppCompatActivity {
    BottomSheetBehavior mBottomSheetBehavior;
    ImageView iv_profile, imageview;
    LinearLayout call;
    ArrayList<DiscoverItemsModel> DiscoverItemsModelArrayList = new ArrayList<>();
    JSONObject json;
    private RecyclerView.Adapter mAdapter;
    TextView product_value, product_description, item_title, product_details, AdDate, Location;
    String product_id,receiver_id, category_id, profilepic, chat_username, sm_productId;
    List<String> ImageString = new ArrayList<>();
    List<String> ImageString_proImg = new ArrayList<>();

    ArrayList<String> ImageArray = new ArrayList<>();
    private LinearLayout l_chat;
    //ArrayList<String> ImageArray_pro = new ArrayList<>();
    private String userdemand="1";
    private String senderid="";

    private Button SimilarItemsButton, DeleteItemButton;
    String Imagecount, PhoneNumber;
    String IsDelete;
    LinearLayout productDetailsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
        l_chat=findViewById(R.id.l_chat);

        // sender_id = getIntent().getStringExtra("sender_id");
        receiver_id = getIntent().getStringExtra("userid");
        // profilepic = getIntent().getStringExtra("image");
        product_id = getIntent().getStringExtra("id");
        category_id = getIntent().getStringExtra("category_id");
        userdemand = getIntent().getStringExtra("userdemand");
        profilepic = getIntent().getStringExtra("image");
        sm_productId = getIntent().getStringExtra("sm_productId");
        chat_username= getIntent().getStringExtra("chat_username");
        IsDelete= getIntent().getStringExtra("IsDelete");

//        String value = getIntent().getStringExtra("value");
//        String description = getIntent().getStringExtra("description");
//        String title = getIntent().getStringExtra("title");
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        item_title = findViewById(R.id.item_title);
        // item_title.setText(title);

        product_value = findViewById(R.id.product_value);
        // product_value.setText("₹."+value);

        product_description = findViewById(R.id.product_description);
        // product_description.setText(description);

        //product_details = findViewById(R.id.product_details);
        AdDate = findViewById(R.id.ad_date);

        iv_profile = findViewById(R.id.iv_profile);
        Location = findViewById(R.id.location);


        call = findViewById(R.id.call_customer);
        // Picasso.with(getApplicationContext()).load(CommonUtils.IMAGE_URL+image).placeholder(R.mipmap.ic_launcher_round)
        // .error(R.mipmap.ic_launcher_round).into(imageview);

        imageview= findViewById(R.id.imageview);
        SimilarItemsButton = findViewById(R.id.SimilarItemsButton);
        DeleteItemButton = findViewById(R.id.DeleteItemButton);
        productDetailsLayout = findViewById(R.id.product_details_layout);

        if (IsDelete.equalsIgnoreCase("1")){
            DeleteItemButton.setVisibility(View.VISIBLE);
        }

        getcontacts(product_id,userdemand);



        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowDetailsActivity.this,SliderActivity.class);
                intent.putStringArrayListExtra("ImageArray", ImageArray);
                intent.putExtra("receiver_id",receiver_id);
                intent.putExtra("product_id",product_id);
                intent.putExtra("userdemand",userdemand);
                intent.putExtra("category_id",category_id);
                intent.putExtra("Imagecount",Imagecount);
                intent.putExtra("PhoneNumber",PhoneNumber);
                intent.putExtra("image",profilepic);
                intent.putExtra("chat_username",chat_username);

                startActivity(intent);
            }
        });

        SimilarItemsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(ShowDetailsActivity.this, SimilarProducts.class);
                i.putExtra("category_id",sm_productId);

                startActivity(i);
            }
        });

        final View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (BottomSheetBehavior.STATE_DRAGGING == newState) {
                    iv_profile.animate().scaleX(0).scaleY(0).setDuration(300).start();

                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    iv_profile.animate().scaleX(1).scaleY(1).setDuration(200).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestForCall();
            }
        });
        DeleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailsActivity.this);
                builder.create();
                builder.setMessage("Are you sure you want delete this product?");

                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteProduct();
                    }
                });


                builder.show();

            }
        });

        l_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ShowDetailsActivity.this,ChatActivity.class);
                i.putExtra("sender_id", SharedPreferenceUtils.getInstance(getApplicationContext())
                .getStringValue(CommonUtils.USERID,""));

                if(SharedPreferenceUtils.getInstance(getApplicationContext())
                        .getStringValue(CommonUtils.USERID,"").equalsIgnoreCase(receiver_id)){

                }else {

                    i.putExtra("receiver_id", receiver_id);
                    i.putExtra("product_id", product_id);
                    i.putExtra("userdemand", userdemand);
                    i.putExtra("category_id", category_id);
                    i.putExtra("image", profilepic);
                    i.putExtra("chat_username", chat_username);
                    startActivity(i);

                }


            }
        });


    }

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions( this, new String[]{permission}, 0);
    }

    public void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);

        //PhoneNumber = Integer.parseInt(mobileno);


        callIntent.setData(Uri.parse("tel:" + PhoneNumber));

       // callIntent.setData(Uri.parse("tel:" + "1234567890"));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(callIntent);
    }

    private void showPermissionRationaleDialog(final String message, final String permission) {
        new AlertDialog.Builder(getApplicationContext())
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestForPermission(permission);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .create()
                .show();
    }

    public void requestForCall() {
        final String permission = Manifest.permission.CALL_PHONE;
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale( this, permission)) {
                showPermissionRationaleDialog("Test", permission);
            } else {
                requestForPermission(permission);
            }
        } else {
            makeCall();
        }
    }


    public void getcontacts(String productid, String userdemand) {
        //   http://demo1.geniesoftsystem.com/newweb/BSell/assets/upload/photos/

        String BasePath = "http://139.59.15.90/bsell/assets/upload/categoryIcon/";

        DiscoverItemsModelArrayList.clear();
        try {
            String url = (CommonUtils.APP_URL)+"product_detail";
            //String url = "http://139.59.15.90/bsell/index.php/Api/discoverfragment";
            json = new DiscoverAllDataAsynk().execute(url,productid,userdemand).get();
            String success="";
            if(json!=null)
                success= json.getString("success");
            if (success.equalsIgnoreCase("1")) {
                JSONObject newJson = json.getJSONObject("data");

                    String image = newJson.getString("product_image").replaceAll(" ", "%20");
                    ImageString = Arrays.asList(image.split(","));
                    ImageArray.addAll(ImageString);
                    Imagecount = String.valueOf(ImageArray.size());
                    imageview = findViewById(R.id.imageview);
                    Picasso.with(getApplicationContext()).load(CommonUtils.IMAGE_URL+ImageString.get(0)).placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round).into(imageview);

                Picasso.with(getApplicationContext()).load(CommonUtils.PROFILE_IMG_URL+(newJson.getString("profile_picture"))).placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round).into(iv_profile);

                    item_title.setText( (newJson.getString("username")));
                    Location.setText(newJson.getString("location"));
                    product_description.setText(newJson.getString("description"));

                    if (!newJson.getString("price").equalsIgnoreCase("Product Price")){
                        product_value.setText("₹"+newJson.getString("price"));
                    }
                    else {
                        product_value.setText(" - ");
                    }

                    String[] icons = newJson.getString("icons").split(",");
//                    List<String> fixedLenghtList = Arrays.asList(elements);
//                    ArrayList<String> listOfString = new ArrayList<String>(fixedLenghtList);

//                    String info =(newJson.getString("info")).replace(":"," : ").replace(",","\n");
                    String info =(newJson.getString("info")).replace(":"," : ");
                    String[] infoelements = info.split(",");

                for (int i = 0; i < infoelements.length; i++) {
                    ImageView product_icon = new ImageView(ShowDetailsActivity.this);
                    TextView category_name = new TextView(ShowDetailsActivity.this);
                    LinearLayout product_Linear = new LinearLayout(ShowDetailsActivity.this);


                    product_Linear.setGravity(Gravity.CENTER_VERTICAL);
                    LinearLayout.LayoutParams imagepara = new LinearLayout.LayoutParams(80, 80);
                    imagepara.setMargins(0,10,0,10   );
                    product_icon.setLayoutParams(imagepara);


                    product_Linear.setOrientation(LinearLayout.HORIZONTAL);

                    Picasso.with(getApplicationContext()).load(BasePath+(icons[i])).placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round).into(product_icon);

                    category_name.setTextSize(18);
                    category_name.setText(" " +infoelements[i]);

                    product_Linear.addView(product_icon);
                    product_Linear.addView(category_name);

                    productDetailsLayout.addView(product_Linear);


                }



               // product_details.setText(info);
                    PhoneNumber = newJson.getString("mobile");
                    AdDate.setText(newJson.getString("posted_date"));
                    if(!newJson.isNull("userid"))
                        senderid=newJson.getString("userid");

              //  }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public void DeleteProduct() {
        try {


            json = new DeleteProductAsynk().execute(product_id).get();
            String success = json.getString("success");
            if (success.equalsIgnoreCase("1")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.create();
                builder.setMessage("Product deleted successfully!");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent(ShowDetailsActivity.this, MainActivity.class);
                        startActivity(i);

                    }
                });
                builder.show();
                }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public class DeleteProductAsynk extends AsyncTask<String, String, JSONObject> {
        private JSONObject jsonResponse;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String url= CommonUtils.APP_URL+"delete_product";
                List<NameValuePair> nameValuePairs=new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("product_id",params[0]));
                json = RestJsonClient.post(url,nameValuePairs);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }
}
