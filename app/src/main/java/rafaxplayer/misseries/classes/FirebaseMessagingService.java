package rafaxplayer.misseries.classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import rafaxplayer.misseries.R;
import rafaxplayer.misseries.activities.NoVistos_Activity;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String TAG = "GCM";
    private static int numMessages = 0;
    NotificationCompat.InboxStyle inboxStyle =  new NotificationCompat.InboxStyle();
    public FirebaseMessagingService() {
    }

    @Override
    public void onDeletedMessages() {
        numMessages = 0;

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            try {

                JSONObject jsonObj = new JSONObject(remoteMessage.getData());
                Log.e(TAG, "Message capitulo name: " + jsonObj.getString("name"));
                if(GlobalUttilities.getPrefs(getApplicationContext()).getBoolean(jsonObj.getString("seriecode"),true)){
                    notiifcation("Mis Series","Tienes nuevos capitulos",jsonObj.getString("name"));

                }

            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            Log.e(TAG,remoteMessage.getNotification().toString());
        }
    }
    private void notiifcation(String title,String body,String name){

        Intent resultIntent = new Intent(this, NoVistos_Activity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(NoVistos_Activity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setSound(RingtoneManager
                                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true);
        numMessages = numMessages+1;
        mBuilder.setContentText("Tienes " + numMessages + " nuevos capitulos");
        mBuilder.setNumber(numMessages);
        mBuilder.setContentIntent(resultPendingIntent);
Log.e("count",numMessages+"");
        inboxStyle.setBigContentTitle("Mis Series Nuevos capitulos :");
        if(numMessages < 6) {

            inboxStyle.addLine(name);
        }
        inboxStyle.setSummaryText("Total " + numMessages + " nuevos capitulos");
        mBuilder.setStyle(inboxStyle);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
        BadgeUtils.setBadge(this,numMessages);
    }


}
