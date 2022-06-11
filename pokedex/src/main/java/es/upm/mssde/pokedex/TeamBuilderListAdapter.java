package es.upm.mssde.pokedex;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;

public class TeamBuilderListAdapter extends ArrayAdapter<String> implements MyObserver {
    ArrayList<PokemonResult> poke_list;
    Context context;
    private PokeAPI pokeAPI;

    public TeamBuilderListAdapter(@NonNull Context context) {
        super(context, R.layout.row_list);

        poke_list = new ArrayList<>();
        pokeAPI = new PokeAPI();
        pokeAPI.addObserver(this);
    }

    public TeamBuilderListAdapter(@NonNull Context context, ArrayList<PokemonResult> poke_list) {
        super(context, R.layout.row_list);
        this.poke_list = poke_list;
        this.context = context;

        pokeAPI = new PokeAPI();
        pokeAPI.addObserver(this);
    }

    @Override
    public int getCount() {
        return poke_list.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Viewolder viewHolder = new Viewolder();
        if (convertView == null) {

            LayoutInflater mInflator = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = mInflator.inflate(R.layout.row_list, parent, false);
            viewHolder.name = convertView.findViewById(R.id.poke_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Viewolder) convertView.getTag();
        }
        viewHolder.name.setText(poke_list.get(position).getName());

        return convertView;
    }

    public void getPokemonsData(int num_pokes) {
        Log.d("TEAM_BUILDER_LIST_ADAPTER", "Getting pokemon data");
        pokeAPI.getPokemonsData(num_pokes);
    }

    public void getPokemonData(int position) {
        pokeAPI.getPokemonData(position, false);
    }

    @Override
    public void onPokemonsDataChanged(ArrayList<PokemonResult> poke_list) {
        Log.d("TEAM_BUILDER_LIST_ADAPTER", "Pokemons data recieved");
        this.poke_list = poke_list;
        notifyDataSetChanged();
    }

    @Override
    public void onPokemonDataChanged(Pokemon pokemon) {

    }

    @Override
    public void onPokemonDataFromNameChanged(Pokemon pokemon) {

    }

    static class Viewolder {
        TextView name;
    }
}