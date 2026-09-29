package es.upm.mssde.pokedex;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.upm.mssde.pokedex.models.PokemonList;
import es.upm.mssde.pokedex.models.PokemonResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PokeAPI implements MyObservable {

    private final Retrofit retrofit;
    public int POKEMON_MAX_RESULTS = 100;
    public final ArrayList<PokemonResult> poke_list;
    private final ArrayList<PokemonResult> allPokemonNames = new ArrayList<>();
    private boolean allNamesLoaded = false;
    private final List<MyObserver> myObservers;

    public PokeAPI() {
        retrofit = PokeApiClient.getRetrofit();
        poke_list = new ArrayList<>();
        myObservers = new ArrayList<>();
    }

    public void getPokemonsData(int offset){
        Log.d("getPokemonData", "offset: " + offset);
        IPokemonEndpoint apiService = retrofit.create(IPokemonEndpoint.class);
        Call<PokemonList> pokemonResultCall = apiService.getAllPokemon(POKEMON_MAX_RESULTS, offset);

        pokemonResultCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PokemonList> call, @NonNull Response<PokemonList> response) {
                if (response.isSuccessful()){
                    PokemonList Pokemons = response.body();
                    Log.d("poke_api_all", response.message());
                    if (Pokemons == null) {
                        Log.w("poke_api_all", "Response body was null for offset " + offset);
                        return;
                    }
                    ArrayList<PokemonResult> Pokemon_list = Pokemons.getResults();
                    poke_list.addAll(Pokemon_list);
                    Log.d("poke_api_all", "poke_list size: " + poke_list.size());
                    notifyObserversPokemonsData();
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonList> call, @NonNull Throwable t) {
                Log.d("poke_api_all", t.toString());
            }
        });
    }

    public void loadAllPokemonNames() {
        loadAllPokemonNames(null);
    }

    public void loadAllPokemonNames(@Nullable Runnable onLoaded) {
        if (allNamesLoaded) {
            if (onLoaded != null) onLoaded.run();
            return;
        }
        IPokemonEndpoint apiService = retrofit.create(IPokemonEndpoint.class);
        Call<PokemonList> call = apiService.getAllPokemon(10000, 0);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PokemonList> call, @NonNull Response<PokemonList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPokemonNames.addAll(response.body().getResults());
                    allNamesLoaded = true;
                    Log.d("poke_api_all_names", "Loaded " + allPokemonNames.size() + " pokemon names");
                    if (onLoaded != null) onLoaded.run();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonList> call, @NonNull Throwable t) {
                Log.d("poke_api_all_names", t.toString());
            }
        });
    }

    public boolean isAllNamesLoaded() {
        return allNamesLoaded;
    }

    public ArrayList<PokemonResult> searchByName(String query) {
        ArrayList<PokemonResult> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        for (PokemonResult p : allPokemonNames) {
            if (p.getName().toLowerCase(Locale.ROOT).contains(lowerQuery)) {
                results.add(p);
            }
        }
        return results;
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
        Log.d("removeObserver", "Observer removed");
        this.myObservers.remove(myObserver);
    }

    @Override
    public void notifyObserversPokemonsData() {
        for (MyObserver myObserver : myObservers) {
            Log.d("notifyObserversPokemonsData", "Notifying observer");
            myObserver.onPokemonsDataChanged(poke_list);
        }
    }
}
