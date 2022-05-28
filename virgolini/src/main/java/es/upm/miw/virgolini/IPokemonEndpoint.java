package es.upm.miw.virgolini;
import es.upm.miw.virgolini.pojo.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IPokemonEndpoint {

    //Request method and URL specified in the annotation
    @GET("pokemon")
    Call<PokemonList> getAllPokemon();
    @GET("pokemon/{name}/")
    Call<Pokemon> getPokemon(@Path("round") String name);

}