package rafaxplayer.misseries.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.activities.MainActivity;
import rafaxplayer.misseries.adapters.ListSeriesAdapter;
import rafaxplayer.misseries.models.Capitulo;
import rafaxplayer.misseries.models.Serie;

import static rafaxplayer.misseries.MisSeries.capitulosRef;
import static rafaxplayer.misseries.MisSeries.seriesRef;

/**
 * Created by rafax on 23/01/2017.
 */

public class Series_Fragment extends Fragment {
    @BindView(R.id.listseries)
    RecyclerView listSeriesView;
    @BindView(R.id.fab)
    FloatingActionButton fabNewSerie;

    private String TAG = ".MainActivity";
    private ListSeriesAdapter adapterSeries;
    private ValueEventListener seriesListener;
    private ValueEventListener capitulosNoVistosListener;
    private Unbinder unbinder;
    private Context context;
    private OnSerieSelectedListener callback;


    public interface OnSerieSelectedListener{
        void onSerieSelected(Bundle bund);
    }

    public Series_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        try{
            callback=(OnSerieSelectedListener)context;
        }catch(Exception ex){
            throw new ClassCastException(context.toString());
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_series, container, false);
        unbinder = ButterKnife.bind(this,v);

        listSeriesView.setItemAnimator(new DefaultItemAnimator());
        listSeriesView.setLayoutManager(new LinearLayoutManager(context));

        seriesListener = seriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Serie> listSeries = new ArrayList<Serie>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    final Serie serie = data.getValue(Serie.class);

                    // set capitulos no visto en la serie
                    capitulosNoVistosListener = capitulosRef.orderByChild("seriecode").equalTo(serie.code)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int count = 0;
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        if (!data.getValue(Capitulo.class).visto) {
                                            count++;
                                        }
                                    }
                                    serie.setnovistos(count);
                                    adapterSeries.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    listSeries.add(serie);

                }
                adapterSeries = new ListSeriesAdapter(context, listSeries,callback);
                listSeriesView.setAdapter(adapterSeries);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        fabNewSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showDialogNewSerie();


            }
        });


        return v;
    }
    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if(seriesListener!=null) {
            seriesRef.removeEventListener(seriesListener);
        }
        if(capitulosNoVistosListener!=null){
            capitulosRef.removeEventListener(capitulosNoVistosListener);
        }
    }





}
