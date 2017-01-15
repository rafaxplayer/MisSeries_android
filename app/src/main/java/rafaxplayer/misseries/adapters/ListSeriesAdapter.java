package rafaxplayer.misseries.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.activities.Capitulos_Activity;
import rafaxplayer.misseries.models.Serie;

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
        holder.temps.setText("Temps : "+String.valueOf(mDataSeries.get(position).temps));
        Picasso.with(con).load(mDataSeries.get(position).poster).fit().into(holder.poster);
    }

    @Override
    public int getItemCount() {

        return mDataSeries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.textName)
        TextView name;
        @BindView(R.id.textTemps)
        TextView temps;
        @BindView(R.id.textNoVistos)
        TextView novistos;
        @BindView(R.id.poster)
        ImageView poster;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(con, Capitulos_Activity.class);
            intent.putExtra("code",mDataSeries.get(ViewHolder.this.getLayoutPosition()).code);
            con.startActivity(intent);
        }
    }

}


