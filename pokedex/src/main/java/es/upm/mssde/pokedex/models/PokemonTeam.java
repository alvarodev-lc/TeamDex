package es.upm.mssde.pokedex.models;

import java.util.ArrayList;

public class PokemonTeam {
    ArrayList<PokemonResult> pokes;
    int team_id;

    public PokemonTeam() {
        pokes = new ArrayList<>();
    }

    public ArrayList<PokemonResult> getTeamPokemons() {
        return pokes;
    }

    public void setTeamPokemons(ArrayList<PokemonResult> pokes) {
        this.pokes = pokes;
    }

    public int getTeamId() {
        return team_id;
    }

    public void setTeamId(int team_id) {
        this.team_id = team_id;
    }
}
