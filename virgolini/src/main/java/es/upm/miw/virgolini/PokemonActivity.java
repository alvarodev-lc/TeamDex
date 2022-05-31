package es.upm.miw.virgolini;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import es.upm.miw.virgolini.models.Pokemon;
import es.upm.miw.virgolini.models.PokemonResult;
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
    private Pokemon pokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_stats);

        Intent intent = getIntent();
        PokemonResult pokemon_result = (PokemonResult) intent.getExtras().getSerializable("pokemon");

        poke = pokemon_result;

        TextView poke_name = (TextView) findViewById(R.id.poke_name);
        poke_name.setText(pokemon_result.getName());

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
                    pokemon = resp;
                    assert resp != null;
                    LinearLayout type_layout =  findViewById(R.id.type_layout);

                    List<TypeList> list_type_list = resp.getTypes();
                    for(TypeList tipo_lista: list_type_list){
                        Type type = tipo_lista.getType();
                        String type_name = type.getName();
                        type_name = type_name.substring(0, 1).toUpperCase() +
                                type_name.substring(1);
                        Log.d(LOG_TAG, type_name);

                        TextView type_view = new TextView(PokemonActivity.this);
                        type_view.setText(type_name);
                        LinearLayout.LayoutParams lp =
                                new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        lp.setMargins(10,52,7,0);
                        type_view.setLayoutParams(lp);
                        type_layout.addView(type_view);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Pokemon> call, @NonNull Throwable t) {
                Log.d(LOG_TAG, "Error");
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View v) {

    }
}