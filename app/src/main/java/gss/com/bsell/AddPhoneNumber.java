package gss.com.bsell;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gss.com.bsell.Model.UserProfileModel;
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

public class AddPhoneNumber extends AppCompatActivity {
    EditText user_mobile, user_otp;
    private  String url= CommonUtils.APP_URL+"user_login";
    private Button submitPhone, submitOTP;
    String mPhoneNumber, FCMToken;
    TextView phone_req, otp_req, otp_again;
    SharedPreferenceUtils preferances;
    String otp, adminOTP, UserId;
    LinearLayout helpline;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone_number);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Bsell Login");
        }
        // submitPhone= findViewById(R.id.submitPhone);
        submitPhone = (Button) findViewById(R.id.submitPhone);
        user_mobile = findViewById(R.id.user_mobile);
        user_otp = findViewById(R.id.user_otp);
        submitOTP = (Button) findViewById(R.id.submitOTP);
        phone_req = findViewById(R.id.phone_req);
        otp_req = findViewById(R.id.otp_req);
        otp_again = findViewById(R.id.otp_again);
        helpline = findViewById(R.id.helpline);
        preferances=SharedPreferenceUtils.getInstance(this);




        //checkPermissionForSDK();

        submitPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (user_mobile.getText().toString().trim() == null ||  user_mobile.getText().toString().trim().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPhoneNumber.this);
                    builder.create();
                    builder.setMessage("Plese enter Mobile number!");

                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    builder.show();

                }
                else if (user_mobile.length() != 10){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPhoneNumber.this);
                    builder.create();
                    builder.setMessage("Please enter your 10 digit Mobile number!");

                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    builder.show();

                }
                else if (user_mobile.getText().toString().startsWith("+91")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPhoneNumber.this);
                    builder.create();
                    builder.setMessage("Please enter your 10 digit Mobile number without country code!");

                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    builder.show();

                }
                else if (user_mobile.getText().toString().startsWith("+")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPhoneNumber.this);
                    builder.create();
                    builder.setMessage("Please enter your 10 digit Mobile number without country code!");

                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    builder.show();

                }
                else {
                    //user_mobile.setVisibility(View.GONE);
                    submitPhone.setVisibility(View.GONE);
                    phone_req.setVisibility(View.GONE);


                    otp_req.setVisibility(View.VISIBLE);
                    otp_again.setVisibility(View.VISIBLE);
                    user_otp.setVisibility(View.VISIBLE);
                    helpline.setVisibility(View.VISIBLE);
                    submitOTP.setVisibility(View.VISIBLE);

                    LoginUser();

                    //new PostDemandAsync().execute();

                }
            }
        });

        submitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (user_otp.getText().toString().trim().equalsIgnoreCase(otp) || user_otp.getText().toString().trim().equalsIgnoreCase(adminOTP)){

                    preferances.setValue(CommonUtils.ISREGISTERED, true);
                    preferances.setValue(CommonUtils.USERMOBILE, mPhoneNumber);
                    preferances.setValue(CommonUtils.USERID, UserId);

                    startActivity(new Intent(AddPhoneNumber.this,MainActivity.class));
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPhoneNumber.this);
                    builder.create();
                    builder.setMessage("OTP is incorrect, please enter valid OTP");

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    builder.show();

                }
            }
        });

        otp_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginUser();
                //new PostDemandAsync().execute();
            }
        });


    }


    class PostDemandAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //startActivity(new Intent(AddPhoneNumber.this,MainActivity.class));
            String response = "";
            try {
                response = json.getString("response");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (response.equalsIgnoreCase("success")){
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPhoneNumber.this);
                builder.create();
                builder.setMessage("OTP sent to your entered mobile number");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                builder.show();
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPhoneNumber.this);
                builder.create();
                builder.setMessage("Opps! Some problem occurred while sending OTP please try again after some time");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                builder.show();
            }
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                mPhoneNumber = user_mobile.getText().toString();

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mobile_phone", mPhoneNumber));
                nameValuePairs.add(new BasicNameValuePair("fcm_token", FCMToken));

                Log.d("datap", nameValuePairs.toString());
                json = RestJsonClient.post(url, nameValuePairs);

                String message = json.getString("message");
                UserId = json.getString("userid");
                otp = json.getString("otp");
                adminOTP = json.getString("admin_otp");
                Log.e("OTP",json.getString("otp"));




            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    public void LoginUser() {

        FCMToken = preferances.getStringValue(CommonUtils.FCMTOCKEN,"");

        mPhoneNumber = user_mobile.getText().toString();
        String url = CommonUtils.APP_URL+"user_login";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("mobile_phone", mPhoneNumber)
                .add("fcm_token", FCMToken)

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


                            String success = json.getString("response");

                            if (success.equalsIgnoreCase("success")) {

                                String message = json.getString("message");
                                UserId = json.getString("userid");
                                otp = json.getString("otp");
                                adminOTP = json.getString("admin_otp");

                                AlertDialog.Builder builder = new AlertDialog.Builder(AddPhoneNumber.this);
                                builder.create();
                                builder.setMessage("OTP sent to your entered mobile number");

                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                });

                                builder.show();

                            }

                            else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(AddPhoneNumber.this);
                                builder.create();
                                builder.setMessage("Opps! Some problem occurred while sending OTP please try again after some time");

                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                });

                                builder.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


//    private void checkPermissionForSDK() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            int Permission1 = ActivityCompat.checkSelfPermission(AddPhoneNumber.this, Manifest.permission.CAMERA);
//            Log.e("LogPermission", Permission1 + "/m");
//            int Permission2 = ActivityCompat.checkSelfPermission(AddPhoneNumber.this, Manifest.permission.CALL_PHONE);
//            Log.e("LogPermission", Permission2 + "/m");
//            int Permission3 = ActivityCompat.checkSelfPermission(AddPhoneNumber.this, Manifest.permission.PROCESS_OUTGOING_CALLS);
//            Log.e("LogPermission", Permission3 + "/m");
//            int Permission4 = ActivityCompat.checkSelfPermission(AddPhoneNumber.this, Manifest.permission.ACCESS_COARSE_LOCATION);
//            Log.e("LogPermission", Permission4 + "/m");
//            int Permission5 = ActivityCompat.checkSelfPermission(AddPhoneNumber.this, Manifest.permission.ACCESS_FINE_LOCATION);
//            Log.e("LogPermission", Permission5 + "/m");
//            int Permission6 = ActivityCompat.checkSelfPermission(AddPhoneNumber.this, Manifest.permission.READ_PHONE_STATE);
//            Log.e("LogPermission", Permission6 + "/m");
//            int Permission7 = ActivityCompat.checkSelfPermission(AddPhoneNumber.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            Log.e("LogPermission", Permission7 + "/m");
//            int Permission8 = ActivityCompat.checkSelfPermission(AddPhoneNumber.this, Manifest.permission.READ_EXTERNAL_STORAGE);
//            Log.e("LogPermission", Permission8 + "/m");
//            ArrayList<String> list = new ArrayList<>();
//
//            if (Permission1 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.CAMERA);
//            }
//            if (Permission2 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.CALL_PHONE);
//            }
//            if (Permission3 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
//            }
//            if (Permission4 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
//            if (Permission5 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
//            if (Permission6 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.READ_PHONE_STATE);
//            }
//            if (Permission7 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            }
//            if (Permission8 != PackageManager.PERMISSION_GRANTED) {
//                list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//            }
//
//            if (list.size() > 0) {
//                ActivityCompat.requestPermissions(AddPhoneNumber.this, list.toArray(new String[list.size()]), Constants.MULTI_PERMISSION);
//            }
//        }
//    }


    @Override
    public void onBackPressed() {




            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.create();
            builder.setMessage("Want to Exit ?");

            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    preferances.setValue(CommonUtils.USERID, "");

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                }
            });


            builder.show();
        }



}
