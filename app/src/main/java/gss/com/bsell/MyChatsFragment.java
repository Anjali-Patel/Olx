package gss.com.bsell;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import gss.com.bsell.Adapter.AllChatAdapter;
import gss.com.bsell.Model.AllChatModel;
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


public class MyChatsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<AllChatModel> chatdata = new ArrayList<AllChatModel>();
    JSONObject json;
    private SharedPreferenceUtils preferences;
    private String userdemand="1";
    String kept;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;
    public MyChatsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_my_chats_fragment, container, false);
        progressBarHolder = (FrameLayout) v.findViewById(R.id.progressBarHolder);

        preferences = SharedPreferenceUtils.getInstance(getActivity());
        mRecyclerView = v.findViewById(R.id.all_listview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AllChatAdapter(getActivity(), chatdata);
        mRecyclerView.setAdapter(mAdapter);

        getChatData(preferences.getStringValue(CommonUtils.USERID,""));

       // getChatData();

        return v;
    }

    public void getChatData() {
        chatdata.clear();
        try {

            String Url= CommonUtils.APP_URL+"chathistoryvi";

            //String Url= "http://139.59.15.90/bsell/index.php/Api/buyandsellchat";
            json = new ShowUserChatHistoryAsynk().execute(Url,preferences.getStringValue(CommonUtils.USERID,"")).get();
            if(json!=null) {
                String success = json.getString("success");
                if (success.equalsIgnoreCase("1")) {
                    JSONArray jsonArray = null;
                    jsonArray = json.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject newJson = jsonArray.getJSONObject(i);
                        AllChatModel model = new AllChatModel();


                        if(newJson.getString("receiver_id").equalsIgnoreCase(newJson.getString("product_owner"))) {

                            model.setUserDemand("1");

                            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(newJson.getString("created_date"));
                            String newString=  new SimpleDateFormat("dd-MM-yyyy").format(date); // 25-03-2019
                            Date td_date = new Date();
                            String todayStr=  new SimpleDateFormat("dd-MM-yyyy").format(td_date); //
                            if (newString.compareTo(todayStr)== 0) {
                                newString = new SimpleDateFormat("hh:mm a").format(date); // 9:00

                            } else {
                                newString=  new SimpleDateFormat("dd-MM-yyyy").format(date); // 25-03-2019

                            }

                            //model.setReceiver_name(newJson.getString("fname"));
                            model.setReceiver_name(newJson.getString("fname"));
                            model.setCreated_date(newString);
                            model.setMsg(newJson.getString("last_message"));
                            model.setProduct_id(newJson.getString("product_id"));
                            model.setReceiver_id(newJson.getString("user_id"));
                            model.setChatCount(newJson.getString("unseen_count"));
                            if(!newJson.getString("product_image").equalsIgnoreCase("null")) {

                                String str = newJson.getString("product_image");
                                if (str.contains(","))
                                    kept = CommonUtils.IMAGE_URL + str.substring(0, str.indexOf(","));
                                else kept = CommonUtils.IMAGE_URL + model.getImage();
                                model.setImage(kept);
                            }

                            if(newJson.getString("receiver_id").equalsIgnoreCase(newJson.getString("product_owner"))) {
                                model.setUserDemand("1");
                            }
                            else{
                                model.setUserDemand("0");
                            }

                            if(newJson.has("sender_id"))
                                model.setSender_id(newJson.getString("sender_id"));
                            chatdata.add(model);

                        }

                        else{
                            model.setUserDemand("0");
                        }
                        mAdapter.notifyDataSetChanged();

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }



    public  class ShowUserChatHistoryAsynk extends AsyncTask<String, String, JSONObject> {
        private JSONObject jsonResponse;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                if (params[1] != null)
                    nameValuePairs.add(new BasicNameValuePair("userid", params[1]));

                jsonResponse = RestJsonClient.post2(params[0],nameValuePairs);
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


    public void getChatData(String Userid) {

        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);

        chatdata.clear();

        String url = CommonUtils.APP_URL+"chathistoryvi";

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();



        RequestBody formBody = new FormBody.Builder()
                .add("userid", Userid)
                .add("userdemad", userdemand)

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

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);


                            String success = json.getString("success");

                            if (success.equalsIgnoreCase("1")) {
                                JSONArray jsonArray = null;
                                jsonArray = json.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject newJson = jsonArray.getJSONObject(i);
                                    AllChatModel model = new AllChatModel();


                                    if(newJson.getString("receiver_id").equalsIgnoreCase(newJson.getString("product_owner"))) {

                                        model.setUserDemand("1");

                                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(newJson.getString("created_date"));
                                        String newString=  new SimpleDateFormat("dd-MM-yyyy").format(date); // 25-03-2019
                                        Date td_date = new Date();
                                        String todayStr=  new SimpleDateFormat("dd-MM-yyyy").format(td_date); //
                                        if (newString.compareTo(todayStr)== 0) {
                                            newString = new SimpleDateFormat("hh:mm a").format(date); // 9:00

                                        } else {
                                            newString=  new SimpleDateFormat("dd-MM-yyyy").format(date); // 25-03-2019

                                        }

                                        //model.setReceiver_name(newJson.getString("fname"));
                                        model.setReceiver_name(newJson.getString("fname"));
                                        model.setCreated_date(newString);
                                        model.setMsg(newJson.getString("last_message"));
                                        model.setProduct_id(newJson.getString("product_id"));
                                        model.setReceiver_id(newJson.getString("user_id"));
                                        model.setChatCount(newJson.getString("unseen_count"));
                                        if(!newJson.getString("product_image").equalsIgnoreCase("null")) {

                                            String str = newJson.getString("product_image");
                                            if (str.contains(","))
                                                kept = CommonUtils.IMAGE_URL + str.substring(0, str.indexOf(","));
                                            else kept = CommonUtils.IMAGE_URL + model.getImage();
                                            model.setImage(kept);
                                        }

                                        if(newJson.getString("receiver_id").equalsIgnoreCase(newJson.getString("product_owner"))) {
                                            model.setUserDemand("1");
                                        }
                                        else{
                                            model.setUserDemand("0");
                                        }

                                        if(newJson.has("sender_id"))
                                            model.setSender_id(newJson.getString("sender_id"));
                                        chatdata.add(model);

                                    }

                                    else{
                                        model.setUserDemand("0");
                                    }
                                    outAnimation = new AlphaAnimation(1f, 0f);
                                    outAnimation.setDuration(200);
                                    progressBarHolder.setAnimation(outAnimation);
                                    progressBarHolder.setVisibility(View.GONE);
                                    mAdapter.notifyDataSetChanged();

                                }
                            }

                            else {
                                outAnimation = new AlphaAnimation(1f, 0f);
                                outAnimation.setDuration(200);
                                progressBarHolder.setAnimation(outAnimation);
                                progressBarHolder.setVisibility(View.GONE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}

