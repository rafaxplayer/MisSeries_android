package rafaxplayer.misseries.classes;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static rafaxplayer.misseries.MisSeries.mAuth;
import static rafaxplayer.misseries.classes.GlobalUttilities.sendRegistrationToServer;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        GlobalUttilities.editSharePrefs(getApplicationContext()).putString("token",refreshedToken).commit();
        if(mAuth.getCurrentUser()!= null) {
            sendRegistrationToServer(getApplicationContext(), refreshedToken);
        }

    }


}
