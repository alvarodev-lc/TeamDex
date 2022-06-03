package es.upm.miw.virgolini;

import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.upm.miw.virgolini.models.Ability;
import es.upm.miw.virgolini.models.AbilityList;
import es.upm.miw.virgolini.models.Pokemon;
import es.upm.miw.virgolini.models.PokemonResult;
import es.upm.miw.virgolini.models.Species;
import es.upm.miw.virgolini.models.Type;
import es.upm.miw.virgolini.models.TypeList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonActivity extends AppCompatActivity implements View.OnClickListener {

    private Retrofit retrofit;
    private String LOG_TAG = "pokemon_activity";
    private PokemonResult poke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_stats);

        Intent intent = getIntent();
        PokemonResult pokemon_result = (PokemonResult) intent.getExtras().getSerializable("pokemon");

        poke = pokemon_result;

        String base_url = "https://pokeapi.co/api/v2/";

        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getPokemonData();
        }

    private void getPokemonData(){
        IPokemonEndpoint apiService = retrofit.create(IPokemonEndpoint.class);
        Call<Pokemon> pokemonCall = apiService.getPokemon(String.valueOf(poke.getNum()));

        pokemonCall.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(@NonNull Call<Pokemon> call, @NonNull Response<Pokemon> response) {
                if (response.isSuccessful()){
                    Pokemon resp = response.body();
                    assert resp != null;

                    TextView poke_name = findViewById(R.id.poke_name);
                    setTextViewText(poke_name, resp.getName());

                    ImageView sprite_view = findViewById(R.id.poke_image);
                    String sprite_url = "https://raw.githubusercontent.com/PokeAPI/sprites/" +
                            "master/sprites/pokemon/"+ poke.getNum() +".png";
                    addImageView(sprite_url, sprite_view);

                    ImageView shiny_sprite_view = findViewById(R.id.shiny_image);
                    String shiny_sprite_url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/" +
                                    "pokemon/shiny/" + poke.getNum() +".png";
                    addImageView(shiny_sprite_url, shiny_sprite_view);

                    loadPokemonTypes(resp);
                    loadPokemonAbilities(resp);

                    try {
                        scrapPokemonPokedexInfo(resp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    TextView poke_exp = findViewById(R.id.poke_exp);
                    setTextViewText(poke_exp, resp.getExperience() + " xp");
                    TextView poke_height = findViewById(R.id.height);
                    setTextViewText(poke_height, resp.getHeight() + " m");
                    TextView poke_weight = findViewById(R.id.weight);
                    setTextViewText(poke_weight, resp.getWeight() + " kg");

                    getSpeciesData();

                }
            }
            @Override
            public void onFailure(@NonNull Call<Pokemon> call, @NonNull Throwable t) {
                Log.d(LOG_TAG, "Error");
                t.printStackTrace();
            }
        });
    }

    private void getSpeciesData(){
        IPokemonEndpoint apiService = retrofit.create(IPokemonEndpoint.class);
        Call<Species> pokemonCall = apiService.getPokemonSpecies(String.valueOf(poke.getNum()));

        pokemonCall.enqueue(new Callback<Species>() {
            @Override
            public void onResponse(@NonNull Call<Species> call, @NonNull Response<Species> response) {
                if (response.isSuccessful()){
                    Species resp = response.body();
                    assert resp != null;
                    Log.d(LOG_TAG, "Capture rate: " + String.valueOf(resp.getCaptureRate()));

                    TextView poke_capt_rate = findViewById(R.id.poke_capt_rate);
                    setTextViewText(poke_capt_rate, resp.getCaptureRate() + " %");

                    TextView poke_happiness = findViewById(R.id.poke_happiness);
                    setTextViewText(poke_happiness, String.valueOf(resp.getBaseHappiness()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<Species> call, @NonNull Throwable t) {
                Log.d(LOG_TAG, "Error");
                t.printStackTrace();
            }
        });
    }
    public void loadPokemonTypes(Pokemon pokemon){
        LinearLayout type_layout =  findViewById(R.id.type_layout);

        List<TypeList> list_type_list = pokemon.getTypes();

        Integer type_list_size = list_type_list.size();

        if (type_list_size == 1) {
            String pokemon_type1 = list_type_list.get(0).getType().getName();

            // Capitalize first letter of each word
            String[] words = pokemon_type1.split(" ");
            StringBuilder sb = new StringBuilder();

            for (String s : words) {
                sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
            }

            String pokemon_type1_capitalized = sb.toString().trim();

            Log.d(LOG_TAG, "Type 1: " + pokemon_type1);

            String pokemon_type = pokemon_type1_capitalized;
            addTextViewToLayout(type_layout, pokemon_type, 20, 25, 25);
        } else if (type_list_size == 2) {
            String pokemon_type1 = list_type_list.get(0).getType().getName();
            String pokemon_type2 = list_type_list.get(1).getType().getName();

            // Capitalize first letter of each word
            String[] words = pokemon_type1.split(" ");
            StringBuilder sb = new StringBuilder();

            for (String s : words) {
                sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
            }

            String pokemon_type1_capitalized = sb.toString().trim();

            String[] words2 = pokemon_type2.split(" ");
            StringBuilder sb2 = new StringBuilder();

            for (String s : words2) {
                sb2.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
            }

            String pokemon_type2_capitalized = sb2.toString().trim();

            Log.d(LOG_TAG, "Type 1: " + pokemon_type1);
            Log.d(LOG_TAG, "Type 2: " + pokemon_type2);

            String pokemon_type = pokemon_type1_capitalized + "/" + pokemon_type2_capitalized;

            addTextViewToLayout(type_layout, pokemon_type, 20, 25, 25);
        }
    }

    public void loadPokemonAbilities(Pokemon pokemon){
        LinearLayout ability_layout =  findViewById(R.id.ability_layout);

        List<AbilityList> list_ability_list = pokemon.getAbilities();
        for(AbilityList ability_list: list_ability_list){
            Ability ability = ability_list.getAbility();
            String ability_name = ability.getName();
            ability_name = ability_name.substring(0, 1).toUpperCase() +
                    ability_name.substring(1);
            Log.d(LOG_TAG, "Ability: " + ability_name);

            addTextViewToLayout(ability_layout, "- " + ability_name, 20, 50, 25);
        }
    }

    public void scrapPokemonPokedexInfo(Pokemon pokemon) throws IOException {
        LinearLayout pokedex_description_layout = findViewById(R.id.pokedex_desc_layout);
        TextView pokedex_summary = findViewById(R.id.pokedex_summary);
        TextView pokedex_description = findViewById(R.id.pokedex_description);
        TextView pokedex_summary_placeholder = findViewById(R.id.pokedex_summary_placeholder);
        TextView pokedex_description_placeholder = findViewById(R.id.pokedex_description_placeholder);

        Integer pokemon_num = poke.getNum();

        Log.d("Scrapping", "Pokemon num: " + pokemon_num);

        String url = "https://pokemon.gameinfo.io/en/pokemon/" + pokemon_num.toString() + "-" + poke.getName().toLowerCase();

        Log.d("Scrapping", "URL: " + url);

        new Thread(() -> {
            try {
                Document document = Jsoup.connect(url).get();
                Elements description = document.body().select("p.description");
                String pokemon_summary = description.get(0).text();
                String pokemon_description = description.get(1).text();

                Log.d("Scrapping", "Summary: " + pokemon_summary);
                Log.d("Scrapping", "About: " + pokemon_description);

                runOnUiThread(() -> {
                    //addTextViewToLayout(pokedex_desc_placeholder_layout, monster_species, 20, 60, 25);
                    //addTextViewToLayout(pokedex_desc_placeholder_layout, monster_description, 17, 60, 40);

                    pokedex_summary_placeholder.setText(pokemon_summary);
                    // underline monster_species
                    pokedex_summary.setPaintFlags(pokedex_summary.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                    pokedex_description_placeholder.setText(pokemon_description);
                    pokedex_description.setPaintFlags(pokedex_summary.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                    // dynamically adjust the height of the description based on the number of characters
                    int number_of_lines = pokemon_description.length() / 30;
                    if (number_of_lines > 1) {
                        pokedex_description_layout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                    }

                    // move the layout to the top
                    pokedex_description_layout.setY(50);


                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addTextViewToLayout(LinearLayout layout, String name, int text_size, int left_margin, int top_margin){
        TextView type_view = new TextView(PokemonActivity.this);
        type_view.setText(name);
        type_view.setTextSize(text_size);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        lp.setMargins(left_margin,top_margin,7,0);
        type_view.setLayoutParams(lp);
        layout.addView(type_view);
    }

    public void addImageView(String url, ImageView sprite_view){
        Picasso.get().load(url).into(sprite_view);
    }

    public void setTextViewText(TextView view, String text){
        view.setText(text);
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View v) {

    }
}