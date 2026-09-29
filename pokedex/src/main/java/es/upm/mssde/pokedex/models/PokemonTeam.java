package es.upm.mssde.pokedex.models;

import java.util.ArrayList;

public class PokemonTeam {
    ArrayList<PokemonResult> pokes;
    String team_id;
    String teamName;

    public PokemonTeam() {
        pokes = new ArrayList<>();
    }

    public ArrayList<PokemonResult> getTeamPokemons() {
        return pokes;
    }

    public void setTeamPokemons(ArrayList<PokemonResult> pokes) {
        this.pokes = pokes;
    }

    public String getTeamId() {
        return team_id;
    }

    public void setTeamId(String team_id) {
        this.team_id = team_id;
    }

    /** User-given name, or null when the team should be shown with its default "Team #N" label. */
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
