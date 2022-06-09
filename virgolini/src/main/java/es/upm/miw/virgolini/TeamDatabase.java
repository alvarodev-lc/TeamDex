package es.upm.miw.virgolini;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.upm.miw.virgolini.models.AbilityList;
import es.upm.miw.virgolini.models.Pokemon;
import es.upm.miw.virgolini.models.Stat;
import es.upm.miw.virgolini.models.Type;
import es.upm.miw.virgolini.models.TypeList;

public class TeamDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "TEAM";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "team";

    private static final String ID_COL = "id";

    private static final String NUM_COL = "num";

    private static final String NAME_COL = "name";

    private static final String TYPE_COL = "type";

    private static final String ABILITY_COL = "ability";

    private static final String HP_COL = "hp";
    private static final String DEF_COL = "def";
    private static final String ATK_COL = "atk";
    private static final String SPDEF_COL = "spdef";
    private static final String SPATK_COL = "spatk";
    private static final String SPEED_COL = "speed";

    private PokeAPI pokeAPI;

    public TeamDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER, "
                + NUM_COL + " INTEGER,"
                + NAME_COL + " TEXT,"
                + TYPE_COL + " TEXT,"
                + ABILITY_COL + " TEXT,"
                + HP_COL + " INTEGER,"
                + ATK_COL + " INTEGER,"
                + DEF_COL + " INTEGER,"
                + SPATK_COL + " INTEGER,"
                + SPDEF_COL + " INTEGER,"
                + SPEED_COL + " INTEGER)";

        db.execSQL(query);

        pokeAPI = new PokeAPI();
    }

    public void addPokemonToTeam(Pokemon pokemon) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        int poke_id = (int) (Math.random() * 1000);

        values.put(ID_COL, poke_id);

        String name = pokemon.getName().substring(0, 1).toUpperCase() + pokemon.getName().substring(1);
        values.put(NAME_COL, name);

        String poke_num = pokemon.getNum();
        values.put(NUM_COL, poke_num);

        List<TypeList> types = pokemon.getTypes();
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

        db.insert(TABLE_NAME, null, values);

        Log.d("DB", "Added pokemon to team in DB: " + name);

        db.close();
    }

    public void delete(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE name ='" + name + "'");
    }

    public ArrayList<Pair<String, String>> retrieveTeam() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Pair<String, String>> dexList = new ArrayList<>();

        String query = "SELECT " + NAME_COL + ", " + TYPE_COL + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        int i = 0;
        while (cursor.moveToNext()) {
            int colIndex = cursor.getColumnIndex(NAME_COL);
            String name = cursor.getString(colIndex);

            colIndex = cursor.getColumnIndex(TYPE_COL);
            String types = cursor.getString(colIndex);

            dexList.add(i, new Pair<>(name, types));
            i++;
        }
        return dexList;
    }

    public Pokemon getPokemon(String givenName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String name = givenName.toLowerCase(Locale.ROOT);

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + TABLE_NAME + ".name LIKE " + "'" + name + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();

        int colIndex = cursor.getColumnIndex(NUM_COL);
        int num = cursor.getInt(colIndex);

        pokeAPI.getPokemonData(num);
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

    public void addTeam(ArrayList<Pokemon> team) {
        for (Pokemon pokemon : team) {
            addPokemonToTeam(pokemon);
        }

        Log.d("DB", "Added team to DB");
    }
}