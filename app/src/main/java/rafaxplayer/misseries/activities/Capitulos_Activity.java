package rafaxplayer.misseries.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.adapters.ListCapitulosAdapter;
import rafaxplayer.misseries.models.Capitulo;
import rafaxplayer.misseries.models.ItemTemp;

import static rafaxplayer.misseries.MisSeries.capitulosRef;

public class Capitulos_Activity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capitulos);
        ButterKnife.bind(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listCapitulosView.setItemAnimator(new DefaultItemAnimator());
        listCapitulosView.setLayoutManager(new LinearLayoutManager(this));

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
                adapterCapitulos = new ListCapitulosAdapter(Capitulos_Activity.this,listcaps,capitulosRef);
                listCapitulosView.setAdapter(adapterCapitulos);

                sTodos.setChecked(adapterCapitulos.isAllChecked());
                listCapitulosView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        Bundle bund =getIntent().getExtras();
        if(bund != null){
            code = bund.getString("code","0");
            name = bund.getString("name","Capitulos ?");
            poster = bund.getString("poster","");
            setTitle(name);
            Picasso.with(getApplicationContext()).load(poster).into(imgSerie);
            capitulosRef.orderByChild("seriecode").equalTo(code).addValueEventListener(capsListener);
        }


        sTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Capitulos_Activity.this, sTodos.isChecked()+"", Toast.LENGTH_SHORT).show();
                adapterCapitulos.checkAll(sTodos.isChecked());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        capitulosRef.removeEventListener(capsListener);
        super.onDestroy();
    }

}
