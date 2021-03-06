package rafaxplayer.misseries.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.adapters.ListCapitulosAdapter;
import rafaxplayer.misseries.classes.BaseActivity;
import rafaxplayer.misseries.models.Capitulo;
import static rafaxplayer.misseries.MisSeries.capitulosRef;

public class NoVistos_Activity extends BaseActivity {

    @BindView(R.id.listNoVistos)
    RecyclerView listNoVistosView;
    @BindView(R.id.switchtodos)
    Switch sTodos;
    private ValueEventListener capsListener;
    private ListCapitulosAdapter adapterCapitulos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        listNoVistosView.setItemAnimator(new DefaultItemAnimator());
        listNoVistosView.setLayoutManager(new LinearLayoutManager(this));
        capsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int lastFirstVisiblePosition = ((LinearLayoutManager)listNoVistosView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                List<Object> listcaps= new ArrayList<>();

                for(DataSnapshot data:dataSnapshot.getChildren()) {

                    Capitulo cap = data.getValue(Capitulo.class);
                    listcaps.add(cap);
                }
                adapterCapitulos = new ListCapitulosAdapter(NoVistos_Activity.this,listcaps,capitulosRef);
                listNoVistosView.setAdapter(adapterCapitulos);

                sTodos.setChecked(adapterCapitulos.isAllChecked());
                listNoVistosView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        capitulosRef.orderByChild("visto").equalTo(false)
                .addValueEventListener(capsListener);
        sTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapterCapitulos.checkAll(sTodos.isChecked());
            }
        });

    }
    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_no_vistos;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== android.R.id.home){
            Intent intent=new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        capitulosRef.removeEventListener(capsListener);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent intent=new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
