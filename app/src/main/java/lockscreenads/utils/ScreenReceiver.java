package lockscreenads.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import lockscreenads.activity.AdsActivity;

import static android.content.Intent.ACTION_USER_PRESENT;

public class ScreenReceiver extends BroadcastReceiver {

    private static final String TAG = "ScreenReceiver";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent != null) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
              /*  Intent intentnew = new Intent();
                intentnew.setClass(context, MainActivity.class);
                intentnew.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentnew.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentnew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentnew);
                */
               /*     TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    telephony.listen(new PhoneStateListener() {
                        @Override
                        public void onCallStateChanged(int state, String incomingNumber) {
                            super.onCallStateChanged(state, incomingNumber);

                            if (state == 0) {
                                Intent intent1 = new Intent(context, AdsActivity.class);
                                intent1.addFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                                                Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                context.startActivity(intent1);
                            }


                        }
                    }, PhoneStateListener.LISTEN_CALL_STATE);
*/
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    Log.i(TAG, "onReceive: ");
                  /*  Intent intent1 = new Intent(context, AdsActivity.class);
                    intent1.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                                    Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent1.putExtra("close", true);
                    context.startActivity(intent1);*/

                    break;
                case ACTION_USER_PRESENT:
                    Log.e(TAG, "onReceive: ACTION_USER_UNLOCKED ");
//                storeData(context);
                    break;
            }
        }
    }






}