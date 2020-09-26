package gss.com.bsell;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



public class AddSellingDetails extends AppCompatActivity {
    private ImageView sell_image,backbutton;
    Button Next;
    EditText enterAmount;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    String[] imageFilePaths;
    String ImageName;
    public int PIC_CODE = 1;
    ArrayList<String> ImagesList = new ArrayList<String>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    TextView toolbar_title;
    ImageView add_images;
    Bundle bundle = new Bundle();
    private static final int RESULT_LOAD_IMAGE = 1;
    private String comprImageName="";
    private int COMPR_PIC_CODE=1;
    String comprImageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_selling_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Sell");
        add_images = findViewById(R.id.add_images);
        sell_image = findViewById(R.id.sell_image);
        backbutton= findViewById(R.id.backbutton);
        Next = findViewById(R.id.next_sell1);
        enterAmount = findViewById(R.id.enterAmount);
        mRecyclerView = findViewById(R.id.Images_listview);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MultipleImagesAdapter(getApplicationContext(), ImagesList);
        mRecyclerView.setAdapter(mAdapter);




//        dispatchTakePictureIntent();
        openPickerDialog(AddSellingDetails.this);
        add_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPickerDialog(AddSellingDetails.this);
//                dispatchTakePictureIntent();
            }
        });
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!enterAmount.getText().toString().trim().equalsIgnoreCase("")) {
//                    Intent i = new Intent(getApplicationContext(), PutInCategoryActivity.class);
                    Intent i = new Intent(getApplicationContext(), AddDescriptionActivity.class);
                    i.putExtra("amount", enterAmount.getText().toString().trim());
                    i.putExtra("imagelist", ImagesList);
                    startActivity(i);
                } else {
                    Toast.makeText(AddSellingDetails.this, "Please enter amount first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }




    private void openPickerDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.setTitle("Add Photo");
        Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnChoosePath)
                .setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        activeGallery();
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.btnTakePhoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        dispatchTakePictureIntent();
                        dialog.dismiss();
                    }
                });

        // show dialog on screen
        dialog.show();
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(AddSellingDetails.this, "gss.com.bsell.GenericProvider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = Environment.getExternalStorageDirectory();
        File f = new File(storageDir + "/OLXImages");

        if (!f.exists()) {
            f.mkdirs();
        }
        ImageName = "Image" + PIC_CODE + ".png";
        PIC_CODE++;
        Log.e("ImageName", ImageName);
        File f1 = new File(f, ImageName);
        imageFilePath = f1.getAbsolutePath();
        return f1;
    }

    private File createCompressedImageFile() throws IOException {
        File storageDir = Environment.getExternalStorageDirectory();
        File f = new File(storageDir + "/OLXImages");

        if (!f.exists()) {
            f.mkdirs();
        }
        comprImageName = "ComprImage" + COMPR_PIC_CODE + ".png";
        COMPR_PIC_CODE++;
        Log.e("ComprImageName", comprImageName);
        File f1 = new File(f, comprImageName);
        comprImageFilePath = f1.getAbsolutePath();
        return f1;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {

        if (requestcode == REQUEST_CAPTURE_IMAGE && resultcode == RESULT_OK) {
            if (imageFilePath != null) {
//                if(ImagesList.size()<5)
                    ImagesList.add(imageFilePath);
                mAdapter.notifyDataSetChanged();
                Picasso.with(getApplicationContext()).load("file:" + imageFilePath).memoryPolicy(MemoryPolicy.NO_CACHE).into(sell_image);

            }
        } else if (requestcode == RESULT_LOAD_IMAGE &&
                resultcode == RESULT_OK && null != data) {

            String imageEncoded="";
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);
                        ImagesList.add(imageEncoded);
                        cursor.close();

                    }
                }






//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver()
//                    .query(selectedImage, filePathColumn, null, null,
//                            null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();

//            compressedImageFile = new Compressor(this).compressToFile(new File(picturePath));
//            if(ImagesList.size()<5)
//                ImagesList.add(picturePath);
            Picasso.with(getApplicationContext()).load("file:" + imageEncoded).memoryPolicy(MemoryPolicy.NO_CACHE).into(sell_image);
            mAdapter.notifyDataSetChanged();
        }else if (ImagesList.isEmpty()) {
                finish();
        }
    }

    public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.MyViewHolder> {
        Context context1;
        private ArrayList<String> MultipleImagesList;

        public MultipleImagesAdapter(Context context, ArrayList<String> ImagesList) {
            context1 = context;
            MultipleImagesList = ImagesList;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView adapterImage;

            public MyViewHolder(View v) {
                super(v);
                adapterImage = (ImageView) v.findViewById(R.id.adapterImageview);
            }
        }

        @NonNull
        @Override
        public MultipleImagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_image_listitem, parent, false);
            return new MultipleImagesAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MultipleImagesAdapter.MyViewHolder holder, final int position) {
            Picasso.with(context1).load("file:" + MultipleImagesList.get(position)).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.adapterImage);
            holder.adapterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (position == MultipleImagesList.size() - 1)
                        dispatchTakePictureIntent();*/
                    Picasso.with(context1).load("file:" + MultipleImagesList.get(position)).memoryPolicy(MemoryPolicy.NO_CACHE).into(sell_image);

                }
            });
        }

        @Override
        public int getItemCount() {
            return MultipleImagesList.size();
        }
    }
}
