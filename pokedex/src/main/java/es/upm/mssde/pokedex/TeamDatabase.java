package es.upm.mssde.pokedex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.PokemonTeam;

public class TeamDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "TEAM";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "team";

    private static final String TEAM_ID_COL = "team_id";

    private static final String NUM_COL = "num";

    private static final String NAME_COL = "name";

    private PokeAPI pokeAPI;

    public TeamDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + TEAM_ID_COL + " INTEGER, "
                + NUM_COL + " INTEGER,"
                + NAME_COL + " TEXT,"
                + "PRIMARY KEY (" + TEAM_ID_COL + "," + NUM_COL + "));";

        db.execSQL(query);

        pokeAPI = new PokeAPI();
    }

    public void addPokemonToTeam(PokemonResult pokemon, String team_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TEAM_ID_COL, team_id);

        String name = pokemon.getName().substring(0, 1).toUpperCase() + pokemon.getName().substring(1);
        values.put(NAME_COL, name);

        int poke_num = pokemon.getNum();
        values.put(NUM_COL, poke_num);

        /*List<TypeList> types = pokemon.getTypes();
        Type type1 = types.get(0).getType();
        Type type2 = types.get(1).getType();

        String type1_capitalized = type1.getName().substring(0, 1).toUpperCase() + type1.getName().substring(1);
        String type2_capitalized = type2.getName().substring(0, 1).toUpperCase() + type2.getName().substring(1);
        values.put(TYPE_COL, type1_capitalized + "/" + type2_capitalized);

        List<AbilityList> abilities = pokemon.getAbilities();
        String ability1 = abilities.get(0).getAbility().getName();
        String ability2 = abilities.get(1).getAbility().getName();

        String ability1_capitalized = ability1.substring(0, 1).toUpperCase() + ability1.substring(1);
        String ability2_capitalized = ability2.substring(0, 1).toUpperCase() + ability2.substring(1);

        values.put(ABILITY_COL, ability1_capitalized + "/" + ability2_capitalized);

        List<Stat> stats = pokemon.getStats();
        int hp = stats.get(0).getBaseStat();
        int def = stats.get(1).getBaseStat();
        int atk = stats.get(2).getBaseStat();
        int spdef = stats.get(3).getBaseStat();
        int spatk = stats.get(4).getBaseStat();
        int speed = stats.get(5).getBaseStat();

        values.put(HP_COL, hp);
        values.put(DEF_COL, def);
        values.put(ATK_COL, atk);
        values.put(SPDEF_COL, spdef);
        values.put(SPATK_COL, spatk);
        values.put(SPEED_COL, speed);
        */

        db.insert(TABLE_NAME, null, values);

        Log.d("DB", "Added pokemon to team in DB: " + name);

        db.close();
    }

    public void delete(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE name ='" + name + "'");
    }

    public ArrayList<PokemonResult> getTeam(String team_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        PokemonTeam team = new PokemonTeam();
        ArrayList<PokemonResult> team_pokemons = new ArrayList<>();

        Log.d("DB", "Getting team from DB: " + team_id);

        String query = "SELECT " + TEAM_ID_COL + ", " + NUM_COL + ", " + NAME_COL + " FROM " + TABLE_NAME + " WHERE " + TEAM_ID_COL + " = " + team_id;
        Cursor cursor = db.rawQuery(query, null);

        int i = 0;
        while (cursor.moveToNext()) {
            Log.d("DB", "Loading pokemon " + i + ": " + cursor.getString(2));
            int col_num = cursor.getColumnIndex(NUM_COL);
            String num = cursor.getString(col_num);

            int colIndex = cursor.getColumnIndex(NAME_COL);
            String name = cursor.getString(colIndex);

            PokemonResult pokemon = new PokemonResult();

            pokemon.setName(name);
            pokemon.setNum(Integer.parseInt(num));

            team_pokemons.add(i, pokemon);

            i++;
        }


        return team_pokemons;
    }

    public ArrayList<PokemonTeam> getAllTeams() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<PokemonTeam> teams = new ArrayList<>();
        ArrayList<PokemonResult> pokemons = new ArrayList<>();

        String query = "SELECT " + TEAM_ID_COL + ", " + NUM_COL + ", " + NAME_COL + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Integer> team_ids = new ArrayList<>();

        int i = 0;
        while (cursor.moveToNext()) {
            int col_num = cursor.getColumnIndex(NUM_COL);
            String num = cursor.getString(col_num);

            int colIndex = cursor.getColumnIndex(NAME_COL);
            String name = cursor.getString(colIndex);

            int col_team_id = cursor.getColumnIndex(TEAM_ID_COL);
            int team_id = cursor.getInt(col_team_id);

            team_ids.add(team_id);

            PokemonResult pokemon = new PokemonResult();

            pokemon.setName(name);
            pokemon.setNum(Integer.parseInt(num));

            pokemons.add(i, pokemon);

            i++;
        }

        // group pokemons by team_id
        HashMap<Integer, ArrayList<PokemonResult>> pokemons_by_team = new HashMap<>();
        int j = 0;
        for (PokemonResult pokemon : pokemons) {
            int team_id = team_ids.get(j);
            if (!pokemons_by_team.containsKey(team_id)) {
                pokemons_by_team.put(team_id, new ArrayList<>());
            }
            pokemons_by_team.get(team_id).add(pokemon);

            j++;
        }

        // convert to ArrayList<PokemonTeam>
        for (int team_id : pokemons_by_team.keySet()) {
            PokemonTeam team = new PokemonTeam();
            team.setTeamPokemons(pokemons_by_team.get(team_id));
            team.setTeamId(String.valueOf(team_id));
            teams.add(team);
        }

        return teams;
    }

    public Pokemon getPokemon(String givenName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String name = givenName.toLowerCase(Locale.ROOT);

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + TABLE_NAME + ".name LIKE " + "'" + name + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();

        int colIndex = cursor.getColumnIndex(NUM_COL);
        int num = cursor.getInt(colIndex);

        pokeAPI.getPokemonData(num, false);
        Pokemon poke = pokeAPI.getQueryPokemon();
        Log.d("DB", "getPokemon: " + poke.getName().toString());

        return poke;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean isEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_NAME;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);

        if (icount == 0) return true;

        return false;
    }

    public void deleteTeam( String team_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE team_id = " + team_id;

        db.execSQL(query);
        Log.d("Erased", "Erased from database");
    }

    public void addTeam(ArrayList<PokemonResult> team, String team_id) {
        for (PokemonResult pokemon : team) {
            addPokemonToTeam(pokemon, team_id);
        }

        Log.d("DB", "Added team to DB");
    }
}