package es.upm.mssde.pokedex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.PokemonTeam;

public class TeamDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "TEAM";

    private static final int DB_VERSION = 2;

    private static final String TABLE_NAME = "team";

    private static final String META_TABLE_NAME = "team_meta";

    private static final String TEAM_ID_COL = "team_id";

    private static final String NUM_COL = "num";

    private static final String NAME_COL = "name";

    private static final String TEAM_NAME_COL = "team_name";

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
        createMetaTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Migrations must keep existing rows: users' saved teams live in this database.
        if (oldVersion < 2) {
            createMetaTable(db);
        }
    }

    private static void createMetaTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + META_TABLE_NAME + " ("
                + TEAM_ID_COL + " INTEGER PRIMARY KEY, "
                + TEAM_NAME_COL + " TEXT);");
    }

    public ArrayList<PokemonResult> getTeam(String team_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PokemonResult> team_pokemons = new ArrayList<>();

        String query = "SELECT " + NUM_COL + ", " + NAME_COL + " FROM " + TABLE_NAME
                + " WHERE " + TEAM_ID_COL + " = ? ORDER BY rowid";
        try (Cursor cursor = db.rawQuery(query, new String[]{team_id})) {
            while (cursor.moveToNext()) {
                team_pokemons.add(readPokemon(cursor, 0, 1));
            }
        }
        return team_pokemons;
    }

    public String getTeamName(String team_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TEAM_NAME_COL + " FROM " + META_TABLE_NAME + " WHERE " + TEAM_ID_COL + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{team_id})) {
            return cursor.moveToFirst() ? cursor.getString(0) : null;
        }
    }

    public ArrayList<PokemonTeam> getAllTeams() {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<Integer, PokemonTeam> teamsById = new LinkedHashMap<>();

        String query = "SELECT " + TEAM_ID_COL + ", " + NUM_COL + ", " + NAME_COL + " FROM " + TABLE_NAME
                + " ORDER BY " + TEAM_ID_COL + ", rowid";
        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                int teamId = cursor.getInt(0);
                PokemonTeam team = teamsById.get(teamId);
                if (team == null) {
                    team = new PokemonTeam();
                    team.setTeamId(String.valueOf(teamId));
                    teamsById.put(teamId, team);
                }
                team.getTeamPokemons().add(readPokemon(cursor, 1, 2));
            }
        }

        String namesQuery = "SELECT " + TEAM_ID_COL + ", " + TEAM_NAME_COL + " FROM " + META_TABLE_NAME;
        try (Cursor cursor = db.rawQuery(namesQuery, null)) {
            while (cursor.moveToNext()) {
                PokemonTeam team = teamsById.get(cursor.getInt(0));
                if (team != null) {
                    team.setTeamName(cursor.getString(1));
                }
            }
        }
        return new ArrayList<>(teamsById.values());
    }

    private static PokemonResult readPokemon(Cursor cursor, int numIndex, int nameIndex) {
        PokemonResult pokemon = new PokemonResult();
        pokemon.setNum(cursor.getInt(numIndex));
        pokemon.setName(cursor.getString(nameIndex));
        return pokemon;
    }

    /**
     * Replaces the team stored under team_id with the given roster and name in a single
     * transaction, so a crash mid-save can't leave the team half-deleted.
     * Saving an empty roster deletes the team.
     */
    public void saveTeam(List<PokemonResult> team, String team_id, String teamName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String[] args = new String[]{team_id};
            db.delete(TABLE_NAME, TEAM_ID_COL + " = ?", args);
            db.delete(META_TABLE_NAME, TEAM_ID_COL + " = ?", args);
            for (PokemonResult pokemon : team) {
                ContentValues values = new ContentValues();
                values.put(TEAM_ID_COL, team_id);
                values.put(NUM_COL, pokemon.getNum());
                values.put(NAME_COL, pokemon.getName());
                db.insert(TABLE_NAME, null, values);
            }
            if (!team.isEmpty() && teamName != null && !teamName.trim().isEmpty()) {
                ContentValues meta = new ContentValues();
                meta.put(TEAM_ID_COL, team_id);
                meta.put(TEAM_NAME_COL, teamName.trim());
                db.insert(META_TABLE_NAME, null, meta);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.d("DB", "Saved team to DB with ID: " + team_id);
    }

    public void deleteTeam(String team_id) {
        saveTeam(new ArrayList<>(), team_id, null);
    }

    public int getLatestTeamId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + TEAM_ID_COL + ") FROM " + TABLE_NAME;
        try (Cursor cursor = db.rawQuery(query, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }
}
