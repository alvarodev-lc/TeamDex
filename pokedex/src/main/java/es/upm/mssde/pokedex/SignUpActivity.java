package es.upm.mssde.pokedex;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    static final String LOG_TAG = "vg";

    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Fields
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        // Click listeners
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.buttonLogIn).setOnClickListener(this);

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
        if (i == R.id.buttonSignUp) {
            createUserWithCredentials();
        }
        else if (i == R.id.buttonLogIn){
            Intent log_in_activity = new Intent(this, LogInActivity.class);
            startActivity(log_in_activity);
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

    private void createUserWithCredentials() {
        if (!validateLinkForm()) {
            return;
        }

        // Get email and password from form
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, firebase account created
                        Log.d(LOG_TAG, "createUserWithEmail:success");
                        Toast.makeText(SignUpActivity.this, "Successfully created your account. Please Log in!",
                                Toast.LENGTH_LONG).show();
                        Intent api_call_activity = new Intent(SignUpActivity.this,
                                LogInActivity.class);
                        startActivity(api_call_activity);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(LOG_TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed." + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}