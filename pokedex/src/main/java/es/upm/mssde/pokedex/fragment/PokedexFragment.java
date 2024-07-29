package es.upm.mssde.pokedex.fragment;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;

import es.upm.mssde.pokedex.ChangeLogActivity;
import es.upm.mssde.pokedex.PokemonActivity;
import es.upm.mssde.pokedex.PokedexListAdapter;
import es.upm.mssde.pokedex.R;
import es.upm.mssde.pokedex.models.PokemonResult;

public class PokedexFragment extends Fragment implements View.OnClickListener, PokedexListAdapter.OnPokemonClickListener, MenuProvider {
    private RecyclerView recyclerView;
    private PokedexListAdapter pokedexListAdapter;
    private boolean charge_allowed;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_pokemon_recyclerview, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewFragment);
        pokedexListAdapter = new PokedexListAdapter(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        recyclerView.setAdapter(pokedexListAdapter);
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
                            pokedexListAdapter.refreshPokemonData();
                        }
                    } else {
                        charge_allowed = true;
                    }
                }
            }
        });

        charge_allowed = true;
        pokedexListAdapter.refreshPokemonData();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.search_menu, menu);
        menuInflater.inflate(R.menu.nav_menu, menu);

        // Get the menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // Get the SearchView and set the searchable configuration.
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Set the listener for query text changes.
        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("PokedexFragment", "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // Filter the RecyclerView.
                filter(query);
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                Log.d("PokedexFragment", "onMenuItemActionExpand");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                pokedexListAdapter.unfilter();
                Log.d("PokedexFragment", "onMenuItemActionCollapse");
                return true;
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_changelog) {
            Intent intent = new Intent(getActivity(), ChangeLogActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    private void filter(String query) {
        // Creating a new array list to filter our data.
        ArrayList<PokemonResult> poke_list = pokedexListAdapter.getUnfilteredData();
        ArrayList<PokemonResult> filtered_list = new ArrayList<>();

        Log.d("PokedexFragment", "filter: " + pokedexListAdapter.getItemCount());
        Log.d("PokedexFragment", "filter: " + query);

        // Running a for loop to compare elements.
        for (PokemonResult pokemon : poke_list) {
            // Checking if the entered string matches with any item of our RecyclerView.
            if (pokemon.getName().toLowerCase().contains(query.toLowerCase())) {
                // If the item matches, add it to the filtered list.
                filtered_list.add(pokemon);
            }
        }

        if (filtered_list.isEmpty()) {
            // If no item is added to the filtered list, display a toast message.
            Toast.makeText(getActivity(), "No pokémon found...", Toast.LENGTH_SHORT).show();
        } else {
            // Pass the filtered list to the adapter class.
            pokedexListAdapter.filterList(filtered_list);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        Log.d("Pokedex Fragment", "onClick: " + i);
    }

    @Override
    public void onPokemonClick(int position) {
        ArrayList<PokemonResult> poke_list_new = pokedexListAdapter.getData();
        PokemonResult pokemon = poke_list_new.get(position);

        String poke_name = pokemon.getName();
        String poke_url = pokemon.getUrl();
        String poke_id = poke_url.substring(poke_url.length() - 2, poke_url.length() - 1);
        int poke_id_num = Integer.parseInt(poke_id);
        Log.d("poke_click", poke_name + " " + poke_id_num);

        Toast.makeText(getActivity(), pokemon.getName() + " selected", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), PokemonActivity.class);
        intent.putExtra("pokemon", pokemon);
        startActivity(intent);
    }
}
