package es.upm.mssde.pokedex;

import android.graphics.Bitmap;
import android.graphics.Color;
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
import java.util.Locale;
import java.util.Objects;

import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;

public class PokedexListAdapter extends RecyclerView.Adapter<PokedexListAdapter.ViewHolder> implements MyObserver {

    public ArrayList<PokemonResult> data;
    public ArrayList<PokemonResult> unfilteredData;
    private final OnPokemonClickListener onPokemonClickListener;
    private final PokeAPI pokeAPI;
    public final int POKEMON_MAX_RESULTS = 100;
    int offset = 0;

    public PokedexListAdapter(OnPokemonClickListener onPokemonClickListener) {
        data = new ArrayList<>();
        unfilteredData = new ArrayList<>();
        pokeAPI = new PokeAPI();
        this.onPokemonClickListener = onPokemonClickListener;
        pokeAPI.addObserver(this);
        pokeAPI.setPokemonMaxResults(POKEMON_MAX_RESULTS);
    }

    public void unfilter() {
        if (!unfilteredData.isEmpty()) {
            Log.d("PokedexListAdapter", "unfilteredData.size() > 0");
            int previousSize = data.size();
            data.clear();
            data.addAll(unfilteredData);
            notifyItemRangeInserted(previousSize, unfilteredData.size());
        }
    }

    public void refreshPokemonData() {
        Log.d("POKEMON_LIST_ADAPTER", "Refreshing pokemon data");
        Log.d("POKEMON_LIST_ADAPTER", "Offset: " + offset);
        pokeAPI.getPokemonsData(offset);
        offset += POKEMON_MAX_RESULTS;
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
        int poke_num = poke.getNum();
        // fill with zeros until the number is 3 digits
        String poke_num_str = "#" + String.format(Locale.getDefault(), "%03d", poke_num);
        holder.poke_num.setText(poke_num_str);

        Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + poke.getNum() + ".png")
            .into(holder.poke_image);

        new Thread(() -> {
            try {
                // set cardview background color to the dominant color of the image
                Bitmap bitmap = Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + poke.getNum() + ".png").get();
                int color = getDominantColor(bitmap);
                // calculate a muted version of the color
                int mutedColor = Color.argb(
                        (int) (Color.alpha(color) * 0.9),
                        (int) (Color.red(color) * 1.1 + 20),
                        (int) (Color.green(color) * 1.1 + 20),
                        (int) (Color.blue(color) * 1.1 + 20)
                );
                // Log.d("POKEMON_LIST_ADAPTER", "color: " + mutedColor);
                holder.cardView.setCardBackgroundColor(mutedColor);
            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
        }).start();
    }

    public static int getDominantColor(Bitmap bitmap) {
        if (bitmap == null) {
            return Color.TRANSPARENT;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        //Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int color;
        int r = 0;
        int g = 0;
        int b = 0;
        int a;
        int count = 0;
        for (int pixel : pixels) {
            color = pixel;
            a = Color.alpha(color);
            if (a > 0) {
                r += Color.red(color);
                g += Color.green(color);
                b += Color.blue(color);
                count++;
            }
        }
        r /= count;
        g /= count;
        b /= count;
        r = (r << 16) & 0x00FF0000;
        g = (g << 8) & 0x0000FF00;
        b = b & 0x000000FF;
        color = 0xFF000000 | r | g | b;
        return color;
    }

    @Override
    public int getItemCount() {
        Log.d("poke_api", String.valueOf(data.size()));
        return data.size();
    }

    public void filterList(ArrayList<PokemonResult> filteredList) {
        int previousSize = data.size();
        data.clear();
        data.addAll(filteredList);
        if (previousSize > 0) {
            notifyItemRangeRemoved(0, previousSize);
        }
        notifyItemRangeInserted(0, filteredList.size());
    }


    @Override
    public void onPokemonsDataChanged(ArrayList<PokemonResult> pokemon_list) {
        int previousSize = data.size();
        data.clear();
        data.addAll(pokemon_list);
        unfilteredData.clear();
        unfilteredData.addAll(pokemon_list);
        if (previousSize > 0) {
            notifyItemRangeRemoved(0, previousSize);
        }
        notifyItemRangeInserted(0, pokemon_list.size());
        Log.d("POKEMON_LIST_ADAPTER", "onPokemonDataChanged");
    }


    @Override
    public void onPokemonDataChanged(Pokemon pokemon) {

    }

    @Override
    public void onPokemonDataFromNameChanged(Pokemon pokemon) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CardView cardView;
        public TextView poke_num;
        private final ImageView poke_image;
        private final TextView poke_name;
        private final OnPokemonClickListener onPokemonClickListener;


        public ViewHolder(View v, OnPokemonClickListener onPokemonClickListener) {
            super(v);
            cardView = v.findViewById(R.id.poke_card_view);
            poke_num = v.findViewById(R.id.poke_num);
            poke_image = v.findViewById(R.id.poke_image);
            poke_name = v.findViewById(R.id.poke_name);
            this.onPokemonClickListener = onPokemonClickListener;

            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onPokemonClickListener.onPokemonClick(getBindingAdapterPosition());
        }
    }

    public interface OnPokemonClickListener{
        void onPokemonClick(int position);
    }

    public ArrayList<PokemonResult> getData() {
        return data;
    }

    public ArrayList<PokemonResult> getUnfilteredData() {
        return unfilteredData;
    }
}