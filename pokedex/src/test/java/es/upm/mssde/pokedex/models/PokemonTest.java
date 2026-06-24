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
    public void getName_shouldReturnEmptyStringForNullName() {
        pokemon.setName(null);
        assertEquals("", pokemon.getName());
    }

    @Test
    public void getName_shouldReturnEmptyStringForEmptyName() {
        pokemon.setName("");
        assertEquals("", pokemon.getName());
    }

    @Test
    public void getHeight_shouldConvertToMeters() {
        pokemon.setHeight("17");
        assertEquals("1.7", pokemon.getHeight());
    }

    @Test
    public void getHeight_shouldReturnEmptyStringForNullHeight() {
        pokemon.setHeight(null);
        assertEquals("", pokemon.getHeight());
    }

    @Test
    public void getWeight_shouldConvertToKilograms() {
        pokemon.setWeight("905");
        assertEquals("90.5", pokemon.getWeight());
    }

    @Test
    public void getWeight_shouldReturnEmptyStringForNullWeight() {
        pokemon.setWeight(null);
        assertEquals("", pokemon.getWeight());
    }
}