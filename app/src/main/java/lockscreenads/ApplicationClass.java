package lockscreenads;

import android.app.Application;

import java.util.ArrayList;

import lockscreenads.models.ImageModel;

public class ApplicationClass extends Application {

    public static ArrayList<ImageModel> imageModels;
    public static final String KEY_prefName = "pref_lock";
    public static final String KEY_lastID = "lastID";
    public static final String KEY_ImagePath = "ImagePath";
    public static boolean isForStopService = false;
    public static final String KEY_isServiceOn = "isServiceOn";

    @Override
    public void onCreate() {
        super.onCreate();
        imageModels = new ArrayList<>();

    }
}
