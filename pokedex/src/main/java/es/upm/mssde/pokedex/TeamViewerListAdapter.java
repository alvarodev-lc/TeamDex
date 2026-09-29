package es.upm.mssde.pokedex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.upm.mssde.pokedex.models.NamedApiResource;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.PokemonTeam;

public class TeamViewerListAdapter extends RecyclerView.Adapter<TeamViewerListAdapter.ViewHolder> {

    public static final int MAX_TEAM_SIZE = 6;

    private final List<PokemonTeam> teams = new ArrayList<>();
    private final OnTeamClickListener onTeamClickListener;

    public TeamViewerListAdapter(OnTeamClickListener onTeamClickListener) {
        this.onTeamClickListener = onTeamClickListener;
    }

    // Replaces the whole list, so there is no finer-grained change to report.
    @SuppressLint("NotifyDataSetChanged")
    public void setTeams(List<PokemonTeam> newTeams) {
        teams.clear();
        teams.addAll(newTeams);
        notifyDataSetChanged();
    }

    public PokemonTeam removeAt(int position) {
        PokemonTeam removed = teams.remove(position);
        notifyItemRemoved(position);
        return removed;
    }

    public void insertAt(int position, PokemonTeam team) {
        teams.add(position, team);
        notifyItemInserted(position);
    }

    public static String displayName(Context context, PokemonTeam team) {
        String name = team.getTeamName();
        return name != null && !name.isEmpty() ? name : context.getString(R.string.team_number, team.getTeamId());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_item, parent, false);
        return new ViewHolder(view, onTeamClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PokemonTeam team = teams.get(position);
        Context context = holder.itemView.getContext();
        List<PokemonResult> members = team.getTeamPokemons();

        holder.teamId = team.getTeamId();
        holder.teamName.setText(displayName(context, team));
        holder.teamCount.setText(context.getString(R.string.team_members_count, members.size()));

        List<String> names = new ArrayList<>();
        for (int i = 0; i < MAX_TEAM_SIZE; i++) {
            ImageView slot = holder.slots.get(i);
            if (i < members.size()) {
                PokemonResult member = members.get(i);
                names.add(NamedApiResource.prettify(member.getName()));
                slot.setAlpha(1f);
                Picasso.get().load(PokeApiClient.spriteUrl(member.getNum())).into(slot);
            } else {
                Picasso.get().cancelRequest(slot);
                slot.setImageDrawable(null);
                slot.setAlpha(0.5f);
            }
        }
        holder.teamMembers.setText(String.join(" · ", names));
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView teamName;
        final TextView teamCount;
        final TextView teamMembers;
        final List<ImageView> slots = new ArrayList<>();
        String teamId;

        public ViewHolder(View v, OnTeamClickListener onTeamClickListener) {
            super(v);
            teamName = v.findViewById(R.id.team_name);
            teamCount = v.findViewById(R.id.team_count);
            teamMembers = v.findViewById(R.id.team_members);

            LinearLayout slotRow = v.findViewById(R.id.team_slots);
            int size = Math.round(48 * v.getResources().getDisplayMetrics().density);
            for (int i = 0; i < MAX_TEAM_SIZE; i++) {
                FrameLayout cell = new FrameLayout(v.getContext());
                ImageView image = new ImageView(v.getContext());
                image.setBackgroundResource(R.drawable.bg_evo_circle);
                // In-game sprites already include transparent margins, so no extra padding.
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                image.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                cell.addView(image, new FrameLayout.LayoutParams(size, size, Gravity.CENTER));
                slotRow.addView(cell, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                slots.add(image);
            }

            v.setOnClickListener(view -> onTeamClickListener.onTeamClick(teamId));
        }
    }

    public interface OnTeamClickListener {
        void onTeamClick(String teamId);
    }
}
