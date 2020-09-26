package gss.com.bsell;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.ArrayList;

import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;

public class ImageSlider extends AppCompatActivity implements
        BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout mAdSlider;
    ImageButton arrowleft, arrowright;
    ArrayList<String> ImageArray = new ArrayList<>();
    int myposition;
    int current_image;
    private LinearLayout l_chat;
    LinearLayout call;
    private int SLIDER_COUNT=2;
    private int previousPosition = 0;
    String product_id,receiver_id, category_id, profilepic,chat_username;
    String userdemand = "1";
    String Imagecount, mobileno;
    TextView count;
    int ImageCountInt;
    String PhoneNumber;

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        arrowleft = findViewById(R.id.arrow_left);
        arrowright = findViewById(R.id.arrow_right);
        l_chat=findViewById(R.id.l_chat);
        call = findViewById(R.id.call_customer);
        count = findViewById(R.id.count);

        product_id = getIntent().getStringExtra("product_id");
        category_id = getIntent().getStringExtra("category_id");
        receiver_id = getIntent().getStringExtra("receiver_id");
        userdemand = getIntent().getStringExtra("userdemand");
        Imagecount = getIntent().getStringExtra("Imagecount");
        PhoneNumber = getIntent().getStringExtra("PhoneNumber");
        profilepic = getIntent().getStringExtra("image");
        chat_username= getIntent().getStringExtra("chat_username");




        ImageCountInt = Integer.parseInt(Imagecount);

        count.setText("1 / "+Imagecount);

        setupAdvertiseLayout();
    }

    public void setupAdvertiseLayout() {
        ImageArray= getIntent().getStringArrayListExtra("ImageArray");
        mAdSlider = findViewById(R.id.slider);
        mAdSlider.stopAutoCycle();

        for (String advName : ImageArray) {

            DefaultSliderView textSliderView = new DefaultSliderView(this);
            textSliderView.image(CommonUtils.IMAGE_URL+advName)
//                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.setScaleType(BaseSliderView.ScaleType.FitCenterCrop);

            mAdSlider.addSlider(textSliderView);


        }

// if (ImageArray.size() <= 2) {
//                mAdSlider.stopAutoCycle();
//                mAdSlider.setPagerTransformer(false, new BaseTransformer() {
//                    @Override
//                    protected void onTransform(View view, float v) {
//                    }
//                });
//                //TODO: disable indicator
//            }


        mAdSlider.removeSliderAt(ImageArray.size());
        mAdSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mAdSlider.setCustomAnimation(new DescriptionAnimation());
        //mAdSlider.setDuration(4000);
        mAdSlider.addOnPageChangeListener(this);
        mAdSlider.stopAutoCycle();
//        mAdSlider.setPresetTransformer(SliderLayout.Transformer.Fade);
        mAdSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        mAdSlider.getPagerIndicator().setDefaultIndicatorColor(R.color.black, R.color.blue);
        mAdSlider.setCurrentPosition(0,true);

        //mAdSlider.startAutoCycle();

        arrowleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdSlider.getCurrentPosition()< ImageArray.size()){
                    mAdSlider.setCurrentPosition(mAdSlider.getCurrentPosition()-1);
                    //count.setText((ImageCountInt+1)+" / "+Imagecount);
                }
                // Toast.makeText(ImageSlider.this, mAdSlider.getCurrentPosition(), Toast.LENGTH_SHORT).show();

            }
        });

        arrowright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdSlider.getCurrentPosition()< ImageArray.size()){
                    mAdSlider.setCurrentPosition(mAdSlider.getCurrentPosition()-1);
                    //count.setText((ImageCountInt-1)+" / "+Imagecount);

                }


            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestForCall();
            }
        });

        l_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(ImageSlider.this,ChatActivity.class);


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

    @Override
    public void onSliderClick(BaseSliderView slider) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        //        if (position == 61){
//           mAdSlider.removeSliderAt(63);
//            mAdSlider.removeSliderAt(64);
//
//        }
//        else if (position == 62){
//            mAdSlider.removeSliderAt(63);
//            mAdSlider.removeSliderAt(64);
//
//        }
        // mAdSlider.removeSliderAt(1);

        mAdSlider.stopAutoCycle();
    }

    @Override
    public void onPageSelected(int position) {

//        if(position == 0 || position == 1) {
//            mAdSlider.moveNextPosition();
//
//        } else  {
//            mAdSlider.moveNextPosition();
//
//        }

        previousPosition = position;

        if (previousPosition == 0) {
            count.setText("1 / "+Imagecount);
        }

        else if (previousPosition >= 1) {
            int newCount = previousPosition + 1;
            count.setText(String.valueOf(newCount) + " / " + Imagecount);
        }

        else if (previousPosition <= 1) {
            int newCount = previousPosition - 1;
            count.setText(String.valueOf(newCount) + " / " + Imagecount);
        }


    }

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions( this, new String[]{permission}, 0);
    }

    public void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse("tel:" + PhoneNumber));

        //callIntent.setData(Uri.parse("tel:" + "1234567890"));
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



    @Override
    public void onPageScrollStateChanged(int state) {
        mAdSlider.stopAutoCycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdSlider.startAutoCycle();
    }

    @Override
    protected void onStop() {
        mAdSlider.stopAutoCycle();
        super.onStop();
    }



}
