package es.upm.mssde.pokedex;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class PokeApiClient {

    public static final String BASE_URL = "https://pokeapi.co/api/v2/";

    private static final String SPRITE_BASE_URL =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";

    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private PokeApiClient() {
    }

    public static Retrofit getRetrofit() {
        return RETROFIT;
    }

    public static String spriteUrl(int pokeNum) {
        return SPRITE_BASE_URL + pokeNum + ".png";
    }

    public static String shinySpriteUrl(int pokeNum) {
        return SPRITE_BASE_URL + "shiny/" + pokeNum + ".png";
    }

    public static String artworkUrl(int pokeNum) {
        return SPRITE_BASE_URL + "other/official-artwork/" + pokeNum + ".png";
    }

    public static String shinyArtworkUrl(int pokeNum) {
        return SPRITE_BASE_URL + "other/official-artwork/shiny/" + pokeNum + ".png";
    }
}
