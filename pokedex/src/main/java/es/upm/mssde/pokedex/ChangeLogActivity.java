package es.upm.mssde.pokedex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedDispatcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangeLogActivity extends AppCompatActivity {
    private TextView changelogTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changelog_activity);

        changelogTextView = findViewById(R.id.changelogTextView);
        ImageButton buttonBack = findViewById(R.id.button_back);
        Button buttonHome = findViewById(R.id.button_home);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GithubService service = retrofit.create(GithubService.class);
        Call<GithubService.GitHubContent> call = service.getChangelog("alvarodev-lc", "TeamDex", "CHANGELOG.md");

        call.enqueue(new Callback<GithubService.GitHubContent>() {
            @Override
            public void onResponse(@NonNull Call<GithubService.GitHubContent> call, @NonNull Response<GithubService.GitHubContent> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String content = response.body().content;
                    String decodedContent = new String(Base64.decode(content, Base64.DEFAULT));
                    changelogTextView.setText(decodedContent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GithubService.GitHubContent> call, @NonNull Throwable t) {
                changelogTextView.setText("Failed to load changelog.");
            }
        });

        // Set up button click listeners
        buttonBack.setOnClickListener(v -> {
            OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
            dispatcher.onBackPressed(); // Navigate back using OnBackPressedDispatcher
        });

        buttonHome.setOnClickListener(v -> {
            Intent intent = new Intent(ChangeLogActivity.this, MainActivity.class); // Replace MainActivity with your target Activity
            startActivity(intent);
        });
    }
}
