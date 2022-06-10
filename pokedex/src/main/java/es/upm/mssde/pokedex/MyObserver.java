package es.upm.mssde.pokedex;

import java.util.ArrayList;

import es.upm.mssde.pokedex.models.PokemonResult;

public interface MyObserver {
    void onPokemonDataChanged(ArrayList<PokemonResult> pokemon_list);
}