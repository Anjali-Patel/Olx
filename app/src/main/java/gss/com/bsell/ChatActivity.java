package gss.com.bsell;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import gss.com.bsell.Model.MyChatModel;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.webrequest.RestJsonClient;

public class ChatActivity extends AppCompatActivity {
    ImageView ivBack;
    Toolbar toolbar;
    TextView UserNameOnChat;

    ImageView send_chatMessage;
    EditText chat_textmessage;
    String sender_id, receiver_id, friend_name, profilepic, chat_username="";
    ImageView ImageLogo;

    ArrayList<MyChatModel> ChatArrayList = new ArrayList<MyChatModel>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;

    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

    JSONArray jsonArray;
    JSONObject json;
    String User_id;
    private String productid, userdemand, category_id;
    private SharedPreferenceUtils preferences;


    private Handler handler = new Handler();
    private Runnable runnable;

    public void stop() {
//        started = false;
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //to stop refreshing chats
        stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.chat_statusbar));
        }

        preferences = SharedPreferenceUtils.getInstance(getApplicationContext());
        User_id=preferences.getStringValue(CommonUtils.USERID,"");

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sender_id = preferences.getStringValue(CommonUtils.USERID,"");;
        receiver_id = getIntent().getStringExtra("receiver_id");
        profilepic = getIntent().getStringExtra("image");
        productid = getIntent().getStringExtra("product_id");
        userdemand = getIntent().getStringExtra("userdemand");
        category_id = getIntent().getStringExtra("category_id");
        chat_username= getIntent().getStringExtra("chat_username");


        //if user is sender then take receiverid  in receiverid else take senderid in receiverid
        if (SharedPreferenceUtils.getInstance(this).getStringValue(CommonUtils.USERID, "")
                .equalsIgnoreCase(sender_id)) {
            receiver_id = receiver_id;

        } else {
            receiver_id = sender_id;

        }

        setupToolBar();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new InChatAdapter(getApplicationContext(), ChatArrayList,User_id);
        mRecyclerView.setAdapter(mAdapter);

        //new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mLayoutManager.setStackFromEnd(true);

        send_chatMessage = findViewById(R.id.btn_send);
        chat_textmessage = findViewById(R.id.edt_text);

        send_chatMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chat_textmessage.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(ChatActivity.this, "Please type something first", Toast.LENGTH_SHORT).show();
                } else {
                    new ProductChatTask().execute(User_id, receiver_id, chat_textmessage.getText().toString(),
                            productid, userdemand);

                }
            }
        });


        //refreshing chat after every 5 seconds
        runnable = new Runnable() {
            public void run() {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        //DO YOUR THINGS
                        LoadList();
                        handler.postDelayed(runnable , 5000);

                    }
                });
            }
        };

        handler.postDelayed(runnable , 0);

        //LoadList();
    }

    public void setupToolBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageLogo = findViewById(R.id.ImageLogo);
        Picasso.with(getApplicationContext()).load(profilepic).placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round).resize(200, 200).into(ImageLogo);
        UserNameOnChat = findViewById(R.id.UserNameOnChat);

        if( chat_username == null){
            UserNameOnChat.setText("User");
        }
        else{
            UserNameOnChat.setText(chat_username);
        }
    }

    public void LoadList() {
        try {
            ChatArrayList.clear();
            // json = new showproductchatsTask().execute(User_id, "15","117","1").get();
            json = new showproductchatsTask().execute(User_id, receiver_id, productid).get();
            if(json!=null)
                if(json.has("success"))
                    if (json.getString("success").equalsIgnoreCase("1")) {
                        if(json.has("data"))
                            jsonArray = json.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            MyChatModel chatModel = new MyChatModel();
                            if(jsonObj.has("id"))
                                chatModel.setChatId(jsonObj.getString("id"));
                            if(jsonObj.has("sender_id"))
                                chatModel.setSender_id(jsonObj.getString("sender_id"));
                            if(jsonObj.has("receiver_id"))
                                chatModel.setReceiver_id(jsonObj.getString("receiver_id"));
                            if(jsonObj.has("msg"))
                                chatModel.setMessage(jsonObj.getString("msg"));
                            if(jsonObj.has("sender_name"))
                                chatModel.setSender_name(jsonObj.getString("sender_name"));
                            //                    chatModel.setReceiver_name(jsonObj.getString("receiver_name"));

                            if(jsonObj.has("created_date")) {
                                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObj.getString("created_date"));
                                String newString = new SimpleDateFormat("dd-MM-yyyy").format(date); // 25-03-2019
                                Date td_date = new Date();
                                String todayStr = new SimpleDateFormat("dd-MM-yyyy").format(td_date); //
                                if (newString.compareTo(todayStr) == 0) {
                                    newString = new SimpleDateFormat("hh:mm a").format(date); // 9:00

                                } else {
                                    newString = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(date); // 25-03-2019

                                }

                                chatModel.setCreated_date(newString);
                            }


                            ChatArrayList.add(chatModel);
                        }

                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(ChatArrayList.size()-1);

                    }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public class ProductChatTask extends AsyncTask<String, String, JSONObject> {
        JSONObject jsonResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String url = CommonUtils.APP_URL+"productchat";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("senderid", params[0]));
                nameValuePairs.add(new BasicNameValuePair("recieverid", params[1]));
                nameValuePairs.add(new BasicNameValuePair("msg", params[2]));
                nameValuePairs.add(new BasicNameValuePair("productid", params[3]));
                nameValuePairs.add(new BasicNameValuePair("userdemand", params[4]));

                jsonResponse = RestJsonClient.post2(url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        protected void onPostExecute(JSONObject params) {
            super.onPostExecute(params);
//            progressBar.setVisibility(View.GONE);
            chat_textmessage.setText("");
            LoadList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("chatFragment","chatFragment");
        startActivity(i);
    }

    private class showproductchatsTask extends AsyncTask<String ,String ,JSONObject>{
        private JSONObject json;
        @Override
        protected JSONObject doInBackground(String... params) {


            try {
                String url= CommonUtils.APP_URL+"showproductchats";
                List<NameValuePair> nameValuePairs=new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("senderid",params[0]));
                nameValuePairs.add(new BasicNameValuePair("recieverid",params[1]));
                nameValuePairs.add(new BasicNameValuePair("productid",params[2]));
//                nameValuePairs.add(new BasicNameValuePair("categoryid",params[3]));
                json = RestJsonClient.post(url,nameValuePairs);
                Log.e("ContactUs", json.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }
}
