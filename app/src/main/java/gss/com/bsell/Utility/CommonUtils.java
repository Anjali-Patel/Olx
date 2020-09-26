package gss.com.bsell.Utility;

import java.util.ArrayList;

import gss.com.bsell.Model.SelectedCategoryDataModel;
import gss.com.bsell.Model.SelectedImageModel;

public class CommonUtils {
    public static String APP_URL= "http://bsell.co.in/index.php/Api/";
//public static String APP_URL="http://134.209.166.137/index.php/Api/";
//    public static String APP_URL = "http://139.59.15.90/bsell/index.php/Api/";
    public static String LOGIN_URL = APP_URL + "user_login";
    public static String ISREGISTERED = "IsRegister";
    public static String EMAIL_ID = "email_id";
    public static String CurrentLocation = "location";
    public static String IMAGE_URL ="http://139.59.15.90/bsell/assets/upload/photos/";
    public static String PROFILE_IMG_URL ="http://139.59.15.90/bsell/assets/upload/users/";
    //public static String CATEGORY_ICON_URL ="http://139.59.15.90/bsell/assets/admin/categories/";
    public static String CATEGORY_ICON_URL ="http://139.59.15.90/bsell/assets/upload/categoryIcon/";


    public static String USERID="userid";
    public static String USERMOBILE="mobile";

    public static String LATTITUTE="lat";
    public static String LONGITUDE="lon";
    public static String MAINCATEGORY="maincat";
    public static String MAINCATEGORYNAME="maincatname";

    public static String FCMTOCKEN="tocken";
    public static String NOTIFICATIONCOUNT="count";

    public static String PRODUCTNAME="product";
    public static String SIMILARPRODUCTID="similarid";




    public static ArrayList<SelectedCategoryDataModel> SELECTEDCATEGORY = new ArrayList<SelectedCategoryDataModel>();
    public static ArrayList<SelectedImageModel> SELECTEDIMAGES = new ArrayList<SelectedImageModel>();
    public static ArrayList<SelectedImageModel> SELECTEDIMAGESINBASE64 = new ArrayList<SelectedImageModel>();



    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty() && !str.equalsIgnoreCase("null") )
            return false;
        return true;
    }

}
