package es.upm.mssde.pokedex;

import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;


import es.upm.mssde.pokedex.fragment.PokedexFragment;
import es.upm.mssde.pokedex.fragment.TeamViewerFragment;

public class FragmentActivity extends AppCompatActivity implements View.OnClickListener {

    private PokedexFragment pokedexFragment;
    private TeamViewerFragment teamViewerFragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_fragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RadioGroup radioGroup = findViewById(R.id.rg_navigation); // I'll add this ID to XML
        if (radioGroup == null) {
            // Fallback if ID is different, let's find it by type if possible or just use the parent of a button
            radioGroup = (RadioGroup) findViewById(R.id.rb_pokedex).getParent();
        }

        RadioGroup finalRadioGroup = radioGroup;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            toolbar.setPadding(0, insets.top, 0, 0);
            if (finalRadioGroup != null) {
                finalRadioGroup.setPadding(
                        finalRadioGroup.getPaddingLeft(),
                        finalRadioGroup.getPaddingTop(),
                        finalRadioGroup.getPaddingRight(),
                        insets.bottom
                );
            }
            return windowInsets;
        });

        fm = getSupportFragmentManager();

        initData();
        showFragment(1);
    }

    private void initData() {
        RadioButton rbTab1 = findViewById(R.id.rb_pokedex);
        RadioButton rbTab2 = findViewById(R.id.rb_team_builder);

        rbTab1.setChecked(true);

        rbTab1.setOnClickListener(this);
        rbTab2.setOnClickListener(this);
    }

    public void showFragment(int index) {
        FragmentTransaction ft = fm.beginTransaction();

        hideFragments(ft);

        switch (index) {
            case 1:
                if (pokedexFragment != null) {
                    ft.show(pokedexFragment);
                } else {
                    pokedexFragment = new PokedexFragment();
                    ft.add(R.id.fl_container, pokedexFragment);
                }
                break;

            case 2:
                if (teamViewerFragment != null) {
                    ft.show(teamViewerFragment);
                } else {
                    teamViewerFragment = new TeamViewerFragment();
                    ft.add(R.id.fl_container, teamViewerFragment);
                }
                break;
        }
        ft.commit();
    }

    public void hideFragments(FragmentTransaction ft) {
        if (pokedexFragment != null)
            ft.hide(pokedexFragment);
        if (teamViewerFragment != null)
            ft.hide(teamViewerFragment);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rb_pokedex) {
            showFragment(1);
        } else if (v.getId() == R.id.rb_team_builder) {
            showFragment(2);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
            return true;
        }
        return false;
    }
}
