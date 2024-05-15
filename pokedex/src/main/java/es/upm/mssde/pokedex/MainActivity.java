package es.upm.mssde.pokedex;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Click listeners
        findViewById(R.id.buttonNoAccContinue).setOnClickListener(this);
        findViewById(R.id.buttonAccContinue).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        Log.i("MainActivity", "No account button clicked");
        if (i == R.id.buttonNoAccContinue) {
            Intent api_call_activity = new Intent(this, FragmentActivity.class);
            startActivity(api_call_activity);
        } else if (i == R.id.buttonAccContinue) {
            Intent log_in_activity = new Intent(this, LogInActivity.class);
            startActivity(log_in_activity);
        }
    }
}
