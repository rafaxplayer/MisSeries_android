package rafaxplayer.misseries.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.jaredrummler.android.device.DeviceName;

import java.util.HashMap;

import rafaxplayer.misseries.R;

import static rafaxplayer.misseries.MisSeries.clientsRef;
import static rafaxplayer.misseries.MisSeries.database;

/**
 * Created by rafax on 14/01/2017.
 */

public class GlobalUttilities {

    public static String BASE_URL="http://seriesdanko.com/";
    public static String not_set ="NOT_SET";
    public static SharedPreferences.Editor editSharePrefs(Context con) {

        SharedPreferences.Editor editor = getPrefs(con).edit();

        return editor;
    }

    public static SharedPreferences getPrefs(Context con) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(con);

        return settings;
    }
    public static String getIntallID(Context con){
        return getPrefs(con).getString("install_id",not_set);
    }

    public static Boolean isDualPanel(AppCompatActivity con){
        return con.getSupportFragmentManager().findFragmentById(R.id.fragmentCapitulos) != null && con.getSupportFragmentManager().findFragmentById(R.id.fragmentCapitulos).isVisible();
    }

    public static void sendRegistrationToServer(Context con , String token){

        String device = DeviceName.getDeviceName();
        HashMap<String,Object> map = new HashMap<>();
        map.put("token",token);
        map.put("devicename",device);
        if(GlobalUttilities.getPrefs(con).getString("install_id",not_set) == not_set){
            String key = database.getReference("/Clients").push().getKey();
            GlobalUttilities.editSharePrefs(con).putString("install_id",key).commit();
        }
        String install_id = GlobalUttilities.getPrefs(con).getString("install_id",not_set);
        clientsRef.child(install_id).setValue(map);


    }
}
