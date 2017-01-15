package rafaxplayer.misseries;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rafax on 14/01/2017.
 */

public class MisSeries extends Application {
    public static FirebaseDatabase database;
    public static DatabaseReference capitulosRef;
    public static DatabaseReference seriesRef;
    public static DatabaseReference clientsRef;
    @Override
    public void onCreate() {
        database = FirebaseDatabase.getInstance();
        capitulosRef= database.getReference("/Capitulos");
        seriesRef= database.getReference("/Series");
        clientsRef= database.getReference("/Clients");
    }
}
