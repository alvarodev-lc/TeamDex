package es.upm.miw.virgolini;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.upm.miw.virgolini.models.Pokemon;

public class PokemonListAdapter extends RecyclerView.Adapter<PokemonListAdapter.ViewHolder>{

    private ArrayList<Pokemon> data;

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
        Pokemon poke = data.get(position);
        holder.poke_name.setText(poke.getName());
    }

    @Override
    public int getItemCount(){
        return 0;
    }

    public void addPokemonList(List<Pokemon> pokemon_list) {
        data.addAll(pokemon_list);
        Log.d("list_poke", data.toString());
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