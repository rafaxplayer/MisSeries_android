package rafaxplayer.misseries.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;
import rafaxplayer.misseries.classes.BaseActivity;
import rafaxplayer.misseries.classes.GlobalUttilities;
import rafaxplayer.misseries.classes.updateDataSerieAsync;
import rafaxplayer.misseries.fragments.Capitulos_Fragment;
import rafaxplayer.misseries.fragments.Series_Fragment;

import static rafaxplayer.misseries.MisSeries.capitulosRef;
import static rafaxplayer.misseries.MisSeries.mAuth;
import static rafaxplayer.misseries.MisSeries.notificationsRef;

public class MainActivity extends BaseActivity implements Series_Fragment.OnSerieSelectedListener {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navview)
    NavigationView navView;
    private String TAG = ".MainActivity";
    private MenuItem itemNoVistos;
    private MenuItem itemNotifications;
    private long countNovistos;
    private long countNotifications;
    private ValueEventListener novistosListener;
    private ValueEventListener notificationsListener;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LinearLayout header_back;
    private ImageView imageUser;
    private TextView username;
    private TextView useremail;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
        }
        countNovistos = 0;
        countNotifications = 0;
        View navHeader = navView.getHeaderView(0);
        itemNoVistos = navView.getMenu().findItem(R.id.menu_no_vistos);
        itemNotifications = navView.getMenu().findItem(R.id.menu_notifications);
        header_back = (LinearLayout) navHeader.findViewById(R.id.header_back);
        imageUser = (ImageView) navHeader.findViewById(R.id.profile_image);
        username = (TextView) navHeader.findViewById(R.id.username);
        useremail = (TextView) navHeader.findViewById(R.id.useremail);
        user_id = GlobalUttilities.getIntallID(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    String token = GlobalUttilities.getPrefs(getApplicationContext()).getString("token", GlobalUttilities.not_set);
                    GlobalUttilities.sendRegistrationToServer(getApplicationContext(), token);
                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    updateUser(user);
                } else {
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MainActivity.this, Login_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        };

        header_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    Toast.makeText(MainActivity.this, "Un Login...", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }
            }
        });

        novistosListener = capitulosRef.orderByChild("visto").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                countNovistos = dataSnapshot.getChildrenCount();
                if (itemNoVistos != null) {
                    itemNoVistos.setTitle("No Vistos (" + countNovistos + ")");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        notificationsListener = notificationsRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                countNotifications = dataSnapshot.getChildrenCount();
                if (itemNotifications != null) {
                    itemNotifications.setTitle("Notificaciones (" + countNotifications + ")");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {

                            case R.id.menu_no_vistos:
                                if (countNovistos > 0) {
                                    startActivity(new Intent(MainActivity.this, NoVistos_Activity.class));
                                } else {
                                    Toast.makeText(MainActivity.this, "Has visualizado todos los capitulos", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.menu_notifications:
                                if (countNotifications > 0) {
                                    startActivity(new Intent(MainActivity.this, Notifications_Activity.class));
                                } else {
                                    Toast.makeText(MainActivity.this, "No hay nuevos eventos", Toast.LENGTH_SHORT).show();
                                }

                                break;

                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });

    }

    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_main;
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

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
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
        if (novistosListener != null) {
            capitulosRef.removeEventListener(novistosListener);
        }
        if (notificationsListener != null) {
            notificationsRef.removeEventListener(novistosListener);
        }

        super.onDestroy();
    }

    public void showDialogNewSerie() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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

                String code = ((EditText) dialogView.findViewById(R.id.editCodeSerie)).getText().toString();

                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(MainActivity.this, "El codigo de la serie es necesario", Toast.LENGTH_SHORT).show();
                    return;
                }

                newSerie(code);
            }
        }).create().show();
    }

    private void newSerie(String code) {
        try {
            updateDataSerieAsync setData = new updateDataSerieAsync(MainActivity.this, code);
            setData.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onSerieSelected(Bundle bund) {


        if (GlobalUttilities.isDualPanel(MainActivity.this)) {
            Capitulos_Fragment frm = (Capitulos_Fragment) getSupportFragmentManager().findFragmentById(R.id.fragmentCapitulos);
            frm.loadCapitulos(bund);

        } else {
            Intent intent = new Intent(this, Capitulos_Activity.class);
            intent.replaceExtras(bund);
            startActivity(intent);
        }
    }


    private void updateUser(FirebaseUser user) {

        if (user != null) {

            String name = TextUtils.isEmpty(user.getDisplayName()) ? "Bienvenido" : user.getDisplayName();
            String email = TextUtils.isEmpty(user.getEmail()) ? "..." : user.getEmail();
            username.setText(name);
            useremail.setText(email);
            if (user.getPhotoUrl() != null) {
                Picasso.with(this).load(user.getPhotoUrl())
                        .resize(60, 60).centerCrop()
                        .error(R.drawable.user_placeholder)
                        .placeholder(R.drawable.user_placeholder)
                        .into(imageUser);
            }
        }
    }
}
