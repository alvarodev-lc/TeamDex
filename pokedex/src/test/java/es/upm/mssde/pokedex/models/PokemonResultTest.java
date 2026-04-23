package es.upm.mssde.pokedex.models;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class PokemonResultTest {

    private PokemonResult pokemonResult;

    @Before
    public void setUp() {
        pokemonResult = new PokemonResult();
    }

    @Test
    public void getName_shouldCapitalizeFirstLetter() {
        pokemonResult.setName("pikachu");
        assertEquals("Pikachu", pokemonResult.getName());
    }

    @Test
    public void getNum_shouldExtractIdFromUrl() {
        pokemonResult.setUrl("https://pokeapi.co/api/v2/pokemon/25/");
        assertEquals(25, pokemonResult.getNum());
    }

    @Test
    public void getNum_shouldReturnNumIfUrlIsNull() {
        pokemonResult.setNum(151);
        pokemonResult.setUrl(null);
        assertEquals(151, pokemonResult.getNum());
    }

    @Test
    public void setAndGetUrl() {
        String url = "https://pokeapi.co/api/v2/pokemon/1/";
        pokemonResult.setUrl(url);
        assertEquals(url, pokemonResult.getUrl());
    }
}