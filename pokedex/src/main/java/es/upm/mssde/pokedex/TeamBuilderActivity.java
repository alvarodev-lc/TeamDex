package es.upm.mssde.pokedex;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import es.upm.mssde.pokedex.models.PokeDB;
import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.Type;
import es.upm.mssde.pokedex.models.TypeList;

public class TeamBuilderActivity extends AppCompatActivity {

    private TeamDatabase teamDatabase;
    private ArrayList<PokemonResult> team;
    private TeamBuilderListAdapter teamBuilderListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_builder);

        team = new ArrayList<>();
        teamDatabase = new TeamDatabase(TeamBuilderActivity.this);

        teamBuilderListAdapter = new TeamBuilderListAdapter(this);

        SearchView searchBox = findViewById(R.id.search_box);
        searchBox.setQueryHint("Search for a Specific Pokemon");

        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateTeamBuilderListView(searchPokemon(query));
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                updateTeamBuilderListView(searchPokemon(query));
                return false;
            }
        });

        loadTeamFromDB();

        teamBuilderListAdapter.getPokemonsData(1000);
    }

    private ArrayList<PokemonResult> searchPokemon(String query) {
        ArrayList<PokemonResult> results = new ArrayList<>();

        for (PokemonResult pokemon : teamBuilderListAdapter.poke_list) {
            if (pokemon.getName().toLowerCase().contains(query.toLowerCase())) {
                results.add(pokemon);
            }
        }
        return results;
    }

    @Override
    public void onDestroy() {
        teamDatabase.close();
        super.onDestroy();
    }

    private void loadTeamFromDB() {
        ArrayList<PokeDB> al = teamDatabase.retrieveTeam();

        for (int i = 0; i < al.size(); i++) {
            String num = al.get(i).getNum();
            String name = al.get(i).getName();
            ArrayList<Type> type = al.get(i).getTypes();
            Log.d("TeamBuilderFragment", "Loading team from DB: " + name + " " + type);
            addCardView(num, name);
        }
    }

    private void updateTeamBuilderListView(ArrayList<PokemonResult> poke_list) {
        TeamBuilderListAdapter adapter = new TeamBuilderListAdapter(this, poke_list);

        ListView lv = findViewById(R.id.dexView);

        lv.post(new Runnable() {
            @Override
            public void run() {
                lv.setAdapter(adapter);
            }
        });

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("search_on_click", "Clicked on " + position);
            teamBuilderListAdapter.getPokemonData(position);

            PokemonResult poke = poke_list.get(position);

            if (poke != null) {
                Log.d("search_on_click", "Added " + poke.getName());
                addToTeam(poke);
            }
        });
    }

    public int getPokemonSprite(String num) {
        String sprite_url = "https://raw.githubusercontent.com/PokeAPI/sprites/" + "master/sprites/pokemon/" + num + ".png";

        return getResources().getIdentifier(sprite_url, "drawable", this.getPackageName());
    }

    public void addToTeam(PokemonResult poke) {
        if (team.size() < 6) {
            PokeDB pokeDB = new PokeDB();
            pokeDB.setNum(String.valueOf(poke.getNum()));
            pokeDB.setName(poke.getName());

            addCardView(pokeDB.getNum(), pokeDB.getName());
            team.add(poke);

            Log.d("addToTeam", "Added " + poke.getName() + " to team");
        } else {
            Toast.makeText(this.getApplicationContext(), "Team already has 6 members!", Toast.LENGTH_LONG);
        }
    }

    public void addCardView(String poke_num, String poke_name) {
        LinearLayout team_list = findViewById(R.id.team_list);
        // create carddview that contains the pokemon name, its types and its sprite
        CardView cardView = new CardView(this);
        cardView.setRadius(10);
        cardView.setCardElevation(10);
        cardView.setMaxCardElevation(10);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(10, 10, 10, 10);
        cardView.setPreventCornerOverlap(false);

        cardView.setOnClickListener(v -> removePokemonFromView(v));

        //add content to cardview
        LinearLayout cardViewContent = new LinearLayout(this);
        cardViewContent.setOrientation(LinearLayout.VERTICAL);
        cardViewContent.setGravity(Gravity.CENTER);

        TextView poke_name_view = new TextView(this);
        poke_name_view.setText(poke_name);
        poke_name_view.setTextSize(20);
        poke_name_view.setTypeface(null, Typeface.BOLD);
        poke_name_view.setGravity(Gravity.CENTER);

        ImageView poke_sprite_view = new ImageView(this);
        poke_sprite_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        poke_sprite_view.setAdjustViewBounds(true);
        poke_sprite_view.setMaxHeight(100);
        poke_sprite_view.setMaxWidth(100);

        Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + poke_num + ".png")
                .into(poke_sprite_view);

        cardViewContent.addView(poke_name_view);
        cardViewContent.addView(poke_sprite_view);

        cardView.addView(cardViewContent);

        team_list.addView(cardView);
    }

    public void removePokemonFromView(View v) {
        if (team.size() == 0) return;
        int index = Integer.parseInt(v.getTag().toString());

        if (index > team.size()) return;

        int[] teamRes = new int[team.size() - 1];

        List<PokemonResult> myList = new CopyOnWriteArrayList<>();

        for (PokemonResult p : team) {
            myList.add(p);
        }

        PokemonResult removeMe = myList.get(index - 1);

        myList.remove(index - 1);

        team = new ArrayList<>();

        for (int i = 0; i < myList.size(); i++) {
            PokemonResult tempP = myList.get(i);
            team.add(tempP);
            teamRes[i] = getPokemonSprite(tempP.getName().toLowerCase(Locale.ROOT));
        }

        teamDatabase.delete(removeMe.getName());
    }

    public void resetTeam(View v) {
        // delete all pokemon from team_list
        LinearLayout team_list = v.findViewById(R.id.team_list);
        team_list.removeAllViews();
    }

    public void saveTeam(View view) {
        if (team.size() == 0) {
            Toast.makeText(this.getApplicationContext(), "Team is empty!", Toast.LENGTH_LONG);
            return;
        }

        /*
        String teamName = ((EditText) findViewById(R.id.teamName)).getText().toString();

        if (teamName.equals("")) {
            Toast.makeText(getApplicationContext(), "Team name cannot be empty!", Toast.LENGTH_LONG);
            return;
        }
        */

        teamDatabase.addTeam(team);

        Toast.makeText(this.getApplicationContext(), "Team saved!", Toast.LENGTH_LONG);
    }
}