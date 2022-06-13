package es.upm.mssde.pokedex;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.upm.mssde.pokedex.models.PokeDB;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.Type;

public class TeamViewerActivity extends AppCompatActivity {

    private TeamDatabase teamDatabase;
    private ArrayList<PokeDB> pokeDBs;
    private ArrayList<PokemonResult> team;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_viewer);

        teamDatabase = new TeamDatabase(this);
        team = new ArrayList<>();

        loadTeamFromDB();

        TeamAdapter teamAdapter = new TeamAdapter(team);
    }

    private void loadTeamFromDB() {
        pokeDBs = teamDatabase.retrieveTeam();
    }
}
