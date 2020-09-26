package lockscreenads.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gss.com.bsell.R;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import lockscreenads.models.ImageModel;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.arch.lifecycle.Lifecycle.State.INITIALIZED;
import static android.net.wifi.SupplicantState.UNINITIALIZED;
import static lockscreenads.ApplicationClass.KEY_ImagePath;
import static lockscreenads.ApplicationClass.KEY_lastID;
import static lockscreenads.ApplicationClass.KEY_prefName;
import static lockscreenads.ApplicationClass.imageModels;
import static lockscreenads.activity.AdsActivity.KEY_imageposi;
import static lockscreenads.activity.AdsActivity.KEY_pin_Image_id;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {


    public static final String KEY_ISFORSWIPELEFT = "SFORSWIPELEFT";
    public static final String KEY_ISFORSWIPERIGHT = "ISFORSWIPERIGHT";
    public static final String KEY_isSwipDone = "isSwipDone";
    private static final String TAG = "BlankFragment";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String KEY_IMAGE_SET = "IMAGE_SET";
    public ArrayList<String> fileList = new ArrayList<>();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int i = -1;
    ImageView imageView;
    String adsId = "0";
    RequestQueue queue;
    ArrayList<ImageModel> imageModelsNew = new ArrayList<>();
    setImageDataInterface setImageDataInterface;
    float startCoY, endCoY, startCoX, endCoX;

    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance(String param1) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    boolean isForMoving = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_blank, container, false);


        imageView = view.findViewById(R.id.img_view);

        queue = Volley.newRequestQueue(getActivity());
        preferances = SharedPreferenceUtils.getInstance(getActivity());

        setImageDataInterface = (BlankFragment.setImageDataInterface) getActivity();
        preferences = getActivity().getSharedPreferences(KEY_prefName, 0);
        editor = preferences.edit();
        editor.apply();
        i = preferences.getInt(KEY_imageposi, -1);
        int MyVersion = Build.VERSION.SDK_INT;
        imageModels.clear();
        if (getArguments().getString(ARG_PARAM1).equals("close")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().finishAndRemoveTask();
            } else {
                getActivity().finish();
            }
        } else if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                if (!preferences.getBoolean("dataStored", false)) {
                    storeData();
                } else {
                    getDataFile();

                }
            }
        } else {
            if (!preferences.getBoolean("dataStored", false)) {
                storeData();
            } else {
                getDataFile();
            }
        }
//             TextInfoListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(get, "asdfsdfds", Toast.LENGTH_SHORT).show();
                if (!isForMoving) {
                    setImageDataInterface.closeBottomSheet(false);
                }

                Log.i(TAG, "onClick: ");
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i(TAG, "onTouch:ACTION_UP ");
                }

                if (event.getAction() == 0) {
                    startCoY = event.getY();
                    startCoX = event.getX();
                } else if (event.getAction() == 1) {
                    endCoY = event.getY();
                    endCoX = event.getX();
                    isForMoving = false;
                    if ((startCoY - endCoY) > 300) {
//                        getActivity().overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_left);
                        getActivity().finish();
                    } else if ((startCoX - endCoX) > 200) {
                        editor.putBoolean(KEY_ISFORSWIPELEFT, true).apply();
                        editor.putBoolean(KEY_isSwipDone, true).apply();
                        editor.putBoolean(KEY_ISFORSWIPERIGHT, false).apply();
                        isForMoving = true;
                        setImage();
                    } else if ((endCoX - startCoX) > 200) {
                        editor.putBoolean(KEY_ISFORSWIPERIGHT, true).apply();
                        editor.putBoolean(KEY_isSwipDone, true).apply();
                        setImage();
                        isForMoving = true;
                    }


                }

                Log.i(TAG, "onTouch: Event Action" + event.getAction());
                Log.i(TAG, "onTouch: Event event.getX()" + event.getX());
                Log.i(TAG, "onTouch: Event event.getY()" + event.getY());

                return false;
            }
        });
//        storeData();

        editor.putBoolean(KEY_ISFORSWIPELEFT, false).apply();
        editor.putBoolean(KEY_ISFORSWIPERIGHT, false).apply();
        editor.putBoolean(KEY_isSwipDone, false).apply();
        return view;
    }

    private void getDataFile() {
        String path = getActivity().getCacheDir().toString();

        File cFile = new File(path);

        File[] files = cFile.listFiles();
        imageModels.clear();
        fileList.clear();
        for (File file : files) {
            Log.d("Files", "FileName:" + file.getName());
            if (file.getName().contains(".jpg")) {
                fileList.add(file.getName().toString());
                String[] onlyFileName = file.getName().split("[.]");

                String desc = preferences.getString(onlyFileName[0] + "_desc", "");
                String title = preferences.getString(onlyFileName[0] + "_title", "");
                String isLike = preferences.getString(onlyFileName[0] + "_isLike", "0");
                String likeCount = preferences.getString(onlyFileName[0] + "_likeCount", "0");
                String imageServerName = preferences.getString(onlyFileName[0] + "_servername", "0");
                imageModels.add(new ImageModel(onlyFileName[0], title, desc,
                        onlyFileName[0] + ".jpg", isLike, likeCount, false,imageServerName));
            }
        }
        setImage();

    }

    public void storeData() {
        String url = "http://139.59.15.90/bsell/index.php/Api/adds";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: " + response);

                        try {
                            JSONObject object = new JSONObject(response);
                            String success = object.getString("success");
                            String image_path = object.getString("image_path");

                            ArrayList<String> deletedIds = new ArrayList<>();

                            if (success.equals("1")) {

                                JSONArray array = object.getJSONArray("data");
                                if (!array.isNull(0)) {
                                    String id = "";
                                    String lstId = preferences.getString(KEY_lastID, "0");
                                    int lastId = 0;
                                    if (lstId != null) {
                                        lastId = Integer.parseInt(lstId);
                                    } else {
                                        lastId = 0;

                                    }
                                    if (!preferences.getBoolean(KEY_IMAGE_SET, false))
                                        imageModels.clear();

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject jsonObject = array.getJSONObject(i);

                                        id = jsonObject.getString("id");
                                        String title = jsonObject.getString("title");
                                        String description = jsonObject.getString("description");
                                        String imageName = jsonObject.getString("image");
                                        String imageServerName = jsonObject.getString("image");
                                        String isLike = jsonObject.getString("is_like");
                                        String totalcount = jsonObject.getString("totalcount");

                                        int curId = Integer.parseInt(id);
//                                        if (curId > lastId) {
                                            editor.putString(id + "_desc", description).apply();
                                            editor.putString(id + "_title", title).apply();
                                            editor.putString(id + "_isLike", isLike).apply();
                                            editor.putString(id + "_likeCount", totalcount).apply();
                                            editor.putString(id + "_servername", imageServerName).apply();
                                            editor.putBoolean(id + "_isDownloaded", false).apply();

                                            boolean isDataUpdated = false;

                                            for (int j = 0; j < imageModels.size(); j++) {

                                                if (imageModels.get(j).getId().equals(id)) {

                                                    imageModels.get(j).setTitle(title);
                                                    imageModels.get(j).setDescription(description);
                                                    imageModels.get(j).setIsLike(isLike);
                                                    imageModels.get(j).setLikeCount(totalcount);
                                                    if (!imageModels.get(j).getImageServerName().equals(imageServerName)) {
                                                        imageModels.get(j).setImageName(image_path + "/" + imageName);
                                                    }

                                                    imageModels.get(j).setImageServerName(imageServerName);
                                                    imageModels.get(j).setDeleted(false);

                                                    isDataUpdated = true;
                                                    break;
                                                } else {
//                                                    imageModels.get(j).setDeleted(true);
                                                }

                                            }

                                            if (!isDataUpdated && (curId > lastId)) {

                                                imageModels.add(new ImageModel(id, title, description,
                                                        image_path + "/" + imageName,
                                                        isLike, totalcount, false,imageServerName));
                                            }

                                            Log.i(TAG, "onResponse: fileListFull:- " + fileList.toString());
                                            Log.i(TAG, "onResponse: id+\".jpg\":- " + id + ".jpg");
                                            if (fileList.contains(id + ".jpg")) {
                                                deletedIds.add(id);
                                            }
//                                        }

                                    }
                                    i = 0;
                                    editor.putString(KEY_lastID, id).apply();
                                    Log.i(TAG, "onResponse: fileList.size():-"+fileList.size());
                                    Log.i(TAG, "onResponse: imageModels.size():-"+imageModels.size());

                                    if (fileList.size() > 0) {

                                        for (int j = imageModels.size() - 1; j >= 0; j--) {

                                            if (!deletedIds.contains(imageModels.get(j).getId())) {
                                                Log.i(TAG, "onResponse: delete this data:- " + imageModels.get(j).getId());

                                                try {
                                                    String path = preferences.getString(KEY_ImagePath, "/data/user/0/gss.com.bsell/cache") + "/" + fileList.get(j);

                                                    File file = new File(path);
                                                    file.delete();
                                                    imageModels.remove(j);
                                                } catch (Exception e) {

                                                }
                                            } else {
                                                Log.i(TAG, "onResponse: data there:-" + imageModels.get(j).getId());
                                            }

                                        }
                                    }
                                    new StoreImageFile().execute();

                                }


                            }
                            editor.putBoolean("dataStored", true).apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<String, String>();

                stringMap.put("user_id", preferances.getStringValue(CommonUtils.USERID, ""));

                return stringMap;
            }
        };

        queue.add(request);


    }
    private SharedPreferenceUtils preferances;

    public void setImage() {
        File file;
        Uri uri;
        String baseFilePath = getActivity().getCacheDir().toString();

        Log.i(TAG, "baseFilePath: " + baseFilePath);
        int pinImageI = preferences.getInt(KEY_pin_Image_id, 0);
        if (pinImageI == 0) {

            if (preferences.getBoolean(KEY_ISFORSWIPERIGHT, false)) {
                i--;
                if (i < 0) {
                    i = imageModels.size() - 1;
                }
            } else {
                i++;
            }


        } else {

            int id = 0;
            for (int j = 0; j < imageModels.size(); j++) {

                id = Integer.parseInt(imageModels.get(j).getId());

                if (pinImageI == id) {
                    i = j;
                    break;
                }

            }

        }

        if (imageModels.size() > 0) {

            if (i > imageModels.size() - 1) {
                i = 0;
                file = new File(baseFilePath + "/" + imageModels.get(i).getImageName());
            } else {
                file = new File(baseFilePath + "/" + imageModels.get(i).getImageName());
            }
            setImageDataInterface.setImageCount(imageModels.get(i).getLikeCount(), i);
            setImageDataInterface.setImageText(imageModels.get(i).getDescription(), imageModels.get(i).getTitle(), i);
            setImageDataInterface.setLikeImage(i);
            uri = Uri.parse(file.getAbsolutePath());
            imageView.setImageURI(uri);
            /* Glide.with(MainActivity.this)
                .load(url)
                .into(imageView);*/
            editor.putInt(KEY_imageposi, i).apply();
//            onRefresh();
            adsId = imageModels.get(i).getId();
            editor.putBoolean(KEY_IMAGE_SET, true).apply();
            setImageDataInterface.closeBottomSheet(true);
            if (!preferences.getBoolean(KEY_isSwipDone, false)) {
                storeData();
            }


        } else {
            Toast.makeText(getActivity(), "Wait while we are updating Ads..", Toast.LENGTH_SHORT).show();
            storeData();

        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                READ_PHONE_STATE,
        }, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    if (!preferences.getBoolean("dataStored", false)) {
                        storeData();
                    } else {
                        getDataFile();
                    }
                } else {
                    //not granted
                    requestForSpecificPermission();

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public interface setImageDataInterface {

        void setImageText(String text, String setImageText, int position);

        void setImageCount(String count, int position);

        void setLikeImage(int position);

        void closeBottomSheet(boolean isForClose);


    }

    public class StoreImageFile extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity()!=null)
            dialog = ProgressDialog.show(getActivity(), "", "Wait for while..", true, false);
        }


        @Override
        protected String doInBackground(String... strings) {

            if (preferences.getBoolean(KEY_IMAGE_SET, false)) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
            for (int j = 0; j < imageModels.size(); j++) {
                try {
                    Bitmap bmp = null;
                    URL url = new URL(imageModels.get(j).getImageName());
                    URLConnection conn = url.openConnection();
                    bmp = BitmapFactory.decodeStream(conn.getInputStream());
                    String path = preferences.getString(KEY_ImagePath, "/data/user/0/gss.com.bsell/cache") + "/";

                    File f = new File(path + imageModels.get(j).getId() + ".jpg");
                    if (f.exists()) {
                        f.delete();
                    }
                        f.createNewFile();
                        Bitmap bitmap = bmp;
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        Log.e(TAG, "imagepath: " + f);
                        imageModels.get(j).setImageName(imageModels.get(j).getId() + ".jpg");

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (preferences.getBoolean("dataStored", false)) {
                if (imageModels.size() > 0) {
                    if (!preferences.getBoolean(KEY_IMAGE_SET, false)) {
                        setImage();
                    }

                } else {
                    Toast.makeText(getActivity(), "No data Found ", Toast.LENGTH_LONG).show();
                }

            }
            if (dialog != null) {
                dialog.dismiss();
            }
        }

    }

}
