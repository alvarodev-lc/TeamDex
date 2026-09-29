package es.upm.mssde.pokedex.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.upm.mssde.pokedex.R;
import es.upm.mssde.pokedex.TeamBuilderActivity;
import es.upm.mssde.pokedex.TeamDatabase;
import es.upm.mssde.pokedex.TeamViewerListAdapter;
import es.upm.mssde.pokedex.models.PokemonTeam;

public class TeamViewerFragment extends Fragment implements TeamViewerListAdapter.OnTeamClickListener {

    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private TeamDatabase teamDatabase;
    private TeamViewerListAdapter adapter;
    private ExtendedFloatingActionButton createButton;
    private TextView countView;
    private View emptyView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.team_viewer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        teamDatabase = new TeamDatabase(requireContext().getApplicationContext());

        countView = view.findViewById(R.id.teams_count);
        emptyView = view.findViewById(R.id.teams_empty);
        createButton = view.findViewById(R.id.create_team_button);
        createButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), TeamBuilderActivity.class)));

        RecyclerView recyclerView = view.findViewById(R.id.team_builder_recyclerview);
        adapter = new TeamViewerListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (dy > 0) {
                    createButton.shrink();
                } else if (dy < 0) {
                    createButton.extend();
                }
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteTeamAt(viewHolder.getBindingAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTeams();
    }

    @Override
    public void onDestroy() {
        dbExecutor.shutdown();
        if (teamDatabase != null) {
            teamDatabase.close();
        }
        super.onDestroy();
    }

    private void loadTeams() {
        dbExecutor.execute(() -> {
            List<PokemonTeam> teams = teamDatabase.getAllTeams();
            mainHandler.post(() -> {
                if (getView() == null) {
                    return;
                }
                adapter.setTeams(teams);
                updateSummary();
            });
        });
    }

    private void updateSummary() {
        int count = adapter.getItemCount();
        countView.setText(getResources().getQuantityString(R.plurals.teams_count, count, count));
        countView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(count > 0 ? View.GONE : View.VISIBLE);
    }

    private void deleteTeamAt(int position) {
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        PokemonTeam team = adapter.removeAt(position);
        updateSummary();
        dbExecutor.execute(() -> teamDatabase.deleteTeam(team.getTeamId()));

        Snackbar.make(requireView(), R.string.team_deleted, Snackbar.LENGTH_LONG)
                .setAnchorView(createButton)
                .setAction(R.string.undo, v -> {
                    adapter.insertAt(Math.min(position, adapter.getItemCount()), team);
                    updateSummary();
                    dbExecutor.execute(() -> teamDatabase.saveTeam(
                            team.getTeamPokemons(), team.getTeamId(), team.getTeamName()));
                })
                .show();
    }

    @Override
    public void onTeamClick(String teamId) {
        Intent intent = new Intent(getActivity(), TeamBuilderActivity.class);
        intent.putExtra("team_id", teamId);
        startActivity(intent);
    }
}
