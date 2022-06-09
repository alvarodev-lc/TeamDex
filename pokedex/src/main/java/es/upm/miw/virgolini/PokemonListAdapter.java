package es.upm.miw.virgolini;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import androidx.cardview.widget.CardView;
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
                Log.d("POKEMON_LIST_ADAPTER", "color: " + mutedColor);
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
        public CardView cardView;
        private ImageView poke_image;
        private TextView poke_name;
        private OnPokemonClickListener onPokemonClickListener;


        public ViewHolder(View v, OnPokemonClickListener onPokemonClickListener) {
            super(v);
            cardView = v.findViewById(R.id.poke_card_view);
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