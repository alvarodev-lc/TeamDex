package es.upm.miw.virgolini;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import es.upm.miw.virgolini.fragment.PokedexFragment;
import es.upm.miw.virgolini.fragment.PokemonTeamBuilderFragment;

public class FragmentActivity extends AppCompatActivity implements View.OnClickListener {

    private PokedexFragment pokedexFragment;
    private PokemonTeamBuilderFragment pokemonTeamBuilderFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);
        bindViews();
        initData();
    }

    private void initData() {
        pokedexFragment = new PokedexFragment();
        pokemonTeamBuilderFragment = new PokemonTeamBuilderFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, pokedexFragment).commit();
    }

    private void bindViews() {
        RadioButton rbTab1 = findViewById(R.id.rb_pokedex);
        RadioButton rbTab2 = findViewById(R.id.rb_team_builder);

        rbTab1.setChecked(true);

        rbTab1.setOnClickListener(this);
        rbTab2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rb_pokedex) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, pokedexFragment).commit();
        } else if (v.getId() == R.id.rb_team_builder) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, pokemonTeamBuilderFragment).commit();
        }
    }
}
