package gss.com.bsell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginWithPhoneActivity extends AppCompatActivity {
    TextView completeLogin;
    EditText phone_number;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone);

        setupToolBar();
        phone_number = findViewById(R.id.phone_number);
        completeLogin = findViewById(R.id.completeLogin);

        completeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_number.getText().toString().trim().length()<10){
                    Toast.makeText(LoginWithPhoneActivity.this, "Please Enter Your Mobile Number", Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    finishAffinity();
                    startActivity(i);
                }
            }
        });
    }
    public void setupToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Phone");
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

}
