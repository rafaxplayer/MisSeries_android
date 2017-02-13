package rafaxplayer.misseries.classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import rafaxplayer.misseries.R;
import rafaxplayer.misseries.activities.NoVistos_Activity;
import rafaxplayer.misseries.models.Notification;

import static rafaxplayer.misseries.MisSeries.mAuth;
import static rafaxplayer.misseries.MisSeries.notificationsRef;
import static rafaxplayer.misseries.classes.GlobalUttilities.getPrefs;
import static rafaxplayer.misseries.classes.GlobalUttilities.not_set;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String TAG = "GCM";
    private static int numMessages = 0;
    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

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

                if(mAuth.getCurrentUser()!= null) {

                    if (getPrefs(getApplicationContext()).getBoolean(jsonObj.getString("seriecode"), true)) {

                        final String user_id = GlobalUttilities.getPrefs(getApplicationContext()).getString("install_id",not_set);
                        final Notification noti = new Notification(jsonObj.getString("name"),jsonObj.getString("seriecode"),jsonObj.getString("temp"),jsonObj.getString("url"));
                        final String key = notificationsRef.child(user_id).push().getKey();
                        noti.setKey(key);
                        //check if notification chapter already exists
                        notificationsRef.child(user_id).orderByChild("name").equalTo(noti.name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists()){
                                    notificationsRef.child(user_id).child(key).setValue(noti);
                                    notiifcation("Mis Series", "Tienes nuevos capitulos", noti.name);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                }

            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            Log.e(TAG, remoteMessage.getNotification().toString());
        }
    }

    private void notiifcation(String title, String body, String name) {

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
        numMessages = numMessages + 1;
        mBuilder.setContentText("Tienes " + numMessages + " nuevos capitulos");
        mBuilder.setNumber(numMessages);
        mBuilder.setContentIntent(resultPendingIntent);
        Log.e("count", numMessages + "");
        inboxStyle.setBigContentTitle("Mis Series Nuevos capitulos :");
        if (numMessages < 6) {

            inboxStyle.addLine(name);
        }
        inboxStyle.setSummaryText("Total " + numMessages + " nuevos capitulos");
        mBuilder.setStyle(inboxStyle);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
        BadgeUtils.setBadge(this, numMessages);
    }


}
