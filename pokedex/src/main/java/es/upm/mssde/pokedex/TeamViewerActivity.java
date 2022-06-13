package es.upm.mssde.pokedex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.upm.mssde.pokedex.models.PokeDB;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.PokemonTeam;
import es.upm.mssde.pokedex.models.Type;

public class TeamViewerActivity extends AppCompatActivity implements View.OnClickListener, TeamViewerListAdapter.OnTeamClickListener {

    private TeamDatabase teamDatabase;
    private ArrayList<PokemonTeam> teams;
    private RecyclerView recyclerView;
    private TeamViewerListAdapter teamViewerListAdapter;

    public static Context appContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = this.getApplicationContext();

        setContentView(R.layout.team_viewer);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        teamViewerListAdapter = new TeamViewerListAdapter(this);
        recyclerView.setAdapter(teamViewerListAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        teamDatabase = new TeamDatabase(this);
        teams = new ArrayList<>();

        loadTeamsFromDB();

        TeamViewerListAdapter teamViewerListAdapter = new TeamViewerListAdapter(this);
    }

    private void loadTeamsFromDB() {
        teams = teamDatabase.getAllTeams();

        TextView teamsNotFound = findViewById(R.id.teams_not_found);

        if (teams.size() == 0) {
            teamsNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onTeamClick(int position) {
        ArrayList<PokemonTeam> all_teams = teamViewerListAdapter.getAllTeams();
        PokemonTeam clicked_team = all_teams.get(position);


        Log.d("team_click", "clicked team: " + clicked_team.getTeamId());

        Toast.makeText(TeamViewerActivity.this,
                "Team #" + clicked_team.getTeamId() + " selected" , Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, TeamBuilderActivity.class);
        intent.putExtra("team_id", position);
        startActivity(intent);
    }
}
