package rafaxplayer.misseries.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.activities.Capitulos_Activity;
import rafaxplayer.misseries.activities.Settings_Activity;
import rafaxplayer.misseries.classes.IconizedMenu;
import rafaxplayer.misseries.models.Serie;

import static rafaxplayer.misseries.MisSeries.capitulosRef;
import static rafaxplayer.misseries.MisSeries.seriesRef;

/**
 * Created by rafax on 10/01/2017.
 */

public class ListSeriesAdapter extends RecyclerView.Adapter<ListSeriesAdapter.ViewHolder>{

    private Context con;
    private List<Serie> mDataSeries;

    public ListSeriesAdapter(Context con, List<Serie> series) {
        this.con = con;
        this.mDataSeries = series;
    }


    @Override
    public ListSeriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_serie, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(mDataSeries.get(position).name);
        holder.temps.setText(con.getString(R.string.temps_item) + " " + String.valueOf(mDataSeries.get(position).temps));
        holder.novistos.setText(con.getString(R.string.novistos_item) + " " + String.valueOf(mDataSeries.get(position).novistos));
        if (mDataSeries.get(position).poster == null || mDataSeries.get(position).poster.isEmpty()) {
            Picasso.with(con).load(R.mipmap.ic_launcher)
                    .into(holder.poster);
        }else{
            Picasso.with(con).load(mDataSeries.get(position).poster)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher).fit().into(holder.poster);
        }
            holder.opButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IconizedMenu PopupMenu = new IconizedMenu(con,v);
                Menu menu = PopupMenu.getMenu();
                MenuInflater inflater = PopupMenu.getMenuInflater();
                inflater.inflate(R.menu.options_serie_menu, PopupMenu.getMenu());
                PopupMenu.show();

                PopupMenu.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_deleteserie:
                                deleteSerie(mDataSeries.get(position).code,mDataSeries.get(position).name);
                                break;
                            case R.id.action_settingsserie:
                                Intent intent = new Intent(con, Settings_Activity.class);
                                intent.putExtra("name",mDataSeries.get(position).name);
                                intent.putExtra("code",mDataSeries.get(position).code);
                                con.startActivity(intent);
                                break;
                            case R.id.action_viewserie:
                                Intent intencion = new Intent(con, Capitulos_Activity.class);
                                intencion.putExtra("name",mDataSeries.get(position).name);
                                intencion.putExtra("code",mDataSeries.get(position).code);
                                intencion.putExtra("poster",mDataSeries.get(position).poster);
                                con.startActivity(intencion);
                              break;
                        }
                        return true;
                    }
                });

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
        @BindView(R.id.textTemps)
        TextView temps;
        @BindView(R.id.textNoVistos)
        TextView novistos;
        @BindView(R.id.poster)
        ImageView poster;
        @BindView(R.id.optionsButton)
        ImageButton opButton;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(con, Capitulos_Activity.class);
            intent.putExtra("code",mDataSeries.get(ViewHolder.this.getLayoutPosition()).code);
            intent.putExtra("name",mDataSeries.get(ViewHolder.this.getLayoutPosition()).name);
            intent.putExtra("poster",mDataSeries.get(ViewHolder.this.getLayoutPosition()).poster);
            con.startActivity(intent);
        }
    }
    public void deleteSerie(final String code,String name){
        AlertDialog.Builder dialog = new AlertDialog.Builder(con);
        dialog.setTitle("Elimnar Serie?")
                .setMessage("¿Seguro quieres eliminar la serie "+ name)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        seriesRef.child(code).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    capitulosRef.orderByChild("seriecode").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot data:dataSnapshot.getChildren()){
                                                data.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        });
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


