package es.upm.mssde.pokedex.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import es.upm.mssde.pokedex.R;
import es.upm.mssde.pokedex.TeamBuilderActivity;
import es.upm.mssde.pokedex.TeamDatabase;
import es.upm.mssde.pokedex.TeamViewerListAdapter;
import es.upm.mssde.pokedex.models.PokemonTeam;

public class TeamViewerFragment extends Fragment implements View.OnClickListener, TeamViewerListAdapter.OnTeamClickListener {

    private TeamDatabase teamDatabase;
    private ArrayList<PokemonTeam> teams;
    private RecyclerView recyclerView;
    private TeamViewerListAdapter teamViewerListAdapter;
    private View view;

    public static Context appContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = View.inflate(getActivity(), R.layout.team_viewer, null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        appContext = getActivity().getApplicationContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.team_builder_recyclerview);
        teamViewerListAdapter = new TeamViewerListAdapter(this);
        recyclerView.setAdapter(teamViewerListAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);

        teamDatabase = new TeamDatabase(getActivity());
        teams = new ArrayList<>();

        loadTeamsFromDB();
        addOnClickListenerToCreateTeamButton();
    }

    private void loadTeamsFromDB() {
        teams = teamDatabase.getAllTeams();

        TextView teamsNotFound = view.findViewById(R.id.teams_not_found);

        if (teams.size() == 0) {
            teamsNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        teams = teamDatabase.getAllTeams();
        TextView teamsNotFound = view.findViewById(R.id.teams_not_found);
        if (teams.size() > 0) {
            teamsNotFound.setVisibility(View.GONE);
        }
        updateTeam();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTeam() {
        teamViewerListAdapter.updateDB();
        teamViewerListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onTeamClick(int position) {
        ArrayList<PokemonTeam> all_teams = teamViewerListAdapter.getAllTeams();
        PokemonTeam clicked_team = all_teams.get(position);


        Log.d("team_click", "clicked team: " + clicked_team.getTeamId());

        Toast.makeText(appContext,
                "Team #" + clicked_team.getTeamId() + " selected" , Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), TeamBuilderActivity.class);
        intent.putExtra("team_id", String.valueOf(position));
        startActivity(intent);
    }

    // add onClickListener to Create Team button
    public void addOnClickListenerToCreateTeamButton() {
        MaterialButton create_team_button = view.findViewById(R.id.create_team_button);

        create_team_button.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TeamBuilderActivity.class);
            startActivity(intent);
        });
    }
}
