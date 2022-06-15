package es.upm.mssde.pokedex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import es.upm.mssde.pokedex.fragment.TeamViewerFragment;
import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.PokemonTeam;

public class TeamViewerListAdapter extends RecyclerView.Adapter<TeamViewerListAdapter.ViewHolder> {

    public ArrayList<PokemonTeam> teams;
    private OnTeamClickListener onTeamClickListener;
    private TeamDatabase teamDatabase;

    public TeamViewerListAdapter(OnTeamClickListener onTeamClickListener) {
        teams = new ArrayList<>();
        this.onTeamClickListener = onTeamClickListener;
        teamDatabase = new TeamDatabase(TeamViewerFragment.appContext);

        teams = teamDatabase.getAllTeams();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_item, parent,false);
        return new ViewHolder(view, onTeamClickListener);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position){
        ArrayList<PokemonResult> team = teamDatabase.getTeam(String.valueOf(position));
        int team_id = position;
        holder.team_name.setText("Team #" + team_id);

        for (int i = 0; i < 6; i++) {
            PokemonResult poke = team.get(i);

            if (poke != null) {
                //holder.poke_name.setText(pokemon.getName());
                Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + poke.getNum() + ".png").into(holder.poke_images.get(i));
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.d("poke_api", String.valueOf(teams.size()));
        return teams.size();
    }

    public ArrayList<PokemonTeam> getAllTeams() {
        return teams;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CardView cardView;
        public TextView team_name;
        private ArrayList<ImageView> poke_images = new ArrayList<>();
        private OnTeamClickListener onTeamClickListener;


        public ViewHolder(View v, OnTeamClickListener onTeamClickListener) {
            super(v);
            cardView = v.findViewById(R.id.poke_card_view);
            team_name = v.findViewById(R.id.poke_team_name);

            for (int i = 0; i < 6; i++) {
                ImageView image = v.findViewById(v.getContext().getResources().getIdentifier("poke_image" + i, "id", v.getContext().getPackageName()));
                poke_images.add(image);
            }

            this.onTeamClickListener = onTeamClickListener;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTeamClickListener.onTeamClick(getAdapterPosition());
        }
    }

    public interface OnTeamClickListener{
        void onTeamClick(int position);
    }
}