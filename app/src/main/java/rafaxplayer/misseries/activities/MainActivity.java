package rafaxplayer.misseries.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.adapters.ListSeriesAdapter;
import rafaxplayer.misseries.models.Serie;

import static rafaxplayer.misseries.MisSeries.capitulosRef;
import static rafaxplayer.misseries.MisSeries.seriesRef;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.listseries)
    RecyclerView listSeriesView;
    private String TAG=".MainActivity";

    private ListSeriesAdapter adapterSeries;
    private ValueEventListener seriesListener;
    private Menu menu;
    private long countNovistos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseInstanceId.getInstance().getToken();

        countNovistos=0;
        listSeriesView.setItemAnimator(new DefaultItemAnimator());
        listSeriesView.setLayoutManager(new LinearLayoutManager(this));

        seriesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Serie> listSeries = new ArrayList<Serie>();
                for(DataSnapshot data:dataSnapshot.getChildren()){

                    Serie serie = data.getValue(Serie.class);
                    listSeries.add(serie);
                    // Log.d(TAG, "Value is: " + data.child("name").getValue());
                }
                adapterSeries= new ListSeriesAdapter(MainActivity.this,listSeries);
                listSeriesView.setAdapter(adapterSeries);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };
        seriesRef.addValueEventListener(seriesListener);


    }
    @Override
    protected void onResume() {
        super.onResume();
        capitulosRef.orderByChild("visto").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                countNovistos = dataSnapshot.getChildrenCount();
                MainActivity.this.menu.findItem(R.id.news).setTitle("NO VISTOS "+countNovistos);

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
        this.menu=menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.news:
                startActivity(new Intent(this,NoVistos_Activity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        seriesRef.removeEventListener(seriesListener);
        super.onDestroy();
    }
}
