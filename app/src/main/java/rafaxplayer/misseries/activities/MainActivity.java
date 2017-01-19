package rafaxplayer.misseries.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.adapters.ListSeriesAdapter;
import rafaxplayer.misseries.classes.GlobalUttilities;
import rafaxplayer.misseries.models.Capitulo;
import rafaxplayer.misseries.models.Serie;

import static rafaxplayer.misseries.MisSeries.capitulosRef;
import static rafaxplayer.misseries.MisSeries.seriesRef;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.listseries)
    RecyclerView listSeriesView;
    @BindView(R.id.fab)
    FloatingActionButton fabNewSerie;
    private String TAG = ".MainActivity";

    private ListSeriesAdapter adapterSeries;
    private ValueEventListener seriesListener;
    private ValueEventListener capitulosNoVistosListener;
    private Menu _menu;
    private long countNovistos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        countNovistos = 0;
        listSeriesView.setItemAnimator(new DefaultItemAnimator());
        listSeriesView.setLayoutManager(new LinearLayoutManager(this));

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

                                    serie.novistos = count;
                                    adapterSeries.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    listSeries.add(serie);

                }
                adapterSeries = new ListSeriesAdapter(MainActivity.this, listSeries);
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
                showDialogNewRecipe();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        capitulosRef.orderByChild("visto").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                countNovistos = dataSnapshot.getChildrenCount();
                if(_menu !=null)
                    _menu.findItem(R.id.news).setTitle("NO VISTOS " + countNovistos);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.news).setTitle("NO VISTOS " + countNovistos);
        this._menu = menu;
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.news:
                if(countNovistos>0) {
                    startActivity(new Intent(this, NoVistos_Activity.class));
                }else{
                    Toast.makeText(this, "Has visualizado todos los capitulos", Toast.LENGTH_SHORT).show();
                }
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        seriesRef.removeEventListener(seriesListener);
        super.onDestroy();
    }

    private void showDialogNewRecipe(){
        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_recipe, null);
        dialog.setView(dialogView)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String code=((EditText)dialogView.findViewById(R.id.editCodeSerie)).getText().toString();

                if(TextUtils.isEmpty(code)){
                    Toast.makeText(MainActivity.this, "El codigo de la serie es necesario", Toast.LENGTH_SHORT).show();
                    return;
                }

                newSerie(code);
            }
        }).create().show();
    }

    private void newSerie(final String code){

        final Serie serie = new Serie(code,"","","");
        seriesRef.child(code).setValue(serie).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    GlobalUttilities.setImageSerie(MainActivity.this,serie);
                }

            }

        });

    }
}
