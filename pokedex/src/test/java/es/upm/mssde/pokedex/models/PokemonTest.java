package es.upm.mssde.pokedex.models;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class PokemonTest {

    private Pokemon pokemon;

    @Before
    public void setUp() {
        pokemon = new Pokemon();
    }

    @Test
    public void getName_shouldCapitalizeFirstLetter() {
        pokemon.setName("charizard");
        assertEquals("Charizard", pokemon.getName());
    }

    @Test
    public void getHeight_shouldConvertToMeters() {
        // La API devuelve 17 decímetros para Charizard
        pokemon.setHeight("17");
        assertEquals("1.7", pokemon.getHeight());
    }

    @Test
    public void getWeight_shouldConvertToKilograms() {
        // La API devuelve 905 hectogramos para Charizard
        pokemon.setWeight("905");
        assertEquals("90.5", pokemon.getWeight());
    }

    @Test
    public void setAndGetNum() {
        pokemon.setNum("6");
        assertEquals("6", pokemon.getNum());
    }
}