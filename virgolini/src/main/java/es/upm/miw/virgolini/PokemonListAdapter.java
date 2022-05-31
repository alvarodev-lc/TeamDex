package es.upm.miw.virgolini;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.upm.miw.virgolini.models.PokemonResult;

public class PokemonListAdapter extends RecyclerView.Adapter<PokemonListAdapter.ViewHolder>{

    private ArrayList<PokemonResult> data;

    public PokemonListAdapter() {
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position){
        PokemonResult poke = data.get(position);
        holder.poke_name.setText(poke.getName());

        Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + poke.getNum() + ".png")
            .into(holder.poke_image);
    }


    public void addPokemonList(List<PokemonResult> pokemon_list) {
        data.addAll(pokemon_list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        Log.d("poke_api", String.valueOf(data.size()));
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView poke_image;
        private TextView poke_name;

        public ViewHolder(View v) {
            super(v);
            poke_image = (ImageView) v.findViewById(R.id.poke_image);
            poke_name = (TextView) v.findViewById(R.id.poke_name);
        }
    }
}