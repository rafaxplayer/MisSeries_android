package rafaxplayer.misseries.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.fragments.Capitulos_Fragment;

public class Capitulos_Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capitulos);
        ButterKnife.bind(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bund = getIntent().getExtras();

        if(bund != null){

            Capitulos_Fragment frm = (Capitulos_Fragment) getSupportFragmentManager().findFragmentById(R.id.fragmentCapitulos);

            frm.loadCapitulos(bund);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


}
