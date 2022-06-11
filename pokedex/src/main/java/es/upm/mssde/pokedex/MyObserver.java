package es.upm.mssde.pokedex;

import java.util.ArrayList;

import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;

public interface MyObserver {
    void onPokemonsDataChanged(ArrayList<PokemonResult> pokemon_list);
    void onPokemonDataChanged(Pokemon pokemon);
    void onPokemonDataFromNameChanged(Pokemon pokemon);
}