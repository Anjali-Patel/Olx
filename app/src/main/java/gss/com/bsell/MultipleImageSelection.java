package gss.com.bsell;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;

public class MultipleImageSelection extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener{

    private ImageView ivImage1, ivImage2, ivImage3, ivImage4, ivImage5, ivImage6, ivImage7, ivImage8, ivImage9, ivImage10, ivImage11, ivImage12;

    ArrayList<Uri> ImagesListSelected = new ArrayList<Uri>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_image_selection);

        ivImage1 = findViewById(R.id.iv_image1);
        ivImage2 = findViewById(R.id.iv_image2);
        ivImage3 = findViewById(R.id.iv_image3);
        ivImage4 = findViewById(R.id.iv_image4);
        ivImage5 = findViewById(R.id.iv_image5);
        ivImage6 = findViewById(R.id.iv_image6);

        ivImage7 = findViewById(R.id.iv_image7);
        ivImage8 = findViewById(R.id.iv_image8);
        ivImage9 = findViewById(R.id.iv_image9);
        ivImage10 = findViewById(R.id.iv_image10);
        ivImage11 = findViewById(R.id.iv_image11);
        ivImage12 = findViewById(R.id.iv_image12);

        findViewById(R.id.tv_single_selection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MultipleImageSelection.this, SellActivity.class);
                intent.putExtra("ImagesListSelected", ImagesListSelected);
                startActivity(intent);
//                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
//                        .build();
//                pickerDialog.show(getSupportFragmentManager(), "picker");
            }
        });
        findViewById(R.id.tv_multi_selection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(2)
                        .setMaximumMultiSelectCount(10)
                        .build();
                pickerDialog.show(getSupportFragmentManager(), "picker");
            }
        });
    }


    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        Glide.with(MultipleImageSelection.this).load(uri).into(ivImage2);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

        for (int i=0; i < uriList.size(); i++) {
            if (i >= 10) return;
            ImageView iv;
            switch (i) {
                case 0:
                    iv = ivImage1;
                    break;
                case 1:
                    iv = ivImage2;
                    break;
                case 2:
                    iv = ivImage3;
                    break;
                case 3:
                    iv = ivImage4;
                    break;
                case 4:
                    iv = ivImage5;
                    break;
                case 5:
                    iv = ivImage6;
                    break;
                case 6:
                    iv = ivImage7;
                    break;
                case 7:
                    iv = ivImage8;
                    break;
                case 8:
                    iv = ivImage9;
                    break;
                case 9:
                    iv = ivImage10;
                    break;
                case 10:
                    iv = ivImage11;
                    break;
                case 11:
                default:
                    iv = ivImage12;
            }
            Glide.with(this).load(uriList.get(i)).into(iv);
            ImagesListSelected.add(uriList.get(i));
        }
    }
}
