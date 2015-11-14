package in.viato.app.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.orhanobut.logger.Logger;


import in.viato.app.R;
import in.viato.app.ui.activities.HomeActivity;

/**
 * Created by saiteja on 02/11/15.
 */
public class GcmListener extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

//        Logger.d(data.toString());

        String message = data.getString("message");
//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "Message: " + message);

//        sendNotification(message);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent();
        intent.setClassName("in.viato.app", "in.viato.app.ui.activities.HomeActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(bm)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


        //                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Intent intent = new Intent();
//                    intent.setClassName("in.viato.app", "in.viato.app.ui.activities.HomeActivity");
//                    intent.putExtra(HomeActivity.EXTRA_SELECT_TAB, HomeActivity.TAB_TRENDING);
//                    PendingIntent resultPendingIntent = PendingIntent.getActivity(mActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    NotificationCompat.Builder mBuilder =
//                            new NotificationCompat.Builder(getApplicationContext())
//                                    .setSmallIcon(R.drawable.ic_launcher)
//                                    .setContentTitle("My notification")
//                                    .setContentText("Hello World!")
//                                    .setAutoCancel(true)
//                                    .setSound(soundUri)
//                                    .setContentIntent(resultPendingIntent);
//                    int mNotificationId = 001;
//                    NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
