package gss.com.bsell;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gss.com.bsell.Adapter.SelectedCategoryAdapter;
import gss.com.bsell.CheckNetworkSpeed.CheckInternetSpeed;
import gss.com.bsell.Model.CategoryDisplayModel;
import gss.com.bsell.Model.SelectedCategoryDataModel;
import gss.com.bsell.Model.SelectedImageModel;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.categories.CategoriesPosting;
import gss.com.bsell.webrequest.RestJsonClient;

import static android.app.Activity.RESULT_OK;

public class AddDemandFragment extends Fragment implements RefreshInterface, BSImagePicker.OnMultiImageSelectedListener  {

    private RelativeLayout relativeLayout;
    private ImageView iv_add_photo;

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    String ImageName;
    public int PIC_CODE = 1;
    ArrayList<String> ImagesList = new ArrayList<String>();
    ArrayList<CategoryDisplayModel> catDisplayList = new ArrayList<>();

    ArrayList<SelectedCategoryDataModel> SelectedcatDisplayList = new ArrayList<>();
    ArrayList<String> ImagesListSelected = new ArrayList<String>();
    ArrayList<SelectedImageModel> SelectedImagesList = new ArrayList<SelectedImageModel>();
    ArrayList<SelectedImageModel> SelectedImagesListInBase64 = new ArrayList<SelectedImageModel>();

    private RecyclerView mRecyclerView;
    private RecyclerView mCatDisplayRv;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mAdapterCatDisplay;
    private RecyclerView.LayoutManager mLayoutManager;
    Bundle bundle = new Bundle();
    private static final int RESULT_LOAD_IMAGE = 1;
    private Button submit;
    Spinner sp_categories;
    EditText et_description,et_product_name,et_mobile, et_product_type;
    private  String url=CommonUtils.APP_URL+"uploadproduct";
    private String user_demands="1",categoriesSelected;
    //    private ProgressBar progress_bar;
    private Fragment fragment;
    private MyViewPager viewPager;
    private DemandFragment demandFragment;
    private TextView tv_categories;
    private int categoryRequest=1;
    private String  sub_cat="",sub_catid="",category="",cat_id="",sub_cat_2id="",sub_cat2="",sub_cat3_id="",sub_cat3="";
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    private SharedPreferenceUtils preferences;
    String adType = "buy";

    String Modeltitle;


    FrameLayout progressBarHolder;
    public AddDemandFragment() {
        // Required empty public constructor
    }
    StringBuilder sb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_demand, container, false);
        progressBarHolder = (FrameLayout) v.findViewById(R.id.progressBarHolder);

        preferences = SharedPreferenceUtils.getInstance(getActivity());


        Intent i=getActivity().getIntent();
        if(i.getExtras()!=null) {

            sub_cat = i.getStringExtra("sub_cat");
            sub_catid = i.getStringExtra("sub_catid");
            category = i.getStringExtra("category");
            cat_id = i.getStringExtra("cat_id");
            sub_cat_2id = i.getStringExtra("sub_cat2_id");
            sub_cat2 = i.getStringExtra("sub_cat2");
            sub_cat3_id = i.getStringExtra("sub_cat3_id");
            sub_cat3 = i.getStringExtra("sub_cat3");

            Modeltitle = i.getStringExtra("sub_cat3");

        }

        relativeLayout = v.findViewById(R.id.rl_my_demand);
        iv_add_photo=v.findViewById(R.id.iv_add_photo);
        sp_categories=v.findViewById(R.id.sp_categories);
        et_description=v.findViewById(R.id.et_description);
        submit=v.findViewById(R.id.submit);
        et_mobile=v.findViewById(R.id.et_mobile);
        et_product_name=v.findViewById(R.id.et_product_name);
        et_product_type=v.findViewById(R.id.et_product_type);
//        progress_bar=v.findViewById(R.id.progress_bar);
        viewPager = (MyViewPager) getActivity().findViewById(R.id.viewpager);
        tv_categories = (TextView) v.findViewById(R.id.tv_categories);
        preferences = SharedPreferenceUtils.getInstance(getActivity());
        mCatDisplayRv = v.findViewById(R.id.rc_display_cat);


        et_mobile.setText(preferences.getStringValue(CommonUtils.USERMOBILE,""));

        SelectedImagesList = CommonUtils.SELECTEDIMAGES;
        for (int j = 0; j < SelectedImagesList.size(); j++) {
            final SelectedImageModel Items = SelectedImagesList.get(j);
            ImagesList.add(Items.getImage());
        }

        SelectedImagesListInBase64 = CommonUtils.SELECTEDIMAGESINBASE64;
        for (int j = 0; j < SelectedImagesListInBase64.size(); j++) {
            final SelectedImageModel Items = SelectedImagesListInBase64.get(j);
            ImagesListSelected.add(Items.getImageInBase64());
        }


        sb = new StringBuilder();
        if(!isNullOrEmpty(category)){
            sb.append("Category \n"+category+"\n");
        }
        if(!isNullOrEmpty(sub_cat)){
            sb.append("subCategory \n"+sub_cat+"\n");
        }
        if(!isNullOrEmpty(sub_cat2)){
            sb.append("brand \n"+sub_cat2+"\n");
        }
        if(!isNullOrEmpty(sub_cat3)){
            sb.append("model \n"+sub_cat3+"\n");
        }

        if(!isNullOrEmpty(sb.toString())) {
            tv_categories.setText("Select Category");
            addCatToDisplay(catDisplayList);
        }

        try {
            CheckInternetSpeed internet = new CheckInternetSpeed();
            int s = Integer.parseInt(internet.ConnectionQuality(getActivity()));
            if (s < 3){
                Toast.makeText(getActivity(), "Your internet speed is low", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){

        }

        SelectedcatDisplayList = CommonUtils.SELECTEDCATEGORY;
        if(SelectedcatDisplayList.size() > 0){
            et_product_type.setVisibility(View.VISIBLE);
            et_product_type.setText(preferences.getStringValue(CommonUtils.MAINCATEGORYNAME, ""));
            et_product_name.setText(preferences.getStringValue(CommonUtils.PRODUCTNAME, ""));
        }



//        Uri path = Uri.parse("android.resource://gss.com.bsell/" + R.drawable.camera);
//        iv_add_photo.setImageURI(path);
        setupList(v,getActivity());
        //setupCatDisplayRecyclerview(v,getActivity());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        mCatDisplayRv.setLayoutManager(manager);
        // members.setAdapter(adapter);

        mAdapter = new SelectedCategoryAdapter(getActivity(), SelectedcatDisplayList, adType);
        mCatDisplayRv.setAdapter(mAdapter);


        iv_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPickerDialog(view);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SelectedcatDisplayList.size() > 0) {
                    StringBuilder nameBuilder = new StringBuilder();

                    for (SelectedCategoryDataModel n : SelectedcatDisplayList) {
                        nameBuilder.append(n.getCatId()).append(",");                        // can also do the following
                    }

                    categoriesSelected = nameBuilder.deleteCharAt(nameBuilder.length() - 1).toString();

                }

                if (et_product_name.getText().toString().trim() == null ||  et_product_name.getText().toString().trim().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.create();
                    builder.setMessage("Plese enter Product Name!");

                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });



                    builder.show();

                }
                else if (SelectedcatDisplayList.size() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.create();
                    builder.setMessage("Plese select product category!");

                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                }


                else if (ImagesList.size() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.create();
                    builder.setMessage("Plese insert at least one image of product!");

                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    builder.show();

                }

                else {

                    String Mylat = preferences.getStringValue(CommonUtils.LATTITUTE,"");
                    String Mylon = preferences.getStringValue(CommonUtils.LONGITUDE,"");
                    String Address = preferences.getStringValue(CommonUtils.CurrentLocation,"");
                    String SimilarCatId = preferences.getStringValue(CommonUtils.SIMILARPRODUCTID, "");

                    new PostDemandAsync().execute(preferences.getStringValue(CommonUtils.USERID,""), categoriesSelected
                            , et_description.getText().toString(), Address,
                            user_demands, et_product_name.getText().toString(),
                            "Product Title", Mylat, Mylon, "Product Price",SimilarCatId);

                }





            }
        });

        tv_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.SELECTEDCATEGORY.clear();
                Intent intent=new Intent(getActivity(),CategoriesPosting.class);
                intent.putExtra("adType","buy");
                startActivity(intent);
            }
        });



        return v;


    }

    private void addCatToDisplay(ArrayList<CategoryDisplayModel> catDisplayList) {
        catDisplayList.clear();
        catDisplayList.add(new CategoryDisplayModel("Category",category));
        catDisplayList.add(new CategoryDisplayModel("Subcategory",sub_cat));
        catDisplayList.add(new CategoryDisplayModel("Brand",sub_cat2));
        catDisplayList.add(new CategoryDisplayModel("Model",sub_cat3));

    }

    public void setupCatDisplayRecyclerview(View v,Context context){
        mCatDisplayRv = v.findViewById(R.id.rc_display_cat);
        mCatDisplayRv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCatDisplayRv.setLayoutManager(layoutManager);
//        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        mCatDisplayRv.setItemAnimator(new DefaultItemAnimator());
        mAdapterCatDisplay = new CatDisplayAdapter(catDisplayList);
        mCatDisplayRv.setAdapter(mAdapterCatDisplay);
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    @Override
    public void refreshAdapterFragmentDemand(){

//        Fragment page = getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 1);
//        DemandFragment mFragment = (DemandFragment) page;
        BuyFragment.ViewPagerAdapter adapter= (BuyFragment.ViewPagerAdapter) viewPager.getAdapter();
        demandFragment = (DemandFragment) adapter.getItem(1);
        demandFragment.refreshAdapter();


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



    private void openPickerDialog(View view) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.setTitle("Add Photo");
        Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnChoosePath)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String deviceMan = android.os.Build.MANUFACTURER;
                        String deviceBrand = Build.BRAND;

                        dialog.dismiss();

                        BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                                    .setMaximumDisplayingImages(Integer.MAX_VALUE)
                                    .isMultiSelect()
                                    .setMinimumMultiSelectCount(2)
                                    .setMaximumMultiSelectCount(10)
                                    .build();
                            pickerDialog.show(getChildFragmentManager(), "Picker");

//                        if (deviceMan.equalsIgnoreCase("Xiaomi")){
//                            activeGallery();
//                        }
//                        else if (deviceBrand.equalsIgnoreCase("Nokia")) {
//                            activeGallery();
//                        }
//                        else {
//                            BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
//                                    .setMaximumDisplayingImages(Integer.MAX_VALUE)
//                                    .isMultiSelect()
//                                    .setMinimumMultiSelectCount(2)
//                                    .setMaximumMultiSelectCount(10)
//                                    .build();
//                            pickerDialog.show(getChildFragmentManager(), "Picker");
//                        }
                    }
//                    @Override public void onClick(View v) {
//                        activeGallery();
//                        dialog.dismiss();
//                    }
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

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

        CommonUtils.SELECTEDIMAGES.clear();
        CommonUtils.SELECTEDIMAGESINBASE64.clear();

        for (int i=0; i < uriList.size(); i++) {

            SelectedImageModel model = new SelectedImageModel();
            model.setImage(String.valueOf(uriList.get(i)));
            CommonUtils.SELECTEDIMAGES.add(model);

            ImagesList.add(String.valueOf(uriList.get(i)));

            Bitmap reducedSizeBitmap = getBitmap(String.valueOf(uriList.get(i)));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            reducedSizeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b1 = baos.toByteArray();
            String ProfileURL = Base64.encodeToString(b1, Base64.DEFAULT);

            model.setImageInBase64(ProfileURL);
            CommonUtils.SELECTEDIMAGESINBASE64.add(model);

            ImagesListSelected.add(ProfileURL);
            mAdapter.notifyDataSetChanged();

            if(ImagesList.size()==0){
                mRecyclerView.setVisibility(View.GONE);
            }
            else {
                mRecyclerView.setVisibility(View.VISIBLE);

            }

        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "gss.com.bsell.GenericProvider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    private void activeGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (imageFilePath != null) {
                if(ImagesList.size()<10)
                    ImagesList.add(imageFilePath);

                SelectedImageModel model = new SelectedImageModel();
                model.setImage(String.valueOf(imageFilePath));
                CommonUtils.SELECTEDIMAGES.add(model);

                Bitmap reducedSizeBitmap = getBitmap("file:"+String.valueOf(imageFilePath));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                reducedSizeBitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
                byte[] b1 = baos.toByteArray();
                String ProfileURL = Base64.encodeToString(b1, Base64.DEFAULT);

                model.setImageInBase64(ProfileURL);
                CommonUtils.SELECTEDIMAGESINBASE64.add(model);
                ImagesListSelected.add(ProfileURL);

                mAdapter.notifyDataSetChanged();

                if(ImagesList.size()==0){
                    mRecyclerView.setVisibility(View.GONE);
                }
                else {
                    mRecyclerView.setVisibility(View.VISIBLE);
//                    mRecyclerView.setLayoutFrozen(true);

                }

//                Picasso.with(getActivity()).load("file:" + imageFilePath).memoryPolicy(MemoryPolicy.NO_CACHE).into(sell_image);

            }
        }else if (requestCode == 200 &&
                resultCode == Activity.RESULT_OK) {

            String[] all_path = data.getStringArrayExtra("all_path");

            ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();


            for (String string : all_path) {
                CustomGallery item = new CustomGallery();
                item.sdcardPath = string;
                String a = item.sdcardPath;
                String[] filePathColumn = { item.sdcardPath };

                String imageEncoded="";

                Uri uri = Uri.parse(item.sdcardPath);

                dataT.add(item);
                mArrayUri.add(uri);

                imageEncoded = filePathColumn[0];
                ImagesList.add(imageEncoded);

            }

            mAdapter.notifyDataSetChanged();


            if(ImagesList.size()==0){
                mRecyclerView.setVisibility(View.GONE);
            }
            else {
                mRecyclerView.setVisibility(View.VISIBLE);
//                    mRecyclerView.setLayoutFrozen(true);

            }

        }

        else if (requestCode == RESULT_LOAD_IMAGE &&
                resultCode == RESULT_OK) {

            String imageEncoded="";
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                    Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    ImagesList.add(imageEncoded);
                    cursor.close();
                }
            }

            mAdapter.notifyDataSetChanged();


            if(ImagesList.size()==0){
                mRecyclerView.setVisibility(View.GONE);
            }
            else {
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }

        else if (ImagesList.isEmpty()) {
            getActivity().finish();
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

            if (MultipleImagesList.get(position).contains("file:")){
                Picasso.with(context1).load(MultipleImagesList.get(position)).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.adapterImage);
            }
            else {
                Picasso.with(context1).load("file:" + MultipleImagesList.get(position)).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.adapterImage);
            }
        }

        @Override
        public int getItemCount() {
            return MultipleImagesList.size();
        }
    }

    public class CatDisplayAdapter extends RecyclerView.Adapter<CatDisplayAdapter.MyViewHolder> {


        private ArrayList<CategoryDisplayModel> catDisplayList;

        public CatDisplayAdapter(ArrayList<CategoryDisplayModel> catDisplayList) {

            this.catDisplayList = catDisplayList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_display_listitem, parent, false);
            return new CatDisplayAdapter.MyViewHolder(v);

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.tvCatName.setText(catDisplayList.get(position).getCatName());
            holder.tvCatVal.setText(catDisplayList.get(position).getCatValue());

//            if(position==(catDisplayList.size()-1)){
//                holder.divider.setVisibility(View.GONE);
//            }else {
//                holder.divider.setVisibility(View.VISIBLE);
//
//            }
        }

        @Override
        public int getItemCount() {
            return catDisplayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView tvCatName,tvCatVal;
            //View divider;
            public MyViewHolder(View itemView) {
                super(itemView);
                tvCatVal=itemView.findViewById(R.id.tvCatVal);
                tvCatName=itemView.findViewById(R.id.tvCatName);
                //divider=itemView.findViewById(R.id.divider);
            }
        }
    }

    class PostDemandAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;


        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("category_id", params[1]));
                nameValuePairs.add(new BasicNameValuePair("description", params[2]));
                nameValuePairs.add(new BasicNameValuePair("location", params[3]));
                nameValuePairs.add(new BasicNameValuePair("user_demands", params[4]));
                nameValuePairs.add(new BasicNameValuePair("product_name", params[5]));
                nameValuePairs.add(new BasicNameValuePair("title", params[6]));
                nameValuePairs.add(new BasicNameValuePair("latitude", params[7]));
                nameValuePairs.add(new BasicNameValuePair("longitude", params[8]));
                nameValuePairs.add(new BasicNameValuePair("price", params[9]));
                nameValuePairs.add(new BasicNameValuePair("sm_product", params[10]));



                for (int i=0;i<ImagesList.size();i++){
                    nameValuePairs.add(new BasicNameValuePair("img"+(i+1), ImagesListSelected.get(i)));

                   // nameValuePairs.add(new BasicNameValuePair("img"+(i+1), ImagesList.get(i).replace("file://","")));

//                    nameValuePairs.add(new BasicNameValuePair("img"+(i+1), ImagesList.get(i)));

                }

                String Url = (CommonUtils.APP_URL)+"add_product";

                Log.d("datap", nameValuePairs.toString());
                json = RestJsonClient.post(Url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
//            progress_bar.setVisibility(View.VISIBLE);

//            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);


            try {
                String success = json.getString("success");


                if (success.equalsIgnoreCase("1")) {

                    Toast.makeText(getActivity(), "Demand added successfully!",
                            Toast.LENGTH_LONG).show();

                    outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);

                    CommonUtils.SELECTEDCATEGORY.clear();
                    CommonUtils.SELECTEDIMAGES.clear();
                    CommonUtils.SELECTEDIMAGESINBASE64.clear();


                    tv_categories.setText("Select Category");
                    viewPager.setCurrentItem(1, true);
                    refreshAdapterFragmentDemand();

                }

                else {

                    Toast.makeText(getActivity(), "Opps! Some problem occured while adding your product, please ensure your location services are on at the highest accuracy.",
                            Toast.LENGTH_LONG).show();

                    outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
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
