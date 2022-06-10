package es.upm.mssde.pokedex;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonList;
import es.upm.mssde.pokedex.models.PokemonResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokeAPI implements MyObservable {

    private Retrofit retrofit;
    public int POKEMON_MAX_RESULTS = 100;
    public ArrayList<PokemonResult> poke_list;
    private Pokemon queryPokemon;
    private List<MyObserver> myObservers;

    public PokeAPI() {
        String base_url = "https://pokeapi.co/api/v2/";

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        poke_list = new ArrayList<>();
        myObservers = new ArrayList<>();
    }

    public void getPokemonData(int poke_num) {
        IPokemonEndpoint apiService = retrofit.create(IPokemonEndpoint.class);
        String poke_id = String.valueOf(poke_num);
        Call<Pokemon> pokemonResultCall = apiService.getPokemon(poke_id);

        pokemonResultCall.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(@NonNull Call<Pokemon> call, @NonNull Response<Pokemon> response) {
                if (response.isSuccessful()) {
                    Pokemon pokemon = response.body();
                    Log.d("poke_request_db", response.message());
                    assert pokemon != null;

                    queryPokemon = pokemon;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Pokemon> call, @NonNull Throwable t) {
                Log.d("poke_request_db", "Error");
                t.printStackTrace();
            }
        });
    }

    public void getPokemons(int offset){
        Log.d("getPokemonData", "offset: " + offset);
        IPokemonEndpoint apiService = retrofit.create(IPokemonEndpoint.class);
        Call<PokemonList> pokemonResultCall = apiService.getAllPokemon(POKEMON_MAX_RESULTS, offset);

        pokemonResultCall.enqueue(new Callback<PokemonList>() {
            @Override
            public void onResponse(@NonNull Call<PokemonList> call, @NonNull Response<PokemonList> response) {
                if (response.isSuccessful()){
                    PokemonList Pokemons = response.body();
                    Log.d("poke_api_all", response.message());
                    assert Pokemons != null;
                    ArrayList<PokemonResult> Pokemon_list = Pokemons.getResults();
                    poke_list.addAll(Pokemon_list);
                    notifyObserversPokemonData();
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonList> call, @NonNull Throwable t) {
                Log.d("poke_api_all", "Error");
                t.printStackTrace();
            }
        });
    }

    public Pokemon getPokemonFromName(String name) {
        Pokemon poke = null;

        Log.d("poke_api_name", name);

        for (PokemonResult poke_ : poke_list) {
            Log.d("poke_api_name", poke_.getName().toLowerCase(Locale.ROOT));
            if (poke_.getName().toLowerCase(Locale.ROOT).equals(name.toLowerCase(Locale.ROOT))) {
                int poke_num = poke_list.indexOf(poke_) + 1;
                Log.d("poke_api_name", String.valueOf(poke_num));
                getPokemonData(poke_num);
                queryPokemon = poke;
                Log.d("poke_api_name", "found: " + poke.getName());
                break;
            } else {
                Log.d("poke_api_name", "Pokemon not found");
            }
        }

        return poke;
    }

    public Pokemon getQueryPokemon() {
        return queryPokemon;
    }

    public void setPokemonMaxResults(int POKEMON_MAX_RESULTS) {
        this.POKEMON_MAX_RESULTS = POKEMON_MAX_RESULTS;
    }

    @Override
    public void addObserver(MyObserver myObserver) {
        Log.d("addObserver", "New observer added");
        this.myObservers.add(myObserver);
    }

    @Override
    public void removeObserver(MyObserver myObserver) {
        this.myObservers.remove(myObserver);
    }

    @Override
    public void notifyObserversPokemonData() {
        for (MyObserver myObserver : myObservers) {
            Log.d("notifyObserversPokemonData", "Notifying observer");
            myObserver.onPokemonDataChanged(poke_list);
        }
    }
}
