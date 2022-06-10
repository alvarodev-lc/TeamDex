package es.upm.mssde.pokedex.fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import es.upm.mssde.pokedex.PokeAPI;
import es.upm.mssde.pokedex.R;
import es.upm.mssde.pokedex.TeamBuilderListAdapter;
import es.upm.mssde.pokedex.TeamDatabase;
import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;

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

        this.teamButtonResources = new int[]{R.id.member1, R.id.member2, R.id.member3, R.id.member4, R.id.member5, R.id.member6};

        pokeAPI = new PokeAPI();

        SearchView searchBox = view.findViewById(R.id.searchbox);
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
        ArrayList<Pair<String, String>> al = teamDatabase.retrieveTeam();
        int[] teamRes = new int[al.size()];

        for (int i = 0; i < al.size(); i++) {
            String name = al.get(i).first; // gets name
            System.out.println(name.toLowerCase(Locale.ROOT));
            Pokemon pokemon = teamDatabase.getPokemon(name);
            team.add(pokemon);
            teamRes[i] = getPokemonSprite(pokemon.getName().toLowerCase(Locale.ROOT));
        }

        updateTeamBuilderUI(teamRes);
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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("search_on_click", "Clicked on " + position);
                Pokemon poke = pokeAPI.getPokemonFromName(names[position].toLowerCase(Locale.ROOT));

                if (poke != null) {
                    Log.d("search_on_click", "Added " + poke.getName());
                    addToTeam(poke);
                }
            }
        });
    }

    public int getPokemonSprite(String num) {
        String sprite_url = "https://raw.githubusercontent.com/PokeAPI/sprites/" +  "master/sprites/pokemon/" + num + ".png";

        return getResources().getIdentifier(sprite_url, "drawable", getActivity().getPackageName());
    }

    public void addToTeam(Pokemon poke) {
        ImageButton imgBtn;
        TextView txtView;

        if (team.size() < 6) {
            switch (team.size()) {
                case 0:
                    imgBtn = view.findViewById(R.id.member1);
                    imgBtn.setImageResource(getPokemonSprite(poke.getNum().toLowerCase(Locale.ROOT)));
                    txtView = view.findViewById(R.id.member1_name);
                    txtView.setText(poke.getName());
                    break;
                case 1:
                    imgBtn = view.findViewById(R.id.member2);
                    imgBtn.setImageResource(getPokemonSprite(poke.getNum().toLowerCase(Locale.ROOT)));
                    txtView = view.findViewById(R.id.member2_name);
                    txtView.setText(poke.getName());
                    break;
                case 2:
                    imgBtn = view.findViewById(R.id.member3);
                    imgBtn.setImageResource(getPokemonSprite(poke.getNum().toLowerCase(Locale.ROOT)));
                    txtView = view.findViewById(R.id.member3_name);
                    txtView.setText(poke.getName());
                    break;
                case 3:
                    imgBtn = view.findViewById(R.id.member4);
                    imgBtn.setImageResource(getPokemonSprite(poke.getNum().toLowerCase(Locale.ROOT)));
                    txtView = view.findViewById(R.id.member4_name);
                    txtView.setText(poke.getName());
                    break;
                case 4:
                    imgBtn = view.findViewById(R.id.member5);
                    imgBtn.setImageResource(getPokemonSprite(poke.getNum().toLowerCase(Locale.ROOT)));
                    txtView = view.findViewById(R.id.member5_name);
                    txtView.setText(poke.getName());
                    break;
                case 5:
                    imgBtn = view.findViewById(R.id.member6);
                    imgBtn.setImageResource(getPokemonSprite(poke.getNum().toLowerCase(Locale.ROOT)));
                    txtView = view.findViewById(R.id.member6_name);
                    txtView.setText(poke.getName());
                    break;
                default:
            }

            Log.d("addToTeam", "Added " + poke.getName() + " to team");

            team.add(poke);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Team already has 6 members!", Toast.LENGTH_LONG);
        }
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
        updateTeamBuilderUI(teamRes);

    }

    public void removePokemon(int index) {
        if (index > team.size()) return;

        int[] teamRes = new int[team.size() - 1];

        List<Pokemon> poke_list_temp = new CopyOnWriteArrayList<>();

        for (Pokemon p : team) {
            poke_list_temp.add(p);
        }

        Pokemon removeMe = poke_list_temp.get(index);

        poke_list_temp.remove(index);

        team = new ArrayList<>();

        for (int i = 0; i < poke_list_temp.size(); i++) {
            Pokemon tempP = poke_list_temp.get(i);
            team.add(tempP);
            teamRes[i] = getPokemonSprite(tempP.getName().toLowerCase(Locale.ROOT));
        }

        teamDatabase.delete(removeMe.getName());
        updateTeamBuilderUI(teamRes);
        ;
    }

    private void updateTeamBuilderUI(int[] teamRes) {
        ImageButton imgBttn;

        for (int i = 0; i < 6; i++) {
            imgBttn = view.findViewById(teamButtonResources[i]);

            if (i >= team.size()) {
                imgBttn.setImageResource(R.drawable.pokeball);
            } else {
                imgBttn.setImageResource(teamRes[i]);
            }
        }
    }

    public void resetTeam(View v) {
        int size = team.size();
        for (int i = 0; i < size; i++) {
            removePokemon(0);
        }
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