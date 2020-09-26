package gss.com.bsell;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.Picasso.Picasso;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;

public class SliderActivity extends AppCompatActivity {

//     implements View.OnTouchListener

    private TouchImageView MainSlider;
    private String  MainImageURL;
    private String ImagePosition;
    ArrayList<String> advertiseArray = new ArrayList<>();
    private ImageView imageView;
    int position;
    String imageURl;
    private TextView tvImgNo;
    private int imageIndex;
    String type;
    int pointerId = 1;

    float initialX;

    String product_id,receiver_id, category_id, profilepic,chat_username, PhoneNumber;
    String userdemand = "1";
    String Imagecount, mobileno;
    LinearLayout call, l_chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        l_chat=findViewById(R.id.l_chat);
        call = findViewById(R.id.call_customer);

        product_id = getIntent().getStringExtra("product_id");
        category_id = getIntent().getStringExtra("category_id");
        receiver_id = getIntent().getStringExtra("receiver_id");
        userdemand = getIntent().getStringExtra("userdemand");
        Imagecount = getIntent().getStringExtra("Imagecount");
        PhoneNumber = getIntent().getStringExtra("PhoneNumber");
        profilepic = getIntent().getStringExtra("image");
        chat_username= getIntent().getStringExtra("chat_username");


        advertiseArray = getIntent().getStringArrayListExtra("ImageArray");

        type = getIntent().getStringExtra("type");
        imageIndex= getIntent().getIntExtra("imageIndex",0);

        tvImgNo=findViewById(R.id.tvImgNo);
        tvImgNo.setText(1+"/"+advertiseArray.size());

        MainSlider = findViewById(R.id.mainImageview);
        MainImageURL = CommonUtils.IMAGE_URL + advertiseArray.get(imageIndex);
        ImagePosition = "0";

        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        Picasso.with(getApplicationContext()).load(MainImageURL).error(R.mipmap.ic_launcher_round).into(MainSlider);

//        MainSlider.setOnTouchListener(SliderActivity.this);

        for (int i = 0; i < advertiseArray.size(); i++) {


            imageView = new ImageView(SliderActivity.this);

            imageView.setId(i);
            Display display = getWindowManager().getDefaultDisplay();
            int width = display.getWidth() / 5; // ((display.getWidth()*20)/100)
            int height = (display.getHeight() * 10) / 100;// ((display.getHeight()*30)/100)
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));

                Picasso.with(getApplicationContext()).load(CommonUtils.IMAGE_URL + advertiseArray.get(i))
                    .error(R.mipmap.ic_launcher_round).into(imageView);

            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setElevation(5);
            }
            imageView.setBackgroundResource(R.drawable.borders);
            final int index = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                        MainImageURL = CommonUtils.IMAGE_URL + advertiseArray.get(index);
                    Log.i("TAG", "The index is" + index);

                    Picasso.with(getApplicationContext()).load(MainImageURL)
                            .error(R.mipmap.ic_launcher_round).into(MainSlider);
                    tvImgNo.setText((index+1)+"/"+advertiseArray.size());
                    MainSlider.resetZoom();

                }
            });
            layout.addView(imageView);

        }


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestForCall();
            }
        });

        l_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(SliderActivity.this,ChatActivity.class);


                if(SharedPreferenceUtils.getInstance(getApplicationContext())
                        .getStringValue(CommonUtils.USERID,"").equalsIgnoreCase(receiver_id)){

                }else {
                    i.putExtra("receiver_id", receiver_id);
                    i.putExtra("product_id", product_id);
                    i.putExtra("userdemand", userdemand);
                    i.putExtra("category_id", category_id);
                    i.putExtra("image", profilepic);
                    i.putExtra("chat_username", chat_username);
                    i.putExtra("sender_id", SharedPreferenceUtils.getInstance(getApplicationContext())
                            .getStringValue(CommonUtils.USERID, ""));


                    startActivity(i);
                }
            }
        });


    }


    public void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse("tel:" + PhoneNumber));

        //callIntent.setData(Uri.parse("tel:" + "1234567890"));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(callIntent);
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

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions( this, new String[]{permission}, 0);
    }
//    @Override
//    public boolean onTouch(View view, MotionEvent event) {
//        // TODO Auto-generated method stub
//        int pointerIndex = event.getActionIndex();
//        int pointerId1 = event.getPointerId(pointerIndex);
//        pointerId = event.getPointerCount();
//
//
//
//
//    switch (event.getAction()) {
//        case MotionEvent.ACTION_DOWN:
//            initialX = event.getX();
//            break;
//        case MotionEvent.ACTION_UP:
//            float finalX = event.getX();
//            if (initialX > finalX) {
//                if (!(position >= advertiseArray.size() - 1)) {
//
//                    PositionArray.clear();
//                    position++;
//                    MainImageURL = CommonUtils.IMAGE_URL + advertiseArray.get(position);
//
//                    Picasso.with(getApplicationContext()).load(MainImageURL)
//                            .error(R.mipmap.ic_launcher_round).into(MainSlider);
//
//
//                }
//
//
//            } else {
//                if (position > 0) {
//
//                    PositionArray.clear();
//
//                    position--;
//
//                    MainImageURL = CommonUtils.IMAGE_URL + advertiseArray.get(position);
//
//                    Picasso.with(getApplicationContext()).load(MainImageURL)
//                            .error(R.mipmap.ic_launcher_round).into(MainSlider);
//
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "No More Images To Swipe",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//            break;
//    }
//
//        return false;
//    }







}
