package es.upm.mssde.pokedex.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.upm.mssde.pokedex.PokemonActivity;
import es.upm.mssde.pokedex.PokemonListAdapter;
import es.upm.mssde.pokedex.R;
import es.upm.mssde.pokedex.models.PokemonResult;

public class PokedexFragment extends Fragment implements View.OnClickListener, PokemonListAdapter.OnPokemonClickListener {
    private RecyclerView recyclerView;
    private PokemonListAdapter pokemonListAdapter;
    private ArrayList<PokemonResult> poke_list = new ArrayList<PokemonResult>();
    private boolean charge_allowed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.fragment_pokemon_recyclerview, null);
        recyclerView = view.findViewById(R.id.recyclerViewFragment);
        pokemonListAdapter = new PokemonListAdapter(this);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setAdapter(pokemonListAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int totalItemCount = layoutManager.getItemCount();
                    int visibleItemCount = layoutManager.getChildCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (charge_allowed) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            charge_allowed = false;
                            pokemonListAdapter.refreshPokemonData();
                        }
                    }
                }
            }
        });

        charge_allowed = true;
        pokemonListAdapter.refreshPokemonData();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(query);
                return false;
            }
        });
    }

    private void filter(String query) {
        // creating a new array list to filter our data.
        ArrayList<PokemonResult> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (PokemonResult pokemon : poke_list) {
            // checking if the entered string matched with any item of our recycler view.
            if (pokemon.getName().toLowerCase().contains(query.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(pokemon);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(getActivity(), "No pokémons found...", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            pokemonListAdapter.filterList(filteredlist);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
    }


    @Override
    public void onPokemonClick(int position) {
        ArrayList<PokemonResult> poke_list_new = pokemonListAdapter.getData();
        PokemonResult pokemon = poke_list_new.get(position);

        String poke_name = pokemon.getName();
        String poke_url = pokemon.getUrl();
        String poke_id = poke_url.substring(poke_url.length() - 2, poke_url.length() - 1);
        int poke_id_num = Integer.parseInt(poke_id);
        Log.d("poke_click", poke_name + " " + poke_id_num);

        Toast.makeText(getActivity(),
                pokemon.getName() + " selected", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), PokemonActivity.class);
        intent.putExtra("pokemon", pokemon);
        startActivity(intent);
    }
}