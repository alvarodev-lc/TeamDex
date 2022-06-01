package es.upm.miw.virgolini;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.upm.miw.virgolini.models.PokemonResult;
import es.upm.miw.virgolini.models.PokemonList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokedexActivity extends AppCompatActivity implements View.OnClickListener, PokemonListAdapter.OnPokemonClickListener {

    private Retrofit retrofit;
    private RecyclerView recyclerView;
    private PokemonListAdapter pokemonListAdapter;
    private ArrayList<PokemonResult> poke_list = new ArrayList<PokemonResult>();
    private int offset;
    private boolean charge_allowed;
    private String LOG_TAG = "poke_api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pokemon_recyclerview);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        pokemonListAdapter = new PokemonListAdapter(this);
        recyclerView.setAdapter(pokemonListAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy > 0) {
                    int totalItemCount = layoutManager.getItemCount();
                    int visibleItemCount = layoutManager.getChildCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (charge_allowed) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount){
                            charge_allowed = false;
                            offset += 20;
                            getPokemonData(offset);
                        }
                    }
                }
            }
        });

        String base_url = "https://pokeapi.co/api/v2/";

        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        charge_allowed = true;
        offset = 0;
        getPokemonData(offset);
    }
    private void getPokemonData(int offset){
        IPokemonEndpoint apiService = retrofit.create(IPokemonEndpoint.class);
        Call<PokemonList> pokemonResultCall = apiService.getAllPokemon(20, offset);

        pokemonResultCall.enqueue(new Callback<PokemonList>() {
            @Override
            public void onResponse(@NonNull Call<PokemonList> call, @NonNull Response<PokemonList> response) {
                charge_allowed = true;
                if (response.isSuccessful()){
                    PokemonList Pokemons = response.body();
                    Log.d(LOG_TAG, response.message());
                    assert Pokemons != null;
                    ArrayList<PokemonResult> Pokemon_list = Pokemons.getResults();
                    poke_list.addAll(Pokemon_list);
                    for(PokemonResult Pokemon: Pokemon_list){
                        String name = Pokemon.getName();
                        Log.d(LOG_TAG, name);
                        Log.d(LOG_TAG, Pokemon.getUrl());
                    }
                    pokemonListAdapter.addPokemonList(Pokemon_list);
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonList> call, @NonNull Throwable t) {
                charge_allowed = true;
                Log.d(LOG_TAG, "Error");
                t.printStackTrace();
            }
        });
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
        PokemonResult pokemon = poke_list.get(position);
        Toast.makeText(PokedexActivity.this,
               pokemon.getName() + " selected" , Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, PokemonActivity.class);
        intent.putExtra("pokemon", pokemon);
        startActivity(intent);
    }
}