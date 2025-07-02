package es.upm.mssde.pokedex;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.upm.mssde.pokedex.fragment.TeamViewerFragment;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.PokemonTeam;

public class TeamViewerListAdapter extends RecyclerView.Adapter<TeamViewerListAdapter.ViewHolder> {

    public ArrayList<PokemonTeam> teams;
    private final OnTeamClickListener onTeamClickListener;
    private final TeamDatabase teamDatabase;

    public TeamViewerListAdapter(OnTeamClickListener onTeamClickListener) {
        teams = new ArrayList<>();
        this.onTeamClickListener = onTeamClickListener;
        teamDatabase = new TeamDatabase(TeamViewerFragment.appContext);

        teams = teamDatabase.getAllTeams();
    }

    public void updateDB() {
        teams = teamDatabase.getAllTeams();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_item, parent,false);
        return new ViewHolder(view, onTeamClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PokemonTeam pokemonTeam = teams.get(position);
        String teamId = pokemonTeam.getTeamId();

        holder.bindTeam(teamId);

        ArrayList<PokemonResult> team = teamDatabase.getTeam(teamId);
        int team_poke_num = team.size();
        holder.team_name.setText(holder.itemView.getContext().getString(R.string.team_number, teamId));

        for (int i = 0; i <= 5; i++) {
            if (i < team_poke_num) {
                Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + team.get(i).getNum() + ".png").into(holder.poke_images.get(i));
            } else {
                Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/0.png").into(holder.poke_images.get(i));
            }
        }
    }


    @Override
    public int getItemCount() {
        Log.d("poke_api", String.valueOf(teams.size()));
        return teams.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final CardView cardView;
        public final TextView team_name;
        private final ArrayList<ImageView> poke_images = new ArrayList<>();
        private final OnTeamClickListener onTeamClickListener;
        private String teamId;

        public ViewHolder(View v, OnTeamClickListener onTeamClickListener) {
            super(v);
            cardView = v.findViewById(R.id.team_card_view);
            team_name = v.findViewById(R.id.team_name);

            int[] pokeImageIds = {
                    R.id.poke_image0,
                    R.id.poke_image1,
                    R.id.poke_image2,
                    R.id.poke_image3,
                    R.id.poke_image4,
                    R.id.poke_image5
            };

            for (int id : pokeImageIds) {
                ImageView image = v.findViewById(id);
                poke_images.add(image);
            }

            this.onTeamClickListener = onTeamClickListener;
            v.findViewById(R.id.team_card_view).setOnClickListener(this);
        }

        public void bindTeam(String teamId) {
            this.teamId = teamId;
        }

        @Override
        public void onClick(View v) {
            onTeamClickListener.onTeamClick(teamId);
        }
    }


    public interface OnTeamClickListener {
        void onTeamClick(String teamId);
    }
}