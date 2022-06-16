package es.upm.mssde.pokedex;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.upm.mssde.pokedex.fragment.TeamViewerFragment;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    static final String LOG_TAG = "vg";

    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Fields
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        // Click listeners
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.buttonLogIn).setOnClickListener(this);
        //findViewById(R.id.statusSwitch).setClickable(false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonLogIn) {
            logInWithCredentials();
        } else if (i == R.id.buttonSignUp) {
            Intent sign_up_activity = new Intent(this, SignUpActivity.class);
            startActivity(sign_up_activity);
        }
    }

    private boolean validateLinkForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.field_required));
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.field_required));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }

    private void logInWithCredentials() {
        if (!validateLinkForm()) {
            return;
        }
        // Get email and password from form
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        // Create EmailAuthCredential with email and password
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        // [START signin_with_email_and_password]
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(LOG_TAG, "logInWithCredentials:success");
                            Toast.makeText(LogInActivity.this,
                                    "Authentication successful", Toast.LENGTH_SHORT).show();
                            Intent api_call_activity = new Intent(LogInActivity.this,
                                    FragmentActivity.class);
                            startActivity(api_call_activity);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "logInWithCredentials:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}