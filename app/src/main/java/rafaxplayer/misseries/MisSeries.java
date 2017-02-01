package rafaxplayer.misseries;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import rafaxplayer.misseries.classes.BadgeUtils;

/**
 * Created by rafax on 14/01/2017.
 */

public class MisSeries extends Application {
    public static FirebaseDatabase database;
    public static DatabaseReference capitulosRef;
    public static DatabaseReference seriesRef;
    public static DatabaseReference clientsRef;
    public static FirebaseAuth mAuth;
    @Override
    public void onCreate() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        capitulosRef = database.getReference("/Capitulos");
        seriesRef = database.getReference("/Series");
        clientsRef = database.getReference("/Clients");
        FirebaseInstanceId.getInstance().getToken();
        BadgeUtils.clearBadge(this);
    }
}
