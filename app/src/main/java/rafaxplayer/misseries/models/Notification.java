package rafaxplayer.misseries.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
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
    public long date;
    public String key;

    public Notification(){
        date = getTimeStamp();
    }

    public Notification(String name, String seriecode, String temp, String url) {
        this.name = name;
        this.seriecode = seriecode;
        this.temp = temp;
        this.url=url;
        this.date = getTimeStamp();


    }
    public void setKey(String key){
        this.key=key;
    }

    private long getTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;

        return tsLong;

    }

    public String getFormatedDate(){

        try{

            Date netDate = (new Date(this.date*1000));
            String format="dd-MM-yyyy hh:mm:ss";
            java.text.DateFormat sdf = new SimpleDateFormat(format);

            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }
}
