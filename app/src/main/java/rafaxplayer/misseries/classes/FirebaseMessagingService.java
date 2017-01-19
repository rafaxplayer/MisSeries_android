package rafaxplayer.misseries.classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import rafaxplayer.misseries.R;
import rafaxplayer.misseries.activities.MainActivity;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
String TAG = "GCM";
    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                int count = 0;
                JSONObject jsonObj = new JSONObject(remoteMessage.getData());
                Log.e(TAG, "Message data payload: " + jsonObj.names());
                JSONArray names = jsonObj.names();
                for (int i = 0; i < jsonObj.length(); i++) {
                    JSONObject obj = new JSONObject(jsonObj.get(names.getString(i)).toString());
                    //Log.e(TAG, "IDS " + obj.getString("seriecode"));
                    if(GlobalUttilities.getPrefs(getApplicationContext()).getBoolean(obj.getString("seriecode"),true)){
                        count++;
                        //Log.e(TAG, "Count : " + count);
                    }
                }
                if (count > 0) {

                    notiifcation("Mis Series","Tienes " + count + " Capitulos nuevos");
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
    private void notiifcation(String title,String body){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }
}
