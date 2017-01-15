package rafaxplayer.misseries.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

/**
 * Created by rafax on 10/01/2017.
 */
@IgnoreExtraProperties
public class Capitulo {

    public String name;
    public Boolean notify;
    public String seriecode;
    public String temp;
    public String url;
    public Boolean visto;

    public Capitulo() {

    }

    public Capitulo(String name, String seriecode, Boolean notify, Boolean visto, String url, String temp) {
        this.name = name;
        this.seriecode = seriecode;
        this.notify = notify;
        this.visto = visto;
        this.url = url;
        this.temp = temp;
    }

    public HashMap<String ,Object> toMap(){
        HashMap<String ,Object> map = new HashMap<>();
        map.put("name",this.name);
        map.put("notify",this.notify);
        map.put("seriecode",this.seriecode);
        map.put("temp",this.temp);
        map.put("url",this.url);
        map.put("visto",this.visto);
        return map;
    }
}
