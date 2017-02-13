package rafaxplayer.misseries.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.classes.GlobalUttilities;
import rafaxplayer.misseries.models.Notification;

import static rafaxplayer.misseries.MisSeries.notificationsRef;

/**
 * Created by rafax on 10/02/2017.
 */

public class ListNotificationsAdapter extends RecyclerView.Adapter<ListNotificationsAdapter.ViewHolder>{
    private Context con;
    private List<Notification> mDataSeries;
    private String user_id;


    public ListNotificationsAdapter(Context con, List<Notification> notifications) {
        this.con = con;
        this.mDataSeries = notifications;
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
        holder.name.setText(mDataSeries.get(position).name);
        holder.temp.setText("Temp : "+ mDataSeries.get(position).temp);
        holder.date.setText(mDataSeries.get(position).date);
        holder.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotify(mDataSeries.get(position).key);

            }
        });

    }

    @Override
    public int getItemCount() {

        return mDataSeries.size();
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
