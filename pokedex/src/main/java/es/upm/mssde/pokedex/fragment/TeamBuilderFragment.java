package es.upm.mssde.pokedex.fragment;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import es.upm.mssde.pokedex.PokeAPI;
import es.upm.mssde.pokedex.R;
import es.upm.mssde.pokedex.TeamBuilderListAdapter;
import es.upm.mssde.pokedex.TeamDatabase;
import es.upm.mssde.pokedex.models.PokeDB;
import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.Type;
import es.upm.mssde.pokedex.models.TypeList;

public class TeamBuilderFragment extends Fragment {

    private TeamDatabase teamDatabase;
    private ArrayList<Pokemon> team;
    private int[] teamButtonResources;
    private PokeAPI pokeAPI;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(getActivity(), R.layout.team_builder, null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.team = new ArrayList<>();
        teamDatabase = new TeamDatabase(getActivity());

        pokeAPI = new PokeAPI();

        SearchView searchBox = view.findViewById(R.id.search_box);
        searchBox.setQueryHint("Search for a Specific Pokemon");

        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateTeamBuilderListView(searchPokemon(query));
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
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
    }

    private ArrayList<String> searchPokemon(String query) {
        ArrayList<String> results = new ArrayList<>();

        if (pokeAPI.poke_list.size() == 0) {
            pokeAPI.getPokemons(1000);
        }

        for (PokemonResult pokemon : pokeAPI.poke_list) {
            if (pokemon.getName().toLowerCase().contains(query.toLowerCase())) {
                results.add(pokemon.getName());
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
            addCardView(num, name, type);
        }
    }

    private void updateTeamBuilderListView(ArrayList<String> poke_names) {
        String[] names = new String[poke_names.size()];

        int i = 0;
        for (String name : poke_names) {
            names[i] = name;
            i++;

        }

        TeamBuilderListAdapter adapter =
                new TeamBuilderListAdapter(getActivity(), names);

        ListView lv = view.findViewById(R.id.dexView);

        lv.post(new Runnable() {
            @Override
            public void run() {
                lv.setAdapter(adapter);
            }
        });

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("search_on_click", "Clicked on " + position);
            Pokemon poke = pokeAPI.getPokemonFromName(names[position].toLowerCase(Locale.ROOT));

            if (poke != null) {
                Log.d("search_on_click", "Added " + poke.getName());
                addToTeam(poke);
            }
        });
    }

    public int getPokemonSprite(String num) {
        String sprite_url = "https://raw.githubusercontent.com/PokeAPI/sprites/" + "master/sprites/pokemon/" + num + ".png";

        return getResources().getIdentifier(sprite_url, "drawable", getActivity().getPackageName());
    }

    public void addToTeam(Pokemon poke) {
        if (team.size() < 6) {
            PokeDB pokeDB = new PokeDB();
            pokeDB.setNum(poke.getNum());
            pokeDB.setName(poke.getName());
            List<TypeList> poke_types = poke.getTypes();
            if (poke_types.size() == 11) {
                pokeDB.setType1(poke_types.get(0).getType());
            } else {
                pokeDB.setType1(poke_types.get(0).getType());
                pokeDB.setType2(poke_types.get(1).getType());
            }
            addCardView(pokeDB.getNum(), pokeDB.getName(), pokeDB.getTypes());
            team.add(poke);

            Log.d("addToTeam", "Added " + poke.getName() + " to team");
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Team already has 6 members!", Toast.LENGTH_LONG);
        }
    }

    public void addCardView(String poke_num, String poke_name, ArrayList<Type> poke_types) {
        LinearLayout team_list = view.findViewById(R.id.team_list);
        // create carddview that contains the pokemon name, its types and its sprite
        CardView cardView = new CardView(getActivity());
        cardView.setRadius(10);
        cardView.setCardElevation(10);
        cardView.setMaxCardElevation(10);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(10, 10, 10, 10);
        cardView.setPreventCornerOverlap(false);

        cardView.setOnClickListener(v -> removePokemonFromView(v));

        //add content to cardview
        LinearLayout cardViewContent = new LinearLayout(getActivity());
        cardViewContent.setOrientation(LinearLayout.VERTICAL);
        cardViewContent.setGravity(Gravity.CENTER);

        TextView poke_name_view = new TextView(getActivity());
        poke_name_view.setText(poke_name);
        poke_name_view.setTextSize(20);
        poke_name_view.setTypeface(null, Typeface.BOLD);
        poke_name_view.setGravity(Gravity.CENTER);

        ImageView poke_sprite_view = new ImageView(getActivity());
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

        List<Pokemon> myList = new CopyOnWriteArrayList<>();

        for (Pokemon p : team) {
            myList.add(p);
        }

        Pokemon removeMe = myList.get(index - 1);

        myList.remove(index - 1);

        team = new ArrayList<>();

        for (int i = 0; i < myList.size(); i++) {
            Pokemon tempP = myList.get(i);
            team.add(tempP);
            teamRes[i] = getPokemonSprite(tempP.getName().toLowerCase(Locale.ROOT));
        }

        teamDatabase.delete(removeMe.getName());
    }

    public void resetTeam(View v) {
        // delete all pokemon from team_list
        LinearLayout team_list = view.findViewById(R.id.team_list);
        team_list.removeAllViews();
    }

    public void saveTeam(View view) {
        if (team.size() == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "Team is empty!", Toast.LENGTH_LONG);
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

        Toast.makeText(getActivity().getApplicationContext(), "Team saved!", Toast.LENGTH_LONG);
    }
}