package es.upm.miw.virgolini;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.upm.miw.virgolini.models.PokemonResult;

public class PokemonListAdapter extends RecyclerView.Adapter<PokemonListAdapter.ViewHolder>{

    private ArrayList<PokemonResult> data;
    private OnPokemonClickListener onPokemonClickListener;

    public PokemonListAdapter(OnPokemonClickListener onPokemonClickListener) {
        data = new ArrayList<>();
        this.onPokemonClickListener = onPokemonClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_item, parent,false);
        return new ViewHolder(view, onPokemonClickListener);
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

    public void filterList(ArrayList<PokemonResult> filteredList) {
        data = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView poke_image;
        private TextView poke_name;
        private OnPokemonClickListener onPokemonClickListener;


        public ViewHolder(View v, OnPokemonClickListener onPokemonClickListener) {
            super(v);
            poke_image = (ImageView) v.findViewById(R.id.poke_image);
            poke_name = (TextView) v.findViewById(R.id.poke_name);
            this.onPokemonClickListener = onPokemonClickListener;

            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onPokemonClickListener.onPokemonClick(getAdapterPosition());
        }
    }

    public interface OnPokemonClickListener{
        void onPokemonClick(int position);
    }

    public ArrayList<PokemonResult> getData() {
        return data;
    }
}