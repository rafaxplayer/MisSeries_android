package rafaxplayer.misseries.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import rafaxplayer.misseries.models.Serie;

/**
 * Created by rafax on 14/01/2017.
 */

public class GlobalUttilities {

    public static SharedPreferences.Editor editSharePrefs(Context con) {

        SharedPreferences.Editor editor = getPrefs(con).edit();

        return editor;
    }

    public static SharedPreferences getPrefs(Context con) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(con);

        return settings;
    }

    public static void setImageSerie(Context con,Serie serie){
        try {

            updateDataSerieAsync setimg= new updateDataSerieAsync(con,serie);
            setimg.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
