package lockscreenads.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import lockscreenads.activity.AdsActivity;

public class CallServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                System.out.println("incomingNumber : " + phoneNumber);
                Log.e("state", String.valueOf(state));
                if (state == 2 || state == 1 || state == 0) {
                    Intent intent1 = new Intent(context, AdsActivity.class);
                    intent1.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                                    Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent1.putExtra("close", true);
                    context.startActivity(intent1);
                }

            }
        }, PhoneStateListener.LISTEN_CALL_STATE);



    }
}
