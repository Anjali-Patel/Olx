package gss.com.bsell;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import gss.com.bsell.CheckNetworkSpeed.CheckInternetSpeed;
import gss.com.bsell.Model.UserProfileModel;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.webrequest.RestJsonClient;

import static android.app.Activity.RESULT_OK;


public class UserProfileForm extends Fragment implements
        BSImagePicker.OnSingleImageSelectedListener {

    private ImageView iv_add_photo;
    EditText id,fname,email,country,pincode,password,address,city;
    //id , fname, states, email,mobile_phone,country,pincode,password,address,city,profile_picture
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    String ImageName;
    public int PIC_CODE = 1;
    ArrayList<String> ImagesList = new ArrayList<String>();
    private static final int RESULT_LOAD_IMAGE = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private String userId;
    private Button submit;

    AutoCompleteTextView ac_states;
    ArrayAdapter<String> adapterstates;
    private ArrayList<String> states=new ArrayList<>();

    String ProfileURL="";

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;


    ArrayList<UserProfileModel> UserProfileModelArrayList = new ArrayList<>();
    JSONObject json;
    String response;

    Bitmap sourceImage;

    private SharedPreferenceUtils preferences;

    private  String url=CommonUtils.APP_URL+"profile_update";




    public UserProfileForm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_profile_form, container, false);
        //return inflater.inflate(R.layout.fragment_user_profile_form, container, false);
        //id , fname, states, email,mobile_phone,country,pincode,password,address,city,profile_picture
        preferences = SharedPreferenceUtils.getInstance(getActivity());

        iv_add_photo=v.findViewById(R.id.iv_add_photo);
        fname=v.findViewById(R.id.user_name);
        address=v.findViewById(R.id.user_addr);
        email=v.findViewById(R.id.userEmail);
        //mobile_phone=v.findViewById(R.id.user_mobile);
        country=v.findViewById(R.id.user_country);
        pincode=v.findViewById(R.id.User_pin);
        city=v.findViewById(R.id.user_city);
        //state=v.findViewById(R.id.user_state);
        progressBarHolder = (FrameLayout) v.findViewById(R.id.progressBarHolder);

        ac_states = (AutoCompleteTextView) v.findViewById(R.id.user_state);



        userId = preferences.getStringValue(CommonUtils.USERID,"");

        submit=v.findViewById(R.id.submitUserData);

        city.setText(preferences.getStringValue(CommonUtils.CurrentLocation,""));

        new StatesTask(getActivity()).execute();

        try {
            CheckInternetSpeed internet = new CheckInternetSpeed();
            int s = Integer.parseInt(internet.ConnectionQuality(getActivity()));
            if (s < 3){
                Toast.makeText(getActivity(), "Your internet speed is low", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){

        }

        setupList(v,getActivity());
        getcontacts();

        iv_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Selectimage();
//                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
//                        .build();
//                pickerDialog.show(getChildFragmentManager(), "picker");
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PostDemandAsync().execute(userId,fname.getText().toString(),ac_states.getText().toString(),email.getText().toString(), country.getText().toString(), pincode.getText().toString(),"12345",address.getText().toString(), city.getText().toString(), "1");
            }
        });

       // Creating the instance of ArrayAdapter containing list of fruit names
        return v;
    }



    public void Selectimage(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                Bitmap reducedSizeBitmap = getBitmap(String.valueOf(resultUri));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                reducedSizeBitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
                byte[] b1 = baos.toByteArray();
                ProfileURL = Base64.encodeToString(b1, Base64.DEFAULT);


                String Pic = String.valueOf(resultUri);
                Picasso.with(getContext()).load(Pic).error(R.drawable.ic_person_black_24dp).memoryPolicy(MemoryPolicy.NO_CACHE).into(iv_add_photo);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }




    private void setupList(View v, Context context) {

//        Uri path = Uri.parse("android.resource://gss.com.bsell/" + R.drawable.camera);
//        ImagesList.add(String.valueOf(path));

        mRecyclerView = v.findViewById(R.id.Images_listview);
        mRecyclerView.setHasFixedSize(true);
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);*/
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MultipleImagesAdapter(context, ImagesList);
        mRecyclerView.setAdapter(mAdapter);
        if(ImagesList.size()==0){
            mRecyclerView.setVisibility(View.GONE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);

        }
    }






//    private void activeGallery() {
//
//        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        startActivityForResult(intent, RESULT_LOAD_IMAGE);
//    }


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

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {

        String Profile = String.valueOf(uri);
//
//        String Image = getRightAngleImage(Profile);\
//        Picasso.with(getActivity()).load(getRightAngleImage(Profile)).memoryPolicy(MemoryPolicy.NO_CACHE).into(iv_add_photo);
        Bitmap bitmap = Bitmap.createBitmap(iv_add_photo.getWidth(), iv_add_photo.getHeight(), Bitmap.Config.RGB_565);

        iv_add_photo.setImageURI(uri);
        convertImageViewToBitmap(iv_add_photo);

//        Bitmap reducedSizeBitmap = getBitmap(getRightAngleImage(Profile));
//        Bitmap reducedSizeBitmap = getBitmap(getRightAngleImage(Profile));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b1 = baos.toByteArray();
        ProfileURL = Base64.encodeToString(b1, Base64.DEFAULT);
        ImagesList.add(ProfileURL);
//

        mAdapter.notifyDataSetChanged();

    }

    private Bitmap convertImageViewToBitmap(ImageView v){

        Bitmap bm=((BitmapDrawable)v.getDrawable()).getBitmap();

        return bm;
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
            Picasso.with(context1).load(ProfileURL).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.adapterImage);
/*
            holder.adapterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == MultipleImagesList.size() - 1)
                        dispatchTakePictureIntent();
                }
            });
*/
        }

        @Override
        public int getItemCount() {
            return MultipleImagesList.size();
        }
    }

    class PostDemandAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;
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
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);


            if (response.equalsIgnoreCase("1")) {

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.create();
                builder.setMessage("Your data saved successfully!");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(getActivity(),MainActivity.class));
                    }
                });
                builder.show();

            }
            else {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.create();
                builder.setMessage("Oops! Some problem occured while sending your data, please try again after sometime.");

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
                //id , fname, states, email,country,pincode,password,address,city,status, profile_picture
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("fname", params[1]));
                nameValuePairs.add(new BasicNameValuePair("states", params[2]));
                nameValuePairs.add(new BasicNameValuePair("email", params[3]));
                nameValuePairs.add(new BasicNameValuePair("country", params[4]));
                nameValuePairs.add(new BasicNameValuePair("pincode", params[5]));
                nameValuePairs.add(new BasicNameValuePair("password", params[6]));
                nameValuePairs.add(new BasicNameValuePair("address", params[7]));
                nameValuePairs.add(new BasicNameValuePair("city", params[8]));
                nameValuePairs.add(new BasicNameValuePair("status", params[9]));

                if (!ProfileURL.equalsIgnoreCase("")) {
                    nameValuePairs.add(new BasicNameValuePair("img1", ProfileURL));
                }



//                if (imageurl != null)
//                    nameValuePairs.add(new BasicNameValuePair("img1", imageurl));
                Log.d("datap", nameValuePairs.toString());
                json = RestJsonClient.post(url, nameValuePairs);
                response = json.getString("status");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

    }



    public void getcontacts() {


        UserProfileModelArrayList.clear();
        try {
//

            json = new DiscoverDataDemandAsynk().execute(userId).get();
            String success = json.getString("status");
//            if (success.equalsIgnoreCase("1")) {

            JSONObject newJson = (json.getJSONObject("data"));


           // JSONArray jsonArray = null;
            //jsonArray = json.getJSONArray("data");
          //  for (int i = 0; i < jsonArray.length(); i++) {

              //  JSONObject newJson = jsonArray.getJSONObject(i);
            if(!CommonUtils.isNullOrEmpty(newJson.getString("fname")))
                fname.setText(newJson.getString("fname"));
            if(!CommonUtils.isNullOrEmpty(newJson.getString("email")))
                email.setText(newJson.getString("email"));
            if(!CommonUtils.isNullOrEmpty(newJson.getString("country")))
                country.setText(newJson.getString("country"));
            if(!CommonUtils.isNullOrEmpty(newJson.getString("pincode")))
                pincode.setText(newJson.getString("pincode"));
            if(!CommonUtils.isNullOrEmpty(newJson.getString("address")))
                address.setText(newJson.getString("address"));
            if(!CommonUtils.isNullOrEmpty(newJson.getString("city")))
                city.setText(newJson.getString("city"));
            if(!CommonUtils.isNullOrEmpty(newJson.getString("states")))
                ac_states.setText(newJson.getString("states"));
            Log.e("profile_picture",newJson.getString("profile_picture"));
            Picasso.with(getActivity()).load(CommonUtils.PROFILE_IMG_URL+newJson.getString("profile_picture")).placeholder(R.drawable.ic_person_black_24dp)
                    .error(R.drawable.ic_person_black_24dp).into(iv_add_photo);

//                sourceImage = BitmapFactory.decodeFile(CommonUtils.PROFILE_IMG_URL+newJson.getString("profile_picture"));
//
//               if (sourceImage == null)
//               {
//                   Picasso.with(getActivity()).load(CommonUtils.PROFILE_IMG_URL+newJson.getString("profile_picture")).placeholder(R.mipmap.ic_launcher_round)
//                        .error(R.mipmap.ic_launcher_round).into(iv_add_photo);
//               }
//               else{
//                   float angle = 90;
//                   Bitmap rotatedImage=rotateImage(sourceImage,angle);
//                   iv_add_photo.setImageBitmap(rotatedImage);
//               }


                mAdapter.notifyDataSetChanged();
         //   }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public class DiscoverDataDemandAsynk extends AsyncTask<String, String, JSONObject> {
        private JSONObject jsonResponse;

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String url= CommonUtils.APP_URL+"view_profile";
                List<NameValuePair> nameValuePairs=new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("id",params[0]));
                json = RestJsonClient.post(url,nameValuePairs);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public class StatesTask extends AsyncTask<String, String, JSONObject> {
        private JSONObject json;
        private Context context;
        private ProgressDialog progress;
        private String district;

        public StatesTask(Context context) {
            this.context = context;
            progress=new ProgressDialog(context);
        }

        public StatesTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Please wait a moment... ");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                String url = CommonUtils.APP_URL + "get_all_states";

                json = RestJsonClient.connect(url);
                Log.e("StatesTask", json.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if(progress!=null)
                progress.dismiss();
            try {
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    JSONArray jArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            JSONObject jObj = jArray.getJSONObject(i);

                            if(jObj.has("state_name")) {
                                states.add(jObj.getString("state_name"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    adapterstates = new ArrayAdapter<String>
                            (getActivity(), R.layout.states_list, states);
                    //Getting the instance of AutoCompleteTextView
                    ac_states.setThreshold(1);//will start working from first character
                    ac_states.setAdapter(adapterstates);//setting the adapter data into the AutoCompleteTextView


                    adapterstates.notifyDataSetChanged();




                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getRightAngleImage(String photoPath) {
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }
            return rotateImage(degree, photoPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoPath;
    }
/*
    public static Bitmap rotateImage(Bitmap sourceImage, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }
*/


    private String rotateImage(int degree, String imagePath) {
        if (degree <= 0) {
            return imagePath;
        }
        try {
            Bitmap b = BitmapFactory.decodeFile(imagePath);
            Matrix matrix = new Matrix();
            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
            }
            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);
            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();
            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    public Bitmap getBitmap(String path) {

        Uri uri = Uri.parse(path);
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getActivity().getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getActivity().getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
//            Log.e("", e.getMessage(), e);
            return null;
        }
    }


}
