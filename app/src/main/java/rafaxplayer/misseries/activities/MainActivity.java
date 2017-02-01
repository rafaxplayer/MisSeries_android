package rafaxplayer.misseries.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.classes.GlobalUttilities;
import rafaxplayer.misseries.classes.updateDataSerieAsync;
import rafaxplayer.misseries.fragments.Capitulos_Fragment;
import rafaxplayer.misseries.fragments.Series_Fragment;

import static rafaxplayer.misseries.MisSeries.capitulosRef;
import static rafaxplayer.misseries.MisSeries.mAuth;

public class MainActivity extends AppCompatActivity implements Series_Fragment.OnSerieSelectedListener{

    private String TAG = ".MainActivity";
    private Menu _menu;
    private long countNovistos;
    private ValueEventListener novistosListener;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        countNovistos = 0;
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MainActivity.this,Login_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        };
        novistosListener=capitulosRef.orderByChild("visto").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                countNovistos = dataSnapshot.getChildrenCount();
                if(_menu != null)
                    _menu.findItem(R.id.news).setTitle("NO VISTOS " + countNovistos);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                if(countNovistos > 0) {
                    startActivity(new Intent(this, NoVistos_Activity.class));
                }else{
                    Toast.makeText(this, "Has visualizado todos los capitulos", Toast.LENGTH_SHORT).show();
                }
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        if(novistosListener!=null){
            capitulosRef.removeEventListener(novistosListener);
        }
        super.onDestroy();
    }

    public void showDialogNewRecipe(){
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

    private void newSerie(String code){
        try {
            updateDataSerieAsync setData = new updateDataSerieAsync(MainActivity.this, code);
            setData.execute();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void onSerieSelected(Bundle bund) {

        if(GlobalUttilities.isDualPanel(MainActivity.this)){
            Capitulos_Fragment frm =(Capitulos_Fragment) getSupportFragmentManager().findFragmentById(R.id.fragmentCapitulos);
            frm.loadCapitulos(bund);

        }else{
            Intent intent = new Intent(this, Capitulos_Activity.class);
            intent.replaceExtras(bund);
            startActivity(intent);
        }
    }
}
