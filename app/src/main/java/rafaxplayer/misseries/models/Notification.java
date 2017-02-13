package rafaxplayer.misseries.models;

import android.text.format.DateFormat;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by rafax on 10/02/2017.
 */
@IgnoreExtraProperties
public class Notification {
    public String name;
    public String seriecode;
    public String temp;
    public String url;
    public String date;
    public String key;

    public Notification(){
        date = DateFormat.format("dd-MM-yyyy hh:mm:ss", new Date()).toString();
    }

    public Notification(String name, String seriecode, String temp, String url) {
        this.name = name;
        this.seriecode = seriecode;
        this.temp = temp;
        this.url=url;
        date = DateFormat.format("dd-MM-yyyy hh:mm:ss", new Date()).toString();
    }
    public void setKey(String key){
        this.key=key;
    }
}
