package es.upm.mssde.pokedex;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import es.upm.mssde.pokedex.fragment.PokedexFragment;
import es.upm.mssde.pokedex.fragment.TeamViewerFragment;

public class FragmentActivity extends AppCompatActivity implements View.OnClickListener {

    private PokedexFragment pokedexFragment;
    private TeamViewerFragment teamViewerFragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_fragment);

        fm = getFragmentManager();

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
                }
                else {
                    pokedexFragment = new PokedexFragment();
                    ft.add(R.id.fl_container, pokedexFragment);
                }
                break;

            case 2:
                if (teamViewerFragment != null) {
                    ft.show(teamViewerFragment);
                }
                else {
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

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
            return true;
        }
        return false;
    }
}
