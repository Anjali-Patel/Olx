package gss.com.bsell;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.listener.AsyncListener;
import gss.com.bsell.webrequest.WebRequest;

public class LoginActivity extends AppCompatActivity {
    TextView completeLogin;
    Toolbar toolbar;
    EditText username, etPassword;
    TextView forgot_password;
    ProgressDialog progressDialog;
    TextInputLayout usernameLayout, etPasswordLayout;
    SharedPreferenceUtils preferances;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferances = SharedPreferenceUtils.getInstance(this);
        requestForCameraPermission();
        setupToolBar();
        forgot_password = findViewById(R.id.forgot_password);
        completeLogin = findViewById(R.id.completeLogin);
        username = findViewById(R.id.username);
        etPassword = findViewById(R.id.etPassword);
        usernameLayout = findViewById(R.id.usernameLayout);
        etPasswordLayout = findViewById(R.id.etPasswordLayout);

        username.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameLayout.setErrorEnabled(false);
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPasswordLayout.setErrorEnabled(false);
            }
        });

        completeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().trim().equalsIgnoreCase("")) {
                    username.setText("");
                    usernameLayout.setError("Please enter valid Email Id");
                } else if (!isValidEmail(username.getText().toString().trim())) {
                    usernameLayout.setError("Invalid Email");
                } else if (etPassword.getText().toString().trim().equalsIgnoreCase("")) {
                    etPassword.setText("");
                    etPasswordLayout.setError("Please enter valid mobile number.");
                } else {
                    Validation_and_call();
                }
            }
        });
    }

    public void setupToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Email Login");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    protected void Validation_and_call() {
        AsyncListener asc = new AsyncListener() {
            @Override
            public void onTaskCompleted(String response) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                try {
                    JSONObject jsonobj = new JSONObject(response);
                    if (jsonobj.get("success").toString().equalsIgnoreCase("1")) {
                        Toast.makeText(getApplicationContext(), "Login Successfully!", Toast.LENGTH_LONG).show();
                        preferances.setValue(CommonUtils.ISREGISTERED, true);
                        preferances.setValue(CommonUtils.EMAIL_ID, username.getText().toString());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        finishAffinity();
                        startActivity(i);
                    } else if (jsonobj.get("success").toString().equalsIgnoreCase("2")) {
                        Toast.makeText(getApplicationContext(), "Wrong Email / Password!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile_phone", username.getText().toString());
            jsonObject.put("password", etPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        WebRequest.serverCall(LoginActivity.this, jsonObject, asc, CommonUtils.LOGIN_URL);
    }
    public void requestForCameraPermission() {
        final String permission = android.Manifest.permission.CAMERA;
        final String permissionreadstorage = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(LoginActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permission)) {
                showPermissionRationaleDialog("Test", permission);
            } else {
                requestForPermission(permission);
            }
        } else if (ContextCompat.checkSelfPermission(LoginActivity.this, permissionreadstorage) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissionreadstorage)) {
                showPermissionRationaleDialog("Test", permissionreadstorage);
            } else {
                requestForPermission(permissionreadstorage);
            }
        } else {
            
        }
    }
    private void showPermissionRationaleDialog(final String message, final String permission) {
        LoginActivity.this.requestForPermission(permission);
    }
    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, REQUEST_CAMERA_PERMISSION);
    }

}