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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import es.upm.mssde.pokedex.models.PokemonResult;

public class PokemonListAdapter extends RecyclerView.Adapter<PokemonListAdapter.ViewHolder> implements MyObserver {

    public ArrayList<PokemonResult> data;
    private OnPokemonClickListener onPokemonClickListener;
    private PokeAPI pokeAPI;
    public final int POKEMON_MAX_RESULTS = 100;
    int offset = 0;

    public PokemonListAdapter(OnPokemonClickListener onPokemonClickListener) {
        data = new ArrayList<>();
        pokeAPI = new PokeAPI();
        this.onPokemonClickListener = onPokemonClickListener;
        pokeAPI.addObserver(this);
        pokeAPI.setPokemonMaxResults(POKEMON_MAX_RESULTS);
    }

    public void refreshPokemonData() {
        Log.d("POKEMON_LIST_ADAPTER", "Refreshing pokemon data");
        Log.d("POKEMON_LIST_ADAPTER", "Offset: " + offset);
        pokeAPI.getPokemons(offset);
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
        String poke_num_str = "#" + String.format("%03d", poke_num);
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
                Log.e("Error", e.getMessage());
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
        int pixels[] = new int[size];
        //Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int color;
        int r = 0;
        int g = 0;
        int b = 0;
        int a;
        int count = 0;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];
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


    public void addPokemonList(List<PokemonResult> pokemon_list) {
        data.addAll(pokemon_list);
        notifyDataSetChanged();
    }

    public int getPokemonMaxResults() {
        return POKEMON_MAX_RESULTS;
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

    @Override
    public void onPokemonDataChanged(ArrayList<PokemonResult> pokemon_list) {
        Log.d("POKEMON_LIST_ADAPTER", "onPokemonDataChanged");
        data = pokemon_list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CardView cardView;
        public TextView poke_num;
        private ImageView poke_image;
        private TextView poke_name;
        private OnPokemonClickListener onPokemonClickListener;


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