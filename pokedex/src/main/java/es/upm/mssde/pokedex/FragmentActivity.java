package es.upm.mssde.pokedex;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import es.upm.mssde.pokedex.R;
import es.upm.mssde.pokedex.fragment.PokedexFragment;

public class FragmentActivity extends AppCompatActivity implements View.OnClickListener {

    private PokedexFragment pokedexFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);
        bindViews();
        initData();
    }

    private void initData() {
        pokedexFragment = new PokedexFragment();

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
        }
    }
}
