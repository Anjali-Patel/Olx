package gss.com.bsell;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import gss.com.bsell.Asynktask.DiscoverDataAsynk;
import gss.com.bsell.Model.AllCategoryModel;
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

public class AllChatFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<AllChatModel> chatdata = new ArrayList<AllChatModel>();
    JSONObject json;
    private SharedPreferenceUtils preferences;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    String kept;


    FrameLayout progressBarHolder;
    public AllChatFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_all_chat_fragment, container, false);
        progressBarHolder = (FrameLayout) v.findViewById(R.id.progressBarHolder);

        preferences = SharedPreferenceUtils.getInstance(getActivity());
        mRecyclerView = v.findViewById(R.id.all_listview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        /*mAdapter = new AllChatAdapter(getActivity(), chatdata);
        mRecyclerView.setAdapter(mAdapter);*/
       // getChatData();
        getChatData(preferences.getStringValue(CommonUtils.USERID,""));

        return v;
    }

    public void getChatData() {
        chatdata.clear();
        try {

//            "id":"510","category_id":"1","chat_type":"single","group_id":"0","list_id":null,"sender_id":"4","receiver_id":"6","product_id":"73","msg":"hiiii"
            json = new ShowUserChatHistoryAsynk().execute(preferences.getStringValue(CommonUtils.USERID,"")).get();
            if(json!=null) {
                String success = json.getString("success");
                if (success.equalsIgnoreCase("1")) {
                    JSONArray jsonArray = null;
                    jsonArray = json.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject newJson = jsonArray.getJSONObject(i);
                        AllChatModel model = new AllChatModel();

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
                    mAdapter = new AllChatAdapter(getActivity(), chatdata);
                    mRecyclerView.setAdapter(mAdapter);
                    //mAdapter.notifyDataSetChanged();
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
        String Url= CommonUtils.APP_URL+"chathistoryvi";
        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("userid", params[0]));
                jsonResponse = RestJsonClient.post(Url,nameValuePairs);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.delete_chat, menu);
        menu.setHeaderTitle("Select The Action");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getItemId()==R.id.default_center_top_right_indicator){
            Toast.makeText(getActivity(),"calling code",Toast.LENGTH_LONG).show();
        } else{
            return false;
        }
        return true;
    }


    public void getChatData(String Userid) {

        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);

        chatdata.clear();

        //String Url= "http://139.59.15.90/bsell/index.php/Api/chathistoryvi";
        String Url= CommonUtils.APP_URL+"productwisechathistorytest";


        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();



        RequestBody formBody = new FormBody.Builder()
                .add("userid", Userid)
                .build();

        Request request = new Request.Builder()
                .url(Url)
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
                                        else kept = CommonUtils.IMAGE_URL + str;
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

                                outAnimation = new AlphaAnimation(1f, 0f);
                                outAnimation.setDuration(200);
                                progressBarHolder.setAnimation(outAnimation);
                                progressBarHolder.setVisibility(View.GONE);

                                mAdapter = new AllChatAdapter(getActivity(), chatdata);
                                mRecyclerView.setAdapter(mAdapter);
                                //mAdapter.notifyDataSetChanged();
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
