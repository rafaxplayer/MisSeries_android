package rafaxplayer.misseries.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.MisSeries;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.classes.GlobalUttilities;
import rafaxplayer.misseries.models.Notification;

import static rafaxplayer.misseries.MisSeries.notificationsRef;

/**
 * Created by rafax on 10/02/2017.
 */

public class ListNotificationsAdapter extends RecyclerView.Adapter<ListNotificationsAdapter.ViewHolder>{
    private Context con;
    private List<Notification> mDataNotifications;
    private String user_id;


    public ListNotificationsAdapter(Context con, List<Notification> notifications) {
        this.con = con;
        this.mDataNotifications = notifications;
        this.user_id = GlobalUttilities.getIntallID(con);

    }


    @Override
    public ListNotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        ListNotificationsAdapter.ViewHolder vh = new ListNotificationsAdapter.ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ListNotificationsAdapter.ViewHolder holder, final int position) {
        holder.name.setText(mDataNotifications.get(position).name);
        holder.temp.setText("Temp : "+ mDataNotifications.get(position).temp);
        holder.date.setText(mDataNotifications.get(position).getFormatedDate());
        holder.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotify(mDataNotifications.get(position).key);

            }
        });

    }

    @Override
    public int getItemCount() {

        return mDataNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        @BindView(R.id.textName)
        TextView name;
        @BindView(R.id.textTemp)
        TextView temp;
        @BindView(R.id.textDate)
        TextView date;
        @BindView(R.id.clearButton)
        ImageButton clearButton;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(con);

            LayoutInflater inflater = LayoutInflater.from(con);
            View dialogView = inflater.inflate(R.layout.dialog_notification, null);
            dialogBuilder.setView(dialogView);
            final ImageView img = (ImageView)dialogView.findViewById(R.id.imageSerieNotify);
            MisSeries.seriesRef.orderByChild("code")
                    .equalTo(mDataNotifications.get(ViewHolder.this.getLayoutPosition()).seriecode)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        for(DataSnapshot data:dataSnapshot.getChildren()){
                            String poster = data.child("poster").getValue(String.class);
                            if(!TextUtils.isEmpty(poster))
                                Picasso.with(con).load(poster).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).into(img);
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            ((TextView) dialogView.findViewById(R.id.textTitleNotifiy)).setText(mDataNotifications.get(ViewHolder.this.getLayoutPosition()).name);
            ((TextView) dialogView.findViewById(R.id.textTempNotify)).setText("Temp : "+mDataNotifications.get(ViewHolder.this.getLayoutPosition()).temp);
            ((TextView) dialogView.findViewById(R.id.textDateNotify)).setText(mDataNotifications.get(ViewHolder.this.getLayoutPosition()).getFormatedDate());

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }
    private void deleteNotify(final String code){
        AlertDialog.Builder dialog = new AlertDialog.Builder(con);
        dialog.setTitle("Elimnar Serie?")
                .setMessage("¿Seguro quieres eliminar la notificacion? ")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {

                            notificationsRef.child(user_id).child(code).removeValue();
                            Toast.makeText(con, "Ok Serie eliminada con exito", Toast.LENGTH_SHORT).show();
                        }catch (Exception ex){
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
