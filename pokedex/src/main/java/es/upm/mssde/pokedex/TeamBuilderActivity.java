package es.upm.mssde.pokedex;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import es.upm.mssde.pokedex.models.NamedApiResource;
import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.Stat;
import es.upm.mssde.pokedex.models.TeamCoverage;
import es.upm.mssde.pokedex.models.TypeDetail;
import es.upm.mssde.pokedex.models.TypeList;

public class TeamBuilderActivity extends AppCompatActivity {

    private static final int MAX_TEAM_SIZE = TeamViewerListAdapter.MAX_TEAM_SIZE;
    private static final int MAX_SEARCH_RESULTS = 8;

    private static final String STATE_TEAM_ID = "team_id";
    private static final String STATE_MEMBERS = "team_members";
    private static final String STATE_ORIGINAL_NUMS = "original_nums";
    private static final String STATE_ORIGINAL_NAME = "original_name";

    private final CallTracker calls = new CallTracker(this);
    private final Map<Integer, Pokemon> pokemonCache = new HashMap<>();
    private final Map<String, TypeDetail> typeCache = new HashMap<>();
    private final Set<Integer> pokemonRequests = new HashSet<>();
    private final Set<String> typeRequests = new HashSet<>();
    private final List<View> slotViews = new ArrayList<>();

    private TeamDatabase teamDatabase;
    private IPokemonEndpoint api;
    private PokeAPI pokeAPI;
    private ArrayList<PokemonResult> team = new ArrayList<>();
    private ArrayList<Integer> originalNums = new ArrayList<>();
    private String originalName = "";
    private String teamID;
    private boolean existingTeam;

    private Toolbar toolbar;
    private NestedScrollView scroll;
    private TextInputEditText nameInput;
    private TextInputLayout searchLayout;
    private TextInputEditText searchInput;
    private TextView searchMessage;
    private LinearLayout searchResults;
    private TextView memberCount;
    private View bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_builder);

        teamDatabase = new TeamDatabase(this);
        api = PokeApiClient.getRetrofit().create(IPokemonEndpoint.class);
        pokeAPI = new PokeAPI();
        bindViews();

        String extraTeamId = getIntent().getStringExtra("team_id");
        existingTeam = extraTeamId != null;
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else if (existingTeam) {
            teamID = extraTeamId;
            team = teamDatabase.getTeam(teamID);
            String name = teamDatabase.getTeamName(teamID);
            originalName = name != null ? name : "";
            nameInput.setText(originalName);
            originalNums = numsOf(team);
        }
        if (teamID == null) {
            teamID = String.valueOf(teamDatabase.getLatestTeamId() + 1);
        }

        setUpToolbar();
        setUpInsets();
        setUpBackHandling();
        ((TextInputLayout) findViewById(R.id.team_name_layout)).setHelperText(
                getString(R.string.team_name_helper, getString(R.string.team_number, teamID)));
        createSlots();
        setUpSearch();
        findViewById(R.id.save_team_button).setOnClickListener(v -> saveAndFinish());

        renderTeam();
    }

    @SuppressWarnings("unchecked")
    private void restoreState(Bundle state) {
        teamID = state.getString(STATE_TEAM_ID);
        ArrayList<PokemonResult> members = state.getSerializable(STATE_MEMBERS, ArrayList.class);
        if (members != null) {
            team = members;
        }
        ArrayList<Integer> nums = state.getIntegerArrayList(STATE_ORIGINAL_NUMS);
        if (nums != null) {
            originalNums = nums;
        }
        originalName = state.getString(STATE_ORIGINAL_NAME, "");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_TEAM_ID, teamID);
        outState.putSerializable(STATE_MEMBERS, team);
        outState.putIntegerArrayList(STATE_ORIGINAL_NUMS, originalNums);
        outState.putString(STATE_ORIGINAL_NAME, originalName);
    }

    @Override
    public void onDestroy() {
        calls.cancelAll();
        teamDatabase.close();
        super.onDestroy();
    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar_builder);
        scroll = findViewById(R.id.builder_scroll);
        nameInput = findViewById(R.id.team_name_input);
        searchLayout = findViewById(R.id.search_layout);
        searchInput = findViewById(R.id.search_input);
        searchMessage = findViewById(R.id.search_message);
        searchResults = findViewById(R.id.search_results);
        memberCount = findViewById(R.id.member_count);
        bottomBar = findViewById(R.id.builder_bottom_bar);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(existingTeam ? R.string.team_title_edit : R.string.team_title_new);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpInsets() {
        int bottomPadding = bottomBar.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.builder_root), (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            toolbar.setPadding(0, bars.top, 0, 0);
            bottomBar.setPadding(bottomBar.getPaddingLeft(), bottomBar.getPaddingTop(),
                    bottomBar.getPaddingRight(), Math.max(bars.bottom, ime.bottom) + bottomPadding);
            return windowInsets;
        });
    }

    private void setUpBackHandling() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isDirty()) {
                    new MaterialAlertDialogBuilder(TeamBuilderActivity.this)
                            .setTitle(R.string.team_unsaved_title)
                            .setMessage(R.string.team_unsaved_message)
                            .setPositiveButton(R.string.team_unsaved_save, (d, w) -> saveAndFinish())
                            .setNegativeButton(R.string.team_unsaved_discard, (d, w) -> finish())
                            .setNeutralButton(R.string.cancel, null)
                            .show();
                } else {
                    finish();
                }
            }
        });
    }

    // ---- Menu ----

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team_builder_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_clear_team).setEnabled(!team.isEmpty());
        menu.findItem(R.id.action_delete_team).setVisible(existingTeam);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        if (id == R.id.action_clear_team) {
            clearTeam();
            return true;
        }
        if (id == R.id.action_delete_team) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.team_delete_title)
                    .setMessage(R.string.team_delete_message)
                    .setPositiveButton(R.string.team_delete_confirm, (d, w) -> {
                        teamDatabase.deleteTeam(teamID);
                        Toast.makeText(this, R.string.team_deleted, Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---- Team state ----

    private boolean isDirty() {
        return !numsOf(team).equals(originalNums) || !currentName().equals(originalName);
    }

    private String currentName() {
        Editable text = nameInput.getText();
        return text != null ? text.toString().trim() : "";
    }

    private static ArrayList<Integer> numsOf(List<PokemonResult> members) {
        ArrayList<Integer> nums = new ArrayList<>();
        for (PokemonResult member : members) {
            nums.add(member.getNum());
        }
        return nums;
    }

    private boolean containsNum(int num) {
        for (PokemonResult member : team) {
            if (member.getNum() == num) {
                return true;
            }
        }
        return false;
    }

    private void saveAndFinish() {
        if (team.isEmpty() && !existingTeam) {
            finish();
            return;
        }
        teamDatabase.saveTeam(team, teamID, currentName());
        Toast.makeText(this, team.isEmpty() ? R.string.team_deleted : R.string.team_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void addMember(PokemonResult result) {
        if (team.size() >= MAX_TEAM_SIZE) {
            showSnackbar(getString(R.string.team_full), null);
            return;
        }
        if (containsNum(result.getNum())) {
            return;
        }
        PokemonResult member = new PokemonResult();
        member.setName(result.getName());
        member.setNum(result.getNum());
        team.add(member);

        searchInput.setText("");
        searchInput.clearFocus();
        WindowCompat.getInsetsController(getWindow(), searchInput).hide(WindowInsetsCompat.Type.ime());
        renderTeam();
    }

    private void removeMember(int index) {
        PokemonResult removed = team.remove(index);
        renderTeam();
        showSnackbar(getString(R.string.team_member_removed, NamedApiResource.prettify(removed.getName())), () -> {
            if (team.size() < MAX_TEAM_SIZE && !containsNum(removed.getNum())) {
                team.add(Math.min(index, team.size()), removed);
                renderTeam();
            }
        });
    }

    private void clearTeam() {
        ArrayList<PokemonResult> previous = new ArrayList<>(team);
        team.clear();
        renderTeam();
        showSnackbar(getString(R.string.team_cleared), () -> {
            team.clear();
            team.addAll(previous);
            renderTeam();
        });
    }

    private void showSnackbar(String message, @Nullable Runnable undo) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.builder_root), message, Snackbar.LENGTH_LONG)
                .setAnchorView(bottomBar);
        if (undo != null) {
            snackbar.setAction(R.string.undo, v -> undo.run());
        }
        snackbar.show();
    }

    // ---- Rendering ----

    private void createSlots() {
        GridLayout grid = findViewById(R.id.member_slots);
        LayoutInflater inflater = getLayoutInflater();
        int margin = ViewUtils.dp(this, 5);
        for (int i = 0; i < MAX_TEAM_SIZE; i++) {
            View slot = inflater.inflate(R.layout.item_team_slot, grid, false);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(i / 2), GridLayout.spec(i % 2, 1f));
            params.width = 0;
            params.setMargins(margin, margin, margin, margin);
            grid.addView(slot, params);
            slotViews.add(slot);
        }
    }

    private void renderTeam() {
        memberCount.setText(getString(R.string.team_members_count, team.size()));
        bindSlots();
        refreshSearchResults();
        invalidateOptionsMenu();
        fetchMissingDetails();
        updateAnalysis();
    }

    private void bindSlots() {
        for (int i = 0; i < MAX_TEAM_SIZE; i++) {
            bindSlot((MaterialCardView) slotViews.get(i), i < team.size() ? team.get(i) : null, i);
        }
    }

    private void bindSlot(MaterialCardView card, @Nullable PokemonResult member, int index) {
        FrameLayout frame = card.findViewById(R.id.slot_frame);
        View filled = card.findViewById(R.id.slot_filled);
        View empty = card.findViewById(R.id.slot_empty);
        ImageButton remove = card.findViewById(R.id.slot_remove);

        if (member == null) {
            filled.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            frame.setBackgroundResource(R.drawable.bg_slot_empty);
            card.setCardBackgroundColor(Color.WHITE);
            card.setContentDescription(getString(R.string.team_add_pokemon));
            card.setOnClickListener(v -> focusSearch());
            return;
        }

        empty.setVisibility(View.GONE);
        filled.setVisibility(View.VISIBLE);
        remove.setVisibility(View.VISIBLE);
        frame.setBackground(null);

        String name = NamedApiResource.prettify(member.getName());
        ((TextView) card.findViewById(R.id.slot_name)).setText(name);
        card.setContentDescription(name);
        ViewUtils.loadArtwork(card.findViewById(R.id.slot_image),
                PokeApiClient.artworkUrl(member.getNum()), PokeApiClient.spriteUrl(member.getNum()));
        remove.setContentDescription(getString(R.string.team_remove_member, name));
        remove.setOnClickListener(v -> removeMember(index));
        card.setOnClickListener(v -> openPokemon(member));

        LinearLayout typesRow = card.findViewById(R.id.slot_types);
        typesRow.removeAllViews();
        int background = ContextCompat.getColor(this, R.color.tileBackground);
        Pokemon details = pokemonCache.get(member.getNum());
        if (details != null) {
            List<String> types = typeNames(details);
            if (!types.isEmpty()) {
                background = ColorUtils.blendARGB(PokeFormat.typeColor(types.get(0)), Color.WHITE, 0.8f);
            }
            for (String type : types) {
                typesRow.addView(ViewUtils.createTypePill(this, type));
            }
        }
        card.setCardBackgroundColor(background);
    }

    private void openPokemon(PokemonResult member) {
        Intent intent = new Intent(this, PokemonActivity.class);
        intent.putExtra("pokemon", member);
        startActivity(intent);
    }

    // ---- Search ----

    private void setUpSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshSearchResults();
            }
        });
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            WindowCompat.getInsetsController(getWindow(), searchInput).hide(WindowInsetsCompat.Type.ime());
            return true;
        });
        pokeAPI.loadAllPokemonNames(() -> {
            if (!isFinishing() && !isDestroyed()) {
                refreshSearchResults();
            }
        });
    }

    private void focusSearch() {
        searchInput.requestFocus();
        WindowCompat.getInsetsController(getWindow(), searchInput).show(WindowInsetsCompat.Type.ime());
        scrollToSearch();
    }

    // Results render below the field, so pin the field near the top or the keyboard hides them.
    private void scrollToSearch() {
        scroll.post(() -> scroll.smoothScrollTo(0, searchLayout.getTop() - ViewUtils.dp(this, 16)));
    }

    private void refreshSearchResults() {
        Editable text = searchInput.getText();
        String query = text != null ? text.toString().trim() : "";
        searchResults.removeAllViews();
        searchResults.setVisibility(View.GONE);

        if (query.isEmpty()) {
            searchMessage.setVisibility(View.GONE);
            return;
        }
        if (team.size() >= MAX_TEAM_SIZE) {
            showSearchMessage(getString(R.string.team_full));
            return;
        }
        if (!pokeAPI.isAllNamesLoaded()) {
            showSearchMessage(getString(R.string.team_search_loading));
            return;
        }

        List<PokemonResult> matches = pokeAPI.searchByName(query);
        if (matches.isEmpty()) {
            showSearchMessage(getString(R.string.team_search_empty, query));
            return;
        }
        // Names that start with the query are what the user is most likely typing towards.
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        matches.sort((a, b) -> Boolean.compare(
                !a.getName().toLowerCase(Locale.ROOT).startsWith(lowerQuery),
                !b.getName().toLowerCase(Locale.ROOT).startsWith(lowerQuery)));

        searchMessage.setVisibility(View.GONE);
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < Math.min(MAX_SEARCH_RESULTS, matches.size()); i++) {
            searchResults.addView(createResultRow(inflater, matches.get(i)));
        }
        searchResults.setVisibility(View.VISIBLE);
        if (searchInput.hasFocus()) {
            scrollToSearch();
        }
    }

    private void showSearchMessage(String message) {
        searchMessage.setText(message);
        searchMessage.setVisibility(View.VISIBLE);
        if (searchInput.hasFocus()) {
            scrollToSearch();
        }
    }

    private View createResultRow(LayoutInflater inflater, PokemonResult result) {
        View row = inflater.inflate(R.layout.item_search_result, searchResults, false);
        int num = result.getNum();
        ((TextView) row.findViewById(R.id.result_name)).setText(NamedApiResource.prettify(result.getName()));
        ((TextView) row.findViewById(R.id.result_number)).setText(getString(R.string.pokemon_number, num));
        Picasso.get().load(PokeApiClient.spriteUrl(num)).into((ImageView) row.findViewById(R.id.result_sprite));

        if (containsNum(num)) {
            ImageView action = row.findViewById(R.id.result_action);
            action.setImageResource(R.drawable.ic_check);
            row.setContentDescription(getString(R.string.team_in_team));
            row.setAlpha(0.55f);
            row.setEnabled(false);
        } else {
            row.setOnClickListener(v -> addMember(result));
        }
        return row;
    }

    // ---- Details & analysis ----

    private static List<String> typeNames(Pokemon pokemon) {
        List<String> names = new ArrayList<>();
        if (pokemon.getTypes() != null) {
            for (TypeList typeList : pokemon.getTypes()) {
                names.add(typeList.getType().getName());
            }
        }
        return names;
    }

    private void fetchMissingDetails() {
        for (PokemonResult member : team) {
            int num = member.getNum();
            Pokemon cached = pokemonCache.get(num);
            if (cached != null) {
                fetchTypes(cached);
                continue;
            }
            if (!pokemonRequests.add(num)) {
                continue;
            }
            calls.enqueue(api.getPokemon(String.valueOf(num)), pokemon -> {
                pokemonCache.put(num, pokemon);
                fetchTypes(pokemon);
                bindSlots();
                updateAnalysis();
            }, () -> pokemonRequests.remove(num));
        }
    }

    private void fetchTypes(Pokemon pokemon) {
        for (String type : typeNames(pokemon)) {
            if (typeCache.containsKey(type) || !typeRequests.add(type)) {
                continue;
            }
            calls.enqueue(api.getType(type), detail -> {
                typeCache.put(type, detail);
                updateAnalysis();
            }, () -> typeRequests.remove(type));
        }
    }

    private void updateAnalysis() {
        View section = findViewById(R.id.section_analysis);
        List<Pokemon> loaded = new ArrayList<>();
        List<Map<String, Double>> memberMultipliers = new ArrayList<>();
        for (PokemonResult member : team) {
            Pokemon pokemon = pokemonCache.get(member.getNum());
            if (pokemon == null) {
                continue;
            }
            loaded.add(pokemon);
            List<TypeDetail> details = new ArrayList<>();
            for (String type : typeNames(pokemon)) {
                TypeDetail detail = typeCache.get(type);
                if (detail == null) {
                    details = null;
                    break;
                }
                details.add(detail);
            }
            if (details != null) {
                memberMultipliers.add(TypeDetail.defensiveMultipliers(details));
            }
        }

        if (loaded.isEmpty()) {
            section.setVisibility(View.GONE);
            return;
        }
        section.setVisibility(View.VISIBLE);
        renderCoverage(TeamCoverage.countMatchups(memberMultipliers));
        renderAverageStats(loaded);
    }

    private void renderCoverage(Map<String, TeamCoverage.Matchup> matchups) {
        List<Map.Entry<String, TeamCoverage.Matchup>> weaknesses = new ArrayList<>();
        List<Map.Entry<String, TeamCoverage.Matchup>> resistances = new ArrayList<>();
        for (Map.Entry<String, TeamCoverage.Matchup> entry : matchups.entrySet()) {
            TeamCoverage.Matchup matchup = entry.getValue();
            if (matchup.getWeak() > matchup.getResist()) {
                weaknesses.add(entry);
            } else if (matchup.getResist() > matchup.getWeak()) {
                resistances.add(entry);
            }
        }
        weaknesses.sort((a, b) -> Integer.compare(b.getValue().getWeak(), a.getValue().getWeak()));
        resistances.sort((a, b) -> Integer.compare(b.getValue().getResist(), a.getValue().getResist()));

        ChipGroup weakChips = findViewById(R.id.team_weak_chips);
        weakChips.removeAllViews();
        for (Map.Entry<String, TeamCoverage.Matchup> entry : weaknesses) {
            weakChips.addView(ViewUtils.createTypeChip(this, entry.getKey(), String.valueOf(entry.getValue().getWeak())));
        }
        findViewById(R.id.team_weak_none).setVisibility(weaknesses.isEmpty() ? View.VISIBLE : View.GONE);

        ChipGroup resistChips = findViewById(R.id.team_resist_chips);
        resistChips.removeAllViews();
        for (Map.Entry<String, TeamCoverage.Matchup> entry : resistances) {
            resistChips.addView(ViewUtils.createTypeChip(this, entry.getKey(), String.valueOf(entry.getValue().getResist())));
        }
        findViewById(R.id.team_resist_none).setVisibility(resistances.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void renderAverageStats(List<Pokemon> members) {
        LinearLayout container = findViewById(R.id.team_stats_container);
        container.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        int total = 0;
        for (String key : PokeFormat.STAT_ORDER) {
            int sum = 0;
            int count = 0;
            for (Pokemon pokemon : members) {
                if (pokemon.getStats() == null) {
                    continue;
                }
                for (Stat stat : pokemon.getStats()) {
                    if (key.equals(stat.getStat().getName()) && stat.getBaseStat() != null) {
                        sum += stat.getBaseStat();
                        count++;
                    }
                }
            }
            if (count == 0) {
                continue;
            }
            int average = Math.round((float) sum / count);
            total += average;
            container.addView(ViewUtils.createStatRow(inflater, container, PokeFormat.statLabel(key), average));
        }
        ((TextView) findViewById(R.id.team_stat_total)).setText(String.valueOf(total));
    }
}
