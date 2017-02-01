package rafaxplayer.misseries.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import rafaxplayer.misseries.R;

/**
 * Created by rafax on 14/01/2017.
 */

public class GlobalUttilities {

    public static String BASE_URL="http://seriesdanko.com/";

    public static SharedPreferences.Editor editSharePrefs(Context con) {

        SharedPreferences.Editor editor = getPrefs(con).edit();

        return editor;
    }

    public static SharedPreferences getPrefs(Context con) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(con);

        return settings;
    }


    public static Boolean isDualPanel(AppCompatActivity con){
        return (con.getSupportFragmentManager().findFragmentById(R.id.fragmentCapitulos) != null);
    }
}
