package lockscreenads.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

public class MyBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        if(intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")
                || intent.getAction().equalsIgnoreCase("android.intent.action.QUICKBOOT_POWERON")) {
//            if (WMDSharedPrefs.isBooleanSet(context, "TRACK_ENABLE")) {
              /*  Intent iService = new Intent(context, MyService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(iService);
                }
                else
                {
                    context.startService(iService);
                }*/
//            }


        } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            Toast.makeText(context.getApplicationContext(), "Hiiii", Toast.LENGTH_SHORT).show();
        }
    }
}
