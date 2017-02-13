package rafaxplayer.misseries.activities;

import android.os.Bundle;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.classes.BaseActivity;
import rafaxplayer.misseries.fragments.Capitulos_Fragment;

public class Capitulos_Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        Bundle bund = getIntent().getExtras();

        if(bund != null){
            Capitulos_Fragment frm = (Capitulos_Fragment) getSupportFragmentManager().findFragmentById(R.id.fragmentCapitulos);
            frm.loadCapitulos(bund);
        }

    }
    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_capitulos;
    }
    @Override
    protected void onResume() {
        super.onResume();

    }


}
