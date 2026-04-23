package es.upm.mssde.pokedex.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class PokemonTeamTest {

    private PokemonTeam pokemonTeam;

    @Before
    public void setUp() {
        pokemonTeam = new PokemonTeam();
    }

    @Test
    public void constructor_shouldInitializeEmptyList() {
        assertNotNull(pokemonTeam.getTeamPokemons());
        assertTrue(pokemonTeam.getTeamPokemons().isEmpty());
    }

    @Test
    public void setAndGetTeamPokemons() {
        ArrayList<PokemonResult> pokes = new ArrayList<>();
        PokemonResult p1 = new PokemonResult();
        p1.setName("bulbasaur");
        pokes.add(p1);

        pokemonTeam.setTeamPokemons(pokes);
        assertEquals(1, pokemonTeam.getTeamPokemons().size());
        assertEquals("Bulbasaur", pokemonTeam.getTeamPokemons().get(0).getName());
    }

    @Test
    public void setAndGetTeamId() {
        String id = "team_123";
        pokemonTeam.setTeamId(id);
        assertEquals(id, pokemonTeam.getTeamId());
    }
}