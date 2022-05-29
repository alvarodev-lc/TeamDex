package es.upm.miw.virgolini;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.upm.miw.virgolini.models.Pokemon;
import es.upm.miw.virgolini.models.PokemonList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCall extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private PokemonListAdapter pokemonListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pokemon_recyclerview);

        String LOG_TAG = "poke_api";

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        pokemonListAdapter = new PokemonListAdapter();
        recyclerView.setAdapter(pokemonListAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);


        String base_url = "https://pokeapi.co/api/v2/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IPokemonEndpoint apiService = retrofit.create(IPokemonEndpoint.class);

        apiService.getAllPokemon().enqueue(new Callback<PokemonList>() {
            @Override
            public void onResponse(@NonNull Call<PokemonList> call, @NonNull Response<PokemonList> response) {
                if (response.isSuccessful()){
                    PokemonList Pokemons = response.body();
                    Log.d(LOG_TAG, response.message());
                    assert Pokemons != null;
                    ArrayList<Pokemon> Pokemon_list = Pokemons.getResults();
                    for(Pokemon Pokemon: Pokemon_list){
                        String name = Pokemon.getName();
                        Log.d(LOG_TAG, name);
                        Log.d(LOG_TAG, Pokemon.getUrl());
                    }
                    pokemonListAdapter.addPokemonList(Pokemon_list);
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonList> call, @NonNull Throwable t) {
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
}