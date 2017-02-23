package rafaxplayer.misseries.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.adapters.ListNotificationsAdapter;
import rafaxplayer.misseries.classes.BaseActivity;
import rafaxplayer.misseries.classes.GlobalUttilities;
import rafaxplayer.misseries.models.Notification;

import static rafaxplayer.misseries.MisSeries.capitulosRef;
import static rafaxplayer.misseries.MisSeries.notificationsRef;

public class Notifications_Activity extends BaseActivity {
    @BindView(R.id.listNotifications)
    RecyclerView listNotifications;
    private ValueEventListener notifyListener;
    private ListNotificationsAdapter adapterNotifications;
    private String user_id;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_notifications;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        listNotifications.setItemAnimator(new DefaultItemAnimator());
        listNotifications.setLayoutManager(new LinearLayoutManager(this));
        user_id = GlobalUttilities.getIntallID(this);
        notifyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int lastFirstVisiblePosition = ((LinearLayoutManager) listNotifications.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                List<Notification> listNoti = new ArrayList<>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Notification noti = data.getValue(Notification.class);
                    listNoti.add(noti);
                }
                //reorder list desc
                Collections.reverse(listNoti);
                adapterNotifications = new ListNotificationsAdapter(Notifications_Activity.this, listNoti);
                listNotifications.setAdapter(adapterNotifications);


                listNotifications.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        notificationsRef.child(user_id).orderByChild("date").limitToLast(20)
                .addValueEventListener(notifyListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notifications, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.delete_all:
                deleteallNotifications();

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (notifyListener != null) {
            capitulosRef.removeEventListener(notifyListener);
        }

        super.onDestroy();
    }

    private void deleteallNotifications() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Eliminar todas las notificaciones?")
                .setMessage("¿Seguro quieres eliminar todas las notificaciones? ")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot sanp : dataSnapshot.getChildren()) {
                                        sanp.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Toast.makeText(Notifications_Activity.this, "Ok notificaciones eliminadas con exito", Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.create().show();
    }
}
