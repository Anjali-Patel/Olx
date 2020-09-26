package gss.com.bsell.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import gss.com.bsell.MainActivity;
import gss.com.bsell.R;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    NotificationManager notificationManager;
    int n = 0;
    private SharedPreferenceUtils preferances;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        preferances = SharedPreferenceUtils.getInstance(this);

        String count = preferances.getStringValue(CommonUtils.NOTIFICATIONCOUNT, "");
        if (count == null || count.equalsIgnoreCase("") || count.equalsIgnoreCase("null") || count.equalsIgnoreCase(" ")) {
            n = n + 1;
        } else {
            n = Integer.parseInt(count) + 1;
        }
        preferances.setValue(CommonUtils.NOTIFICATIONCOUNT, String.valueOf(n));

        showNotification(remoteMessage.getData().get("sender_name"),
                remoteMessage.getData().get("message"),
                remoteMessage.getData().get("datetime"));


//                   notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            setupChannels();
//        }
//        int notificationId = new Random().nextInt(60000);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "123")
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle(remoteMessage.getData().get("title"))
//                .setContentText(remoteMessage.getData().get("body"))
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

//        if (remoteMessage.getData().containsKey("tag")) {
//            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("tag"));
//        } else {
//            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), "tag");
//        }


    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        preferances = SharedPreferenceUtils.getInstance(this);

        String refreshedToken = s;
        preferances.setValue(CommonUtils.FCMTOCKEN, s);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence adminChannelName = "test";
        String adminChannelDescription = "123";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("123", adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    public void showNotification(String Sender, String Message, String Date) {

        Intent i = null;

        i = new Intent(this, MainActivity.class);
        i.putExtra("chatFragment", "chatFragment");
//
//        if (tag.equals("message")) {
//            i = new Intent(this, MainActivity.class);
//            i.putExtra("chatFragment","chatFragment");
//        }
//        else {
//            i = new Intent(this, MainActivity.class);
//        }


        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String channelId = "channel ";
        String channelName = "ChannelName";

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(i);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "123")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(Sender)
                .setContentText(Message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

    }


}