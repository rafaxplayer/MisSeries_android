package rafaxplayer.misseries.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.misseries.R;

import static rafaxplayer.misseries.MisSeries.mAuth;

public class Login_Activity extends AppCompatActivity {
    @BindView(R.id.editEmail)
    EditText email;
    @BindView(R.id.editPassword)
    EditText password;
    @BindView(R.id.buttonSigIn)
    Button sigin;
    private String TAG = ".LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText())) {
                    Toast.makeText(Login_Activity.this, "Required email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password.getText())) {
                    Toast.makeText(Login_Activity.this, "Required password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(Login_Activity.this, "Ok Login correct!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {

                                    Toast.makeText(Login_Activity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    email.setText("");
                                    password.setText("");
                                }

                            }
                        });
            }
        });
    }
}
