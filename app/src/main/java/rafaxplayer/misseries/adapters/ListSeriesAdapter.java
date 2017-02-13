package rafaxplayer.misseries.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.activities.Settings_Activity;
import rafaxplayer.misseries.classes.IconizedMenu;
import rafaxplayer.misseries.fragments.Series_Fragment;
import rafaxplayer.misseries.models.Serie;

import static rafaxplayer.misseries.MisSeries.seriesRef;

/**
 * Created by rafax on 10/01/2017.
 */

public class ListSeriesAdapter extends RecyclerView.Adapter<ListSeriesAdapter.ViewHolder>{

    private Context con;
    private List<Serie> mDataSeries;
    private Series_Fragment.OnSerieSelectedListener callback;

    public ListSeriesAdapter(Context con, List<Serie> series,Series_Fragment.OnSerieSelectedListener callback) {
        this.con = con;
        this.mDataSeries = series;
        this.callback=callback;
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
                                Bundle bun= new Bundle();
                                bun.putString("code",mDataSeries.get(position).code);
                                bun.putString("name",mDataSeries.get(position).name);
                                bun.putString("poster",mDataSeries.get(position).poster);
                                callback.onSerieSelected(bun);

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
           Bundle bun= new Bundle();
            bun.putString("code",mDataSeries.get(ViewHolder.this.getLayoutPosition()).code);
            bun.putString("name",mDataSeries.get(ViewHolder.this.getLayoutPosition()).name);
            bun.putString("poster",mDataSeries.get(ViewHolder.this.getLayoutPosition()).poster);
            callback.onSerieSelected(bun);

        }
    }
    public void deleteSerie(final String code,String name){
        AlertDialog.Builder dialog = new AlertDialog.Builder(con);
        dialog.setTitle("Elimnar Serie?")
                .setMessage("¿Seguro quieres eliminar la serie "+ name)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            seriesRef.child(code).removeValue();
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


