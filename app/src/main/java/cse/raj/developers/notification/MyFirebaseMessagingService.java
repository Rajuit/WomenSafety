package csedevelopers.freaky.developers.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import csedevelopers.freaky.developers.womensafety.CentralActivity;
import csedevelopers.freaky.developers.womensafety.R;

/**
 * Created by PURUSHOTAM on 11/30/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, CentralActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle("Women Safety");
        nBuilder.setContentText(remoteMessage.getNotification().getBody());
        nBuilder.setAutoCancel(true);

//        remoteMessage.getData();

    /*    Log.d("Value", "mapString  lati " + lati + " longi " + longi);

        if (mapString.equals("map")) {

            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lati, longi);
            Intent intents = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);

        }*/

        nBuilder.setSmallIcon(R.mipmap.ic_launcher);
        nBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, nBuilder.build());
    }
}
