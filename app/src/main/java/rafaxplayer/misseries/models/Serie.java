package rafaxplayer.misseries.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

/**
 * Created by rafax on 10/01/2017.
 */
@IgnoreExtraProperties
public class Serie {
    public String code;
    public String name;
    public String poster;
    public String temps;
    public int novistos;

    public Serie() {
    }

    public Serie(String code,String name, String poster,String temps) {
        this.code=code;
        this.name = name;
        this.poster = poster;
        this.temps=temps;

    }
    public void setnovistos(int novistos){
        this.novistos=novistos;
    }



}
