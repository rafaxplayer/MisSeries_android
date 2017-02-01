package rafaxplayer.misseries.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.activities.Capitulos_Activity;
import rafaxplayer.misseries.adapters.ListCapitulosAdapter;
import rafaxplayer.misseries.models.Capitulo;
import rafaxplayer.misseries.models.ItemTemp;

import static rafaxplayer.misseries.MisSeries.capitulosRef;

/**
 * Created by rafax on 23/01/2017.
 */

public class Capitulos_Fragment extends Fragment {
    @BindView(R.id.listCapitulos)
    RecyclerView listCapitulosView;
    @BindView(R.id.switchtodos)
    Switch sTodos;
    @BindView(R.id.imageSerie)
    ImageView imgSerie;
    String code ;
    String name ;
    String poster;
    private ValueEventListener capsListener;
    private ListCapitulosAdapter adapterCapitulos;
    private Unbinder unbinder;

    public Capitulos_Fragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_capitulos, container, false);
        unbinder = ButterKnife.bind(this,v);
        listCapitulosView.setItemAnimator(new DefaultItemAnimator());
        listCapitulosView.setLayoutManager(new LinearLayoutManager(getActivity()));

        capsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int lastFirstVisiblePosition = ((LinearLayoutManager)listCapitulosView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                List<Object> listcaps= new ArrayList<>();
                String temp = "0";
                for(DataSnapshot data:dataSnapshot.getChildren()) {

                    Capitulo cap = data.getValue(Capitulo.class);

                    if(Integer.valueOf(cap.temp.trim()) != Integer.valueOf(temp.trim())){

                        ItemTemp itemtemp = new ItemTemp(cap.temp);
                        listcaps.add(itemtemp);
                        temp = cap.temp;
                    }
                    listcaps.add(cap);


                }
                adapterCapitulos = new ListCapitulosAdapter(getActivity(),listcaps,capitulosRef);
                listCapitulosView.setAdapter(adapterCapitulos);

                sTodos.setChecked(adapterCapitulos.isAllChecked());
                listCapitulosView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        sTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterCapitulos.checkAll(sTodos.isChecked());
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void loadCapitulos(Bundle args){
        if(args != null){
            code = args.getString("code","0");
            name = args.getString("name","Capitulos ?");
            poster = args.getString("poster","");
            setTitle(name);
            if(TextUtils.isEmpty(poster)){
                Picasso.with(getActivity()).load(R.mipmap.ic_launcher).into(imgSerie);
            }else{
                Picasso.with(getActivity()).load(poster).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).into(imgSerie);
            }

            capitulosRef.orderByChild("seriecode").equalTo(code).addValueEventListener(capsListener);
        }

    }

    private void setTitle(String title){
        if(getActivity()instanceof Capitulos_Activity){
            getActivity().setTitle(name);
        }
    }
    @Override
    public void onDestroy() {
        if(capsListener!=null) {
            capitulosRef.removeEventListener(capsListener);
        }
        unbinder.unbind();
        super.onDestroy();
    }
}
