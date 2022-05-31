package es.upm.miw.virgolini;
import es.upm.miw.virgolini.models.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IPokemonEndpoint {

    //Request method and URL specified in the annotation
    @GET("pokemon?limit=20")
    Call<PokemonList> getAllPokemon(@Query("limit") int limit,@Query("offset") int offset);
    @GET("pokemon/{name}/")
    Call<PokemonResult> getPokemon(@Path("round") String name);

}