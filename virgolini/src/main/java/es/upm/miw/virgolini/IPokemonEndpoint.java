package es.upm.miw.virgolini;
import es.upm.miw.virgolini.models.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IPokemonEndpoint {

    //Request method and URL specified in the annotation
    @GET("pokemon")
    Call<PokemonList> getAllPokemon(@Query("limit") int limit,@Query("offset") int offset);
    @GET("pokemon/{num}")
    Call<Pokemon> getPokemon(@Path("num") String num);
}