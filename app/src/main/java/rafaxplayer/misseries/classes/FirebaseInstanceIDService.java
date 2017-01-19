package rafaxplayer.misseries.classes;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.jaredrummler.android.device.DeviceName;

import java.util.HashMap;

import static rafaxplayer.misseries.MisSeries.clientsRef;
import static rafaxplayer.misseries.MisSeries.database;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static String TAG = "FirebaseIDService";
    private static String not_set ="NOT_SET";
    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token){
        database = FirebaseDatabase.getInstance();
        String device= DeviceName.getDeviceName();
        HashMap<String,Object> map = new HashMap<>();
        map.put("token",token);
        map.put("devicename",device);
        if(GlobalUttilities.getPrefs(getApplicationContext()).getString("install_id",not_set) == not_set){
            String key = database.getReference("/Clients").push().getKey();
            GlobalUttilities.editSharePrefs(getApplicationContext()).putString("install_id",key).commit();
        }
        String install_id = GlobalUttilities.getPrefs(getApplicationContext()).getString("install_id",not_set);
        clientsRef.child(install_id).setValue(map);


    }
}
