package lockscreenads.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import gss.com.bsell.R;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import lockscreenads.fragments.BlankFragment;
import lockscreenads.fragments.TextInfoListDialogFragment;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static lockscreenads.ApplicationClass.KEY_ImagePath;
import static lockscreenads.ApplicationClass.KEY_prefName;
import static lockscreenads.ApplicationClass.imageModels;
import static lockscreenads.activity.VideoPlayActivity.start;
import static lockscreenads.fragments.BlankFragment.KEY_ISFORSWIPELEFT;
import static lockscreenads.fragments.BlankFragment.KEY_ISFORSWIPERIGHT;
import static lockscreenads.fragments.BlankFragment.KEY_isSwipDone;

public class AdsActivity extends AppCompatActivity implements TextInfoListDialogFragment.Listener,
        BlankFragment.setImageDataInterface {
    public static final String KEY_imageposi = "imageposi";
    public static final String TAG = "AdsActivity";
    public static final String KEY_pin_Image_id = "pin_Image";

    String adsId = "0";
    LinearLayout layoutBottomSheet;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    BottomSheetBehavior sheetBehavior;
    RequestQueue queue;
    TextView imageDescri, imageTitle, tvLikeCount;
    ImageView imageLike, imgPlay, imgDownload;
    int i = 0;
    TextView mCustomToast;
    private SharedPreferenceUtils preferances;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED, WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            window.setFlags(WindowManager.LayoutPa
//            .rams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//        }
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        window.setFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER, WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
//        window.setExitTransition(new Explode());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setEnterTransition(new Explode());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
        }
        setContentView(R.layout.activity_ads);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            openOverlaySettings();
        }

        imageDescri = findViewById(R.id.prd_desc);
        imageTitle = findViewById(R.id.prd_title);
        preferances = SharedPreferenceUtils.getInstance(this);

        imageLike = findViewById(R.id.img_like);
        imageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        tvLikeCount = findViewById(R.id.tv_like_count);
        imgPlay = findViewById(R.id.img_play);
        imgDownload = findViewById(R.id.img_download);
        mCustomToast = (TextView) findViewById(R.id.tv_custom_toast);
        mCustomToast.setVisibility(View.GONE);
        imgPlay.setVisibility(View.GONE);
        sheetBehavior.setHideable(false);
//        Intent intent = new Intent(AdsActivity.this, MyService.class);
//        intent.setAction(MyService.ACTION);
//        intent.setAction(MyService.ACTION_OFF);
        preferences = getSharedPreferences(KEY_prefName, 0);
        editor = preferences.edit();
        editor.apply();
        i = preferences.getInt(KEY_imageposi, -1);
        queue = Volley.newRequestQueue(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//        } else {
//            startService(intent);
//        }

        int MyVersion = Build.VERSION.SDK_INT;

        if (getIntent().hasExtra("close")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        } else if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (!checkIfAlreadyHavePermission()) {
                requestForSpecificPermission();
            } else {
                setFragment("");
            }
        } else {
            setFragment("");
        }

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callVideoLink(imageDescri);

            }
        });
        ImageView tgBtnPin = findViewById(R.id.img_pin);

        int pinImageI = preferences.getInt(KEY_pin_Image_id, 0);

        if (pinImageI != 0) {
            tgBtnPin.setImageDrawable(getResources().getDrawable(R.drawable.ic_push_pin_selected));
        } else {
            tgBtnPin.setImageDrawable(getResources().getDrawable(R.drawable.ic_push_pin_un_selected));
        }


        editor.putString(KEY_ImagePath, getCacheDir().toString()).apply();


    }

    /**
     * show custom toast:
     * fix the problem that {@link android.widget.Toast} can't show when screen be
     * locked
     */
    private void showCustomToast() {
        if (mCustomToast != null) {
            if (mCustomToast.getVisibility() == View.VISIBLE) {
                return;
            }
            mCustomToast.setText(getString(R.string.img_store_mess));
            mCustomToast.setVisibility(View.VISIBLE);
            mCustomToast.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCustomToast.setVisibility(View.GONE);
                }
            }, 1000);
        }
    }

    public void setFragment(String text) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, BlankFragment.newInstance(text));
//        transaction.commit();
        transaction.commitAllowingStateLoss();
    }

    private boolean checkIfAlreadyHavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                READ_PHONE_STATE,
        }, 101);
    }


    @Override
    public void setImageText(String text, String imageTitleStr, int position) {
        imageDescri.setText(text);
        imageTitle.setText(imageTitleStr);
        String videoId = "";
        Scanner scanner = new Scanner(text);
        while (scanner.hasNext()) {
            String possibleUrl = scanner.next();
            Log.i(TAG, "onCreate: possibleUrl:-" + possibleUrl);
            if (possibleUrl.contains("www.youtube.com")) {
                Log.i(TAG, "onCreate: inside if possibleUrl " + possibleUrl);
                String[] data = possibleUrl.split("=");
                videoId = data[1];
            } else if (possibleUrl.contains("youtu.be")) {
                String id = possibleUrl.substring(possibleUrl.lastIndexOf("/") + 1);
                videoId = id;
            }
        }


        if (videoId.length() > 1) {
            imgPlay.setVisibility(View.VISIBLE);
            imageDescri.setVisibility(View.GONE);

        } else {
            imgPlay.setVisibility(View.GONE);
            imageDescri.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public void setImageCount(String count, int position) {
        tvLikeCount.setText(count);
    }

    @Override
    public void setLikeImage(int position) {

        if (imageModels.get(position).getIsLike().equals("0")) {
            imageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
        } else {
            imageLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        }

        if (preferences.getBoolean(imageModels.get(position).getId() + "_isDownloaded", false)) {
            imgDownload.setImageDrawable(getResources().getDrawable(R.drawable.ic_downloaded));
        } else {
            imgDownload.setImageDrawable(getResources().getDrawable(R.drawable.ic_get_app));
        }


    }

    @Override
    public void closeBottomSheet(boolean isForClose) {
        if (isForClose) {

            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    setFragment("");

                } else {
                    //not granted
                    requestForSpecificPermission();

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void openOverlaySettings() {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        try {
            startActivityForResult(intent, 11);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            addAutoStartup();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addAutoStartup() {

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(AdsActivity.this, MyService.class);
//        intent.setAction(MyService.ACTION);

//        unregisterReceiver(receiver);
//        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
//        finish();

        editor.putBoolean(KEY_ISFORSWIPELEFT, false).apply();
        editor.putBoolean(KEY_ISFORSWIPERIGHT, false).apply();
        editor.putBoolean(KEY_isSwipDone, false).apply();
    }


    public void setOnClose(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdsActivity.this)
                .setMessage("Do you sure want to delete?")
                .setTitle("")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (imageModels.size() > 0) {
                            i = preferences.getInt(KEY_imageposi, 0);

                            Log.i(TAG, "setOnClose: Image:- " + imageModels.get(i).getImageName());
                            if (deleteFile(imageModels.get(i).getImageName())) {
                                imageModels.remove(i);
                                setFragment("");
                            }
                        } else {
                            Toast.makeText(AdsActivity.this, "Reached end..", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    public void onTextInfoClicked(int position) {

    }

    public boolean deleteFile(String fileName) {
        String baseFilePath = getCacheDir().toString();
        File file;
        i = preferences.getInt(KEY_imageposi, 0);

        file = new File(baseFilePath + "/" + imageModels.get(i).getImageName());
        return file.delete();
    }

    public void onLikeAdd(final View view) {
        i = preferences.getInt(KEY_imageposi, 0);
        adsId = imageModels.get(i).getId();

        if (imageModels.get(i).getIsLike().equals("0")) {
            String url = "http://139.59.15.90/bsell/index.php/Api/adv_like";
            int count = Integer.parseInt(tvLikeCount.getText().toString());
            tvLikeCount.setText(String.valueOf(count + 1));
            editor.putString(adsId + "_likeCount", String.valueOf(count + 1)).apply();
            ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i(TAG, "onResponse: " + response);
                            imageModels.get(i).setIsLike("1");
                            editor.putString(adsId + "_isLike", "1").apply();


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "onErrorResponse: ", error);
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> stringMap = new HashMap<String, String>();

                    stringMap.put("user_id", preferances.getStringValue(CommonUtils.USERID, ""));
                    stringMap.put("adv_id", adsId);

                    return stringMap;
                }
            };
            queue.add(request);
        } else {
            String url = "http://139.59.15.90/bsell/index.php/Api/adv_unlike";
            int count = Integer.parseInt(tvLikeCount.getText().toString());
            tvLikeCount.setText(String.valueOf(count - 1));
            editor.putString(adsId + "_likeCount", String.valueOf(count - 1)).apply();

            ((ImageView) view).setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_favorite_border));

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i(TAG, "onResponse: " + response);
                            imageModels.get(i).setIsLike("0");
                            editor.putString(adsId + "_isLike", "0").apply();


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "onErrorResponse: ", error);
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> stringMap = new HashMap<String, String>();

                    stringMap.put("user_id", preferances.getStringValue(CommonUtils.USERID, ""));
                    stringMap.put("adv_id", adsId);

                    return stringMap;
                }
            };
            queue.add(request);
        }


    }


    public void setOnDownload(View view) {
        String baseFilePath = getCacheDir().toString();
        String externalPath = getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString();

        i = 0;
        i = preferences.getInt(KEY_imageposi, 0);


        File sourceLocation = new File(baseFilePath + "/" + imageModels.get(i).getImageName());
        File targetLocation = new File(externalPath + "/" + imageModels.get(i).getImageName());
        try {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = null;

            out = new FileOutputStream(targetLocation);


            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            MediaScannerConnection.scanFile(this, new String[]{targetLocation.getPath()}, new String[]{"image/jpeg"}, null);
            showCustomToast();
            editor.putBoolean(imageModels.get(i).getId() + "_isDownloaded", true).apply();
            imgDownload.setImageDrawable(getResources().getDrawable(R.drawable.ic_downloaded));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void setOnPin(View view) {

//        ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.ic_push_pin_selected));
        i = preferences.getInt(KEY_imageposi, 0);
        int pinInt = preferences.getInt(KEY_pin_Image_id, 0);

        if (pinInt == 0) {
            editor.putInt(KEY_pin_Image_id, Integer.parseInt(imageModels.get(i).getId())).apply();
            ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.ic_push_pin));
        } else {
            editor.putInt(KEY_pin_Image_id, 0).apply();
            ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.ic_push_pin_un_selected));
        }


    }

    public void callShowBottom(View view) {

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    public void callVideoLink(View view) {

        String videoId = "";


        TextView textView = (TextView) view;

        Scanner scanner = new Scanner(textView.getText().toString());
        while (scanner.hasNext()) {
            String possibleUrl = scanner.next();
            Log.i(TAG, "onCreate: possibleUrl:-" + possibleUrl);
            if (possibleUrl.contains("www.youtube.com")) {
                Log.i(TAG, "onCreate: inside if possibleUrl " + possibleUrl);
                String[] data = possibleUrl.split("=");
                videoId = data[1];
            } else if (possibleUrl.contains("youtu.be")) {
                String id = possibleUrl.substring(possibleUrl.lastIndexOf("/") + 1);
                videoId = id;
            }
        }


        if (videoId.length() > 1) {
            start(AdsActivity.this, videoId);
        }
    }
}
