package es.upm.mssde.pokedex;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.upm.mssde.pokedex.models.Ability;
import es.upm.mssde.pokedex.models.AbilityList;
import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.Species;
import es.upm.mssde.pokedex.models.Stat;
import es.upm.mssde.pokedex.models.StatName;
import es.upm.mssde.pokedex.models.TypeList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonActivity extends AppCompatActivity implements View.OnClickListener {

    private Retrofit retrofit;
    private String LOG_TAG = "pokemon_activity";
    private PokemonResult poke;

    // variable for our bar chart
    private BarChart barChart;

    // variable for our bar data.
    private BarData barData;

    // variable for our bar data set.
    private BarDataSet barDataSet;

    // array list for storing entries.
    ArrayList barEntriesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_stats);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPokemonData() {
        IPokemonEndpoint apiService = retrofit.create(IPokemonEndpoint.class);
        Call<Pokemon> pokemonCall = apiService.getPokemon(String.valueOf(poke.getNum()));

        pokemonCall.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(@NonNull Call<Pokemon> call, @NonNull Response<Pokemon> response) {
                if (response.isSuccessful()) {
                    Pokemon resp = response.body();
                    assert resp != null;

                    TextView poke_name = findViewById(R.id.poke_name);
                    setTextViewText(poke_name, resp.getName());

                    ImageView sprite_view = findViewById(R.id.poke_image);
                    String sprite_url = "https://raw.githubusercontent.com/PokeAPI/sprites/" +
                            "master/sprites/pokemon/" + poke.getNum() + ".png";
                    addImageView(sprite_url, sprite_view);

                    ImageView shiny_sprite_view = findViewById(R.id.shiny_image);
                    String shiny_sprite_url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/" +
                                    "pokemon/shiny/" + poke.getNum() + ".png";
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

                    loadStats(resp);

                }
            }

            @Override
            public void onFailure(@NonNull Call<Pokemon> call, @NonNull Throwable t) {
                Log.d(LOG_TAG, "Error");
                t.printStackTrace();
            }
        });
    }

    private void getSpeciesData() {
        IPokemonEndpoint apiService = retrofit.create(IPokemonEndpoint.class);
        Call<Species> pokemonCall = apiService.getPokemonSpecies(String.valueOf(poke.getNum()));

        pokemonCall.enqueue(new Callback<Species>() {
            @Override
            public void onResponse(@NonNull Call<Species> call, @NonNull Response<Species> response) {
                if (response.isSuccessful()) {
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

    public void loadPokemonTypes(Pokemon pokemon) {
        LinearLayout type_layout = findViewById(R.id.type_layout);
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


            addCardView(type_layout, pokemon_type1_capitalized, -1);
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


            addCardView(type_layout, pokemon_type1_capitalized, -1);
            addCardView(type_layout, pokemon_type2_capitalized, -1);
        }
    }

    public void addCardView(LinearLayout layout, String text, int i) {
        // create round card view and add the ability_name
        CardView cardView = new CardView(this);
        // change width and height of the card
        cardView.setRadius(100);
        cardView.setCardElevation(4);
        cardView.setContentPadding(20, 20, 20, 20);
        //set background color to material color
        cardView.setCardBackgroundColor(Color.parseColor("#FAFAFA"));
        // set margin right in card view
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(25, 0, 5, 0);
        cardView.setLayoutParams(params);
        // add the ability_name to the card view
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        if (i != -1) {
            textView.setTextColor(getMaterialColorSeq(i));
        } else {
            textView.setTextColor(getMaterialColorType(text));
        }
        cardView.addView(textView);
        // add the card view to the layout
        layout.addView(cardView);
    }

    // function that given a type of pokemon, returns a material design color
    public int getMaterialColorType(String typeColor) {
        Log.d("COLOR_REQUEST_POKE_TYPE", "Type: " + typeColor);
        HashMap<String, String> matColor = new HashMap<>();
        matColor.put("Grass", "#7a8a06");
        matColor.put("Poison", "#A33EA1");
        matColor.put("Fire", "#e6653e");
        matColor.put("Water", "#6390F0");
        matColor.put("Bug", "#A6B91A");
        matColor.put("Normal", "#A8A77A");
        matColor.put("Electric", "#F7D02C");
        matColor.put("Ground", "#D9A664");
        matColor.put("Fairy", "#F4B1F4");
        matColor.put("Fighting", "#C22E28");
        matColor.put("Psychic", "#F95587");
        matColor.put("Rock", "#B6A136");
        matColor.put("Ice", "#96D9D6");
        matColor.put("Ghost", "#735797");
        matColor.put("Dragon", "#6F35FC");
        matColor.put("Dark", "#705746");
        matColor.put("Steel", "#B7B7CE");
        matColor.put("Flying", "#d0c0fc");

        return Color.parseColor(matColor.get(typeColor));
    }

    private int getMaterialColorSeq(int index)
    {
        HashMap<Integer, Integer> matColor = new HashMap<>();
        matColor.put(0, Color.parseColor("#388baf"));
        matColor.put(1, Color.parseColor("#5f3061"));
        matColor.put(2, Color.parseColor("#ef4d46"));
        matColor.put(3, Color.parseColor("#f4e7e3"));

        return matColor.get(index);
    }

    public void loadPokemonAbilities(Pokemon pokemon) {
        LinearLayout ability_layout = findViewById(R.id.ability_layout);

        List<AbilityList> list_ability_list = pokemon.getAbilities();

        int i = 0;

        for (AbilityList ability_list : list_ability_list) {
            Ability ability = ability_list.getAbility();
            String ability_name = ability.getName();
            ability_name = ability_name.substring(0, 1).toUpperCase() +
                    ability_name.substring(1);
            Log.d(LOG_TAG, "Ability: " + ability_name);

            addCardView(ability_layout, ability_name, i);

            i++;
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
                String pokemon_description = description.get(1).text().replaceAll("\"", "");

                Log.d("Scrapping", "Summary: " + pokemon_summary);
                Log.d("Scrapping", "Description: " + pokemon_description);

                runOnUiThread(() -> {

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

    public void loadStats(Pokemon pokemon) {
        List<Stat> list_stat_list = pokemon.getStats();
        // initializing variable for bar chart.
        barChart = findViewById(R.id.stats_bar_chart);

        barEntriesArrayList = new ArrayList<>();

        String[] stat_names = new String[list_stat_list.size()];

        // create hashmap for stat names and values
        HashMap<String, String> stat_hashmap = new HashMap<>();
        stat_hashmap.put("hp", "HP");
        stat_hashmap.put("attack", "Atk");
        stat_hashmap.put("defense", "Def");
        stat_hashmap.put("special-attack", "SpA");
        stat_hashmap.put("special-defense", "SpD");
        stat_hashmap.put("speed", "Spe");

        HashMap<String, Integer> stats_order_hashmap = new HashMap<>();
        stats_order_hashmap.put("hp", 0);
        stats_order_hashmap.put("attack", 1);
        stats_order_hashmap.put("defense", 2);
        stats_order_hashmap.put("special-attack", 3);
        stats_order_hashmap.put("special-defense", 4);
        stats_order_hashmap.put("speed", 5);

        //reorder list of stats based on order of stats_order_hashmap
        List<Stat> ordered_list_stat_list = new ArrayList<>();
        for (Stat stat_value : list_stat_list) {
            StatName stat = stat_value.getStat();
            String stat_name = stat.getName();
            Log.d("reorder", "Stat name: " + stat_name + "Position: " + stats_order_hashmap.get(stat.getName()));
            ordered_list_stat_list.add(stats_order_hashmap.get(stat_name), stat_value);
        }

        for (Stat stat_value : ordered_list_stat_list) {
            int index = ordered_list_stat_list.indexOf(stat_value);
            StatName stat = stat_value.getStat();
            String stat_name = stat_hashmap.get(stat.getName());

            Integer stat_base_stat = stat_value.getBaseStat();
            Log.d("poke_stats", "Index: " + index + " Stat: " + stat_name + " - " + stat_base_stat);

            // adding new entry to our array list with bar
            // entry and passing x and y axis value to it.
            barEntriesArrayList.add(new BarEntry(index, stat_base_stat));

            stat_names[index] = stat_name;
        }

        // creating a new bar data set.
        barDataSet = new BarDataSet(barEntriesArrayList, "Stats");

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // set the colors of the bars
        int[] colors = new int[]{
                Color.rgb(239, 71, 111),
                Color.rgb(237, 142, 80),
                Color.rgb(255, 209, 82),
                Color.rgb(6, 204, 160),
                Color.rgb(17, 155, 198),
                Color.rgb(164, 164, 255),
        };

        barDataSet.setColors(colors);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(25f);

        // set column label
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(stat_names));

        barChart.setNoDataText("Loading stats...");

        int max = list_stat_list.size();
        barChart.getAxisLeft().setLabelCount(max);

        // disable grid lines
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        // remove legend
        barChart.getLegend().setEnabled(false);

        // remove description
        barChart.getDescription().setEnabled(false);

        // make it non interactive
        barChart.setTouchEnabled(false);
        barChart.setPinchZoom(false);

        // put y values at the right of each bar
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);

        barDataSet.setDrawValues(true);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);

        YAxis left = barChart.getAxisLeft();
        left.setAxisMinimum(0f);

        // format y values as integers
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        // add extra space to the left of the chart
        barChart.setExtraLeftOffset(10f);
        barChart.setExtraRightOffset(20f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // set bar width
        barData.setBarWidth(0.6f);

        // increase x axis label size
        xAxis.setTextSize(13f);

        CircleHorizontalBarChartRenderer renderer = new CircleHorizontalBarChartRenderer(barChart, barChart.getAnimator(), barChart.getViewPortHandler());
        renderer.initBuffers();
        barChart.setRenderer(renderer);

        barChart.invalidate();
        barChart.refreshDrawableState();
    }

    public void addTextViewToLayout(LinearLayout layout, String name, int text_size, int left_margin, int top_margin) {
        TextView type_view = new TextView(PokemonActivity.this);
        type_view.setText(name);
        type_view.setTextSize(text_size);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        lp.setMargins(left_margin, top_margin, 7, 0);
        type_view.setLayoutParams(lp);
        layout.addView(type_view);
    }

    public void addImageView(String url, ImageView sprite_view) {
        Picasso.get().load(url).into(sprite_view);
    }

    public void setTextViewText(TextView view, String text) {
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