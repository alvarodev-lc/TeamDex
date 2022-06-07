package es.upm.miw.virgolini;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PokemonMainActivity extends AppCompatActivity {

    Button mButton1;
    Button mButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_main);

        mButton1 = findViewById(R.id.button1);
        mButton2 = findViewById(R.id.button2);

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PokemonMainActivity.this, PokedexActivity.class));
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PokemonMainActivity.this, PokemonTeamAcrivity.class));
            }
        });

    }

}
