package es.upm.mssde.pokedex;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import es.upm.mssde.pokedex.models.PokemonResult;

public class TeamBuilderActivity extends AppCompatActivity {

    private TeamDatabase teamDatabase;
    private ArrayList<PokemonResult> team;
    private TeamBuilderListAdapter teamBuilderListAdapter;
    private String teamID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_builder);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        team = new ArrayList<>();
        teamDatabase = new TeamDatabase(TeamBuilderActivity.this);
        teamBuilderListAdapter = new TeamBuilderListAdapter(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.getString("team_id") != null) {
                teamID = extras.getString("team_id");
            } else {
                teamID = String.valueOf(teamDatabase.getLatestTeamId() + 1);
            }
        } else {
            teamID = savedInstanceState.getString("team_id");
        }

        SearchView searchBox = findViewById(R.id.search_box);
        searchBox.setQueryHint("Search for a Pokemon");

        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateTeamBuilderListView(searchPokemon(query));
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                ListView lv_ = findViewById(R.id.poke_search_list);
                lv_.setVisibility(View.VISIBLE);

                updateTeamBuilderListView(searchPokemon(query));
                return false;
            }
        });

        loadTeamFromDB();
        teamBuilderListAdapter.getPokemonsData(913);

        addOnClickListenerToResetTeamButton();
        addOnClickListenerToSaveTeamButton();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<PokemonResult> searchPokemon(String query) {
        Log.d("POKEDEX_SEARCH", "Searching for: " + query);
        ArrayList<PokemonResult> results = new ArrayList<>();

        Log.d("POKEDEX_SEARCH", teamBuilderListAdapter.poke_list.size() + " pokemons in list");

        for (PokemonResult pokemon : teamBuilderListAdapter.poke_list) {
            if (pokemon.getName().toLowerCase().contains(query.toLowerCase())) {
                Log.d("POKEMON_FOUND", pokemon.getName());
                results.add(pokemon);
            }
        }

        if (results.size() > 5) {
            results.subList(5, results.size()).clear();
        }

        return results;
    }

    @Override
    public void onDestroy() {
        teamDatabase.close();
        super.onDestroy();
    }

    private void loadTeamFromDB() {
        ArrayList<PokemonResult> team = teamDatabase.getTeam(teamID);

        Log.d("LOAD_TEAM_DB", "Team size: " + team.size());

        for (int i = 0; i < team.size(); i++) {
            PokemonResult pokemon = team.get(i);
            int num = pokemon.getNum();
            String name = pokemon.getName();
            Log.d("TeamBuilderFragment", "Loading team from DB: " + name);
            addCardView(num, name);
        }

        this.team = team;
    }

    private void updateTeamBuilderListView(ArrayList<PokemonResult> poke_list) {
        TeamBuilderListAdapter adapter = new TeamBuilderListAdapter(this, poke_list);

        ListView lv = findViewById(R.id.poke_search_list);

        lv.post(() -> lv.setAdapter(adapter));

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("search_on_click", "Clicked on " + position);
            teamBuilderListAdapter.getPokemonData(position);

            PokemonResult poke = poke_list.get(position);

            if (poke != null) {
                if (team.contains(poke)) {
                    Toast.makeText(TeamBuilderActivity.this, "Pokémon already added!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("search_on_click", "Added " + poke.getName());
                    addToTeam(poke);

                    Toast.makeText(TeamBuilderActivity.this, "Added " + poke.getName(), Toast.LENGTH_SHORT).show();
                }

                teamBuilderListAdapter.poke_list.remove(position);

                SearchView searchBox = findViewById(R.id.search_box);
                searchBox.setQuery("", false);

                lv.setVisibility(View.GONE);
            }
        });
    }

    public int getPokemonSprite(String num) {
        String sprite_url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + num + ".png";

        return getResources().getIdentifier(sprite_url, "drawable", this.getPackageName());
    }

    public void addToTeam(PokemonResult poke) {
        if (team.size() < 6) {
            addCardView(poke.getNum(), poke.getName());
            team.add(poke);

            Log.d("addToTeam", "Added " + poke.getName() + " to team");
        } else {
            Toast.makeText(this.getApplicationContext(), "Team already has 6 members!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePokemon(View v) {
        GridLayout team_list = findViewById(R.id.team_list);
        int child_count = team_list.getChildCount();

        for (int i = 0; i < child_count; i++) {
            View child = team_list.getChildAt(i);
            if (child == v.getParent()) {
                team_list.removeViewAt(i);
                team.remove(i);
                break;
            }
        }
    }

    public void addCardView(int poke_num, String poke_name) {
        GridLayout team_list = findViewById(R.id.team_list);
        // create carddview that contains the pokemon name, its types and its sprite
        CardView cardView = new CardView(this);
        cardView.setRadius(100);
        cardView.setCardElevation(10);
        cardView.setMaxCardElevation(10);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(30, 30, 30, 30);
        cardView.setPreventCornerOverlap(false);

        cardView.setOnClickListener(this::goToPokemonStats);

        //add content to cardview
        LinearLayout cardViewContent = new LinearLayout(this);
        cardViewContent.setOrientation(LinearLayout.HORIZONTAL);
        cardViewContent.setGravity(Gravity.CENTER);

        TextView poke_name_view = new TextView(this);
        poke_name_view.setText(poke_name);
        poke_name_view.setTextSize(20);
        poke_name_view.setTypeface(null, Typeface.BOLD);
        poke_name_view.setGravity(Gravity.CENTER);

        ImageView poke_sprite_view = new ImageView(this);
        poke_sprite_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        poke_sprite_view.setAdjustViewBounds(true);
        poke_sprite_view.setMaxHeight(300);
        poke_sprite_view.setMaxWidth(300);

        // align sprite to left
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 10, 0);
        poke_sprite_view.setLayoutParams(params);

        Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + poke_num + ".png")
                .into(poke_sprite_view);

        // add button to delete pokemon
        Button delete_button = new Button(this);
        delete_button.setText("X");
        delete_button.setOnClickListener(this::deletePokemon);

        cardViewContent.addView(poke_sprite_view);
        cardViewContent.addView(poke_name_view);

        cardView.addView(cardViewContent);

        team_list.addView(cardView);
    }

    public void goToPokemonStats(View view) {
        Toast.makeText(this, "Show stats or details of the clicked Pokemon", Toast.LENGTH_SHORT).show();
    }

    private void addOnClickListenerToResetTeamButton() {
        Button resetButton = findViewById(R.id.reset_team_button);
        resetButton.setOnClickListener(v -> {
            team.clear();
            GridLayout team_list = findViewById(R.id.team_list);
            team_list.removeAllViews();
            Toast.makeText(this, "Team reset!", Toast.LENGTH_SHORT).show();
        });
    }

    private void addOnClickListenerToSaveTeamButton() {
        MaterialButton saveButton = findViewById(R.id.save_team_button);
        saveButton.setOnClickListener(v -> {
            if (!team.isEmpty()) {
                teamDatabase.addTeam(team, teamID);
                Toast.makeText(this, "Team saved!", Toast.LENGTH_SHORT).show();
            } else {
                teamDatabase.deleteTeam(teamID);
                Toast.makeText(this, "Team deleted!", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }


}
