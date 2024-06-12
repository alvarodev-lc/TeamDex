package es.upm.mssde.pokedex.fragment;

import android.content.Context;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import es.upm.mssde.pokedex.R;
import es.upm.mssde.pokedex.TeamBuilderActivity;
import es.upm.mssde.pokedex.TeamViewerListAdapter;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.PokemonTeam;

public class TeamViewerFragment extends Fragment implements View.OnClickListener, TeamViewerListAdapter.OnTeamClickListener {

    private RecyclerView recyclerView;
    private TeamViewerListAdapter teamViewerListAdapter;
    private View view;

    public static Context appContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.team_viewer, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appContext = requireContext().getApplicationContext();

        recyclerView = view.findViewById(R.id.team_builder_recyclerview);
        teamViewerListAdapter = new TeamViewerListAdapter(this);
        recyclerView.setAdapter(teamViewerListAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);

        addOnClickListenerToCreateTeamButton();

        for (PokemonTeam team : teamViewerListAdapter.teams) {
            ArrayList<PokemonResult> poke_team = team.getTeamPokemons();
            Log.d("final_de", "Team id: " + team.getTeamId());
            for (PokemonResult poke : poke_team) {
                Log.d("final_de", poke.getName());
            }
        }

        TextView teamsNotFound = view.findViewById(R.id.teams_not_found);

        if (teamViewerListAdapter.teams.isEmpty()) {
            teamsNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        teamViewerListAdapter.updateDB();
        TextView teamsNotFound = view.findViewById(R.id.teams_not_found);

        if (!teamViewerListAdapter.teams.isEmpty()) {
            teamsNotFound.setVisibility(View.GONE);
        } else {
            teamsNotFound.setVisibility(View.VISIBLE);
        }

        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(teamViewerListAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View view) {
        // Handle view click events
    }

    @Override
    public void onTeamClick(String teamId) {
        // Handle the click event with the team ID
        // For example, start a new activity or fragment with the team details
        Intent intent = new Intent(getActivity(), TeamBuilderActivity.class);
        intent.putExtra("team_id", teamId);
        startActivity(intent);
    }

    // Add onClickListener to Create Team button
    public void addOnClickListenerToCreateTeamButton() {
        MaterialButton create_team_button = view.findViewById(R.id.create_team_button);

        create_team_button.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TeamBuilderActivity.class);
            startActivity(intent);
        });
    }
}
