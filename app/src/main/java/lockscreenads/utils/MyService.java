package lockscreenads.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import gss.com.bsell.R;

import static lockscreenads.ApplicationClass.isForStopService;

public class MyService extends Service {
    public static final String ACTION = "android.intent.action.SCREEN_ON";
    public static final String ACTION_OFF = "android.intent.action.SCREEN_OFF";
    private static final String TAG = "MyService";
    ScreenReceiver receiver;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        startInForeground(getApplicationContext());
        register();

    }

    private void register() {
//        if(receiver == null) {
        receiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            filter.addAction(Intent.ACTION_USER_PRESENT);
        }
        registerReceiver(receiver, filter);


//     }
    }

    private void startInForeground(Context context) {
      /*  Log.i(TAG, "startInForeground: " + Build.VERSION.SDK_INT);
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {

            Notification.Builder builder = new Notification.Builder(this);
            startForeground(-1, builder.getNotification());
        } else {
            Intent notificationIntent = new Intent(context, MyService.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "165")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.app_name))
//                    .setTicker("TICKER")
                    .setContentIntent(pendingIntent);
            Notification notification = builder.build();
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel("165", "BSell Screen Saver", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("A channel for BSell Screen Saver");
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
            startForeground(1327, notification);
        }*/


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (receiver != null)
        unregisterReceiver(receiver);

       /* if (!isForStopService) {
            register();
            Intent intent = new Intent(this, MyService.class);
            intent.setAction(MyService.ACTION);
            intent.setAction(MyService.ACTION_OFF);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }*/
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        if (receiver != null)

        unregisterReceiver(receiver);
       /* if (!isForStopService) {
            register();
            Intent intent = new Intent(getApplicationContext(), MyService.class);
            intent.setAction(MyService.ACTION);
            intent.setAction(MyService.ACTION_OFF);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(intent);
            } else {
                getApplicationContext().startService(intent);
            }
        }*/
    }

}
