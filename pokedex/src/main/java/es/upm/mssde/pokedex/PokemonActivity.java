package es.upm.mssde.pokedex;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoublePredicate;

import es.upm.mssde.pokedex.models.AbilityList;
import es.upm.mssde.pokedex.models.Cries;
import es.upm.mssde.pokedex.models.EvolutionChain;
import es.upm.mssde.pokedex.models.FlavorTextEntry;
import es.upm.mssde.pokedex.models.NamedApiResource;
import es.upm.mssde.pokedex.models.Pokemon;
import es.upm.mssde.pokedex.models.PokemonResult;
import es.upm.mssde.pokedex.models.Species;
import es.upm.mssde.pokedex.models.Stat;
import es.upm.mssde.pokedex.models.TypeDetail;
import es.upm.mssde.pokedex.models.TypeList;
import retrofit2.Call;

public class PokemonActivity extends AppCompatActivity {

    private static final String LOG_TAG = "pokemon_activity";
    private static final String[] STAT_ORDER = PokeFormat.STAT_ORDER;
    private static final String STATE_SHINY = "showing_shiny";
    private static final String STATE_PIXEL_ART = "showing_pixel_art";

    private final CallTracker calls = new CallTracker(this);
    private IPokemonEndpoint api;
    private PokemonResult poke;
    private Pokemon pokemon;
    private MediaPlayer cryPlayer;

    private int currentSpeciesId;
    private String displayName;
    private boolean showingShiny;
    private boolean showingPixelArt;
    private boolean titleShown;
    private int headerColor;
    private int onHeaderColor = Color.WHITE;
    private final List<String> headerTypes = new ArrayList<>();
    private final List<String> headerBadges = new ArrayList<>();

    private Toolbar toolbar;
    private View header;
    private View nameRow;
    private TextView nameView;
    private TextView numberView;
    private TextView genusView;
    private ChipGroup headerChips;
    private ImageView artworkView;
    private ImageView watermarkView;
    private MaterialButton cryButton;
    private MaterialButton shinyButton;
    private MaterialButton pixelButton;
    private View loadingView;
    private View errorView;
    private View sectionsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_stats);

        poke = getIntent().getSerializableExtra("pokemon", PokemonResult.class);
        if (poke == null) {
            finish();
            return;
        }
        api = PokeApiClient.getRetrofit().create(IPokemonEndpoint.class);

        bindViews();
        setUpToolbar();
        setUpInsets();
        initTiles();

        currentSpeciesId = poke.getNum();
        displayName = NamedApiResource.prettify(poke.getName());
        nameView.setText(displayName);
        numberView.setText(getString(R.string.pokemon_number, poke.getNum()));
        headerColor = ContextCompat.getColor(this, R.color.colorPrimary);
        applyHeaderColor(headerColor);
        if (savedInstanceState != null) {
            showingShiny = savedInstanceState.getBoolean(STATE_SHINY);
            showingPixelArt = savedInstanceState.getBoolean(STATE_PIXEL_ART);
        }
        updateArtButtons();
        loadArtwork();

        setCryButtonEnabled(false);
        shinyButton.setOnClickListener(v -> toggleShiny());
        pixelButton.setOnClickListener(v -> togglePixelArt());
        findViewById(R.id.button_retry).setOnClickListener(v -> loadPokemon());

        loadPokemon();
    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar_pokemon);
        header = findViewById(R.id.header_layout);
        nameRow = findViewById(R.id.header_name_row);
        nameView = findViewById(R.id.poke_name);
        numberView = findViewById(R.id.poke_number);
        genusView = findViewById(R.id.poke_genus);
        headerChips = findViewById(R.id.header_chips);
        artworkView = findViewById(R.id.poke_image);
        watermarkView = findViewById(R.id.poke_watermark);
        cryButton = findViewById(R.id.button_play_cry);
        shinyButton = findViewById(R.id.button_toggle_shiny);
        pixelButton = findViewById(R.id.button_toggle_pixel);
        loadingView = findViewById(R.id.poke_loading);
        errorView = findViewById(R.id.poke_error);
        sectionsView = findViewById(R.id.poke_sections);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        NestedScrollView scroll = findViewById(R.id.poke_scroll);
        scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    int nameBottom = header.getTop() + nameRow.getTop() + nameView.getBottom();
                    boolean showTitle = scrollY > nameBottom;
                    if (showTitle != titleShown) {
                        titleShown = showTitle;
                        toolbar.setTitle(showTitle ? displayName : "");
                    }
                });
    }

    private void setUpInsets() {
        View scroll = findViewById(R.id.poke_scroll);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pokemon_root), (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            toolbar.setPadding(0, bars.top, 0, 0);
            scroll.setPadding(0, 0, 0, bars.bottom);
            return windowInsets;
        });
    }

    private void initTiles() {
        setTileLabel(R.id.tile_height, R.string.poke_label_height);
        setTileLabel(R.id.tile_weight, R.string.poke_label_weight);
        setTileLabel(R.id.tile_habitat, R.string.poke_label_habitat);
        setTileLabel(R.id.tile_capture_rate, R.string.poke_label_capture_rate);
        setTileLabel(R.id.tile_happiness, R.string.poke_label_happiness);
        setTileLabel(R.id.tile_base_exp, R.string.poke_label_base_exp);
        setTileLabel(R.id.tile_growth_rate, R.string.poke_label_growth_rate);
        setTileLabel(R.id.tile_ev_yield, R.string.poke_label_ev_yield);
        setTileLabel(R.id.tile_egg_groups, R.string.poke_label_egg_groups);
        setTileLabel(R.id.tile_egg_cycles, R.string.poke_label_egg_cycles);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        calls.cancelAll();
        if (cryPlayer != null) {
            cryPlayer.release();
            cryPlayer = null;
        }
        super.onDestroy();
    }

    // ---- Networking ----

    private <T> void enqueue(Call<T> call, Consumer<T> onSuccess, @Nullable Runnable onFailure) {
        calls.enqueue(call, onSuccess, onFailure);
    }

    private void loadPokemon() {
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        sectionsView.setVisibility(View.GONE);
        enqueue(api.getPokemon(String.valueOf(poke.getNum())), this::onPokemonLoaded, () -> {
            loadingView.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
        });
    }

    private void onPokemonLoaded(Pokemon loaded) {
        pokemon = loaded;
        loadingView.setVisibility(View.GONE);
        sectionsView.setVisibility(View.VISIBLE);

        NamedApiResource species = loaded.getSpecies();
        Integer speciesId = species != null ? species.getId() : null;
        if (speciesId != null) {
            currentSpeciesId = speciesId;
            numberView.setText(getString(R.string.pokemon_number, speciesId));
        }
        displayName = NamedApiResource.prettify(loaded.getName());
        nameView.setText(displayName);

        headerTypes.clear();
        if (loaded.getTypes() != null) {
            for (TypeList typeList : loaded.getTypes()) {
                headerTypes.add(typeList.getType().getName());
            }
        }
        int typeColor = PokeFormat.typeColor(headerTypes.isEmpty() ? null : headerTypes.get(0));
        applyHeaderColor(ColorUtils.blendARGB(typeColor, Color.BLACK, 0.12f));
        loadArtwork();
        setUpCryButton(loaded.getCries());

        bindAbout(loaded);
        bindStats(loaded);
        bindTraining(loaded);
        bindAbilities(loaded);
        loadTypeDefenses(new ArrayList<>(headerTypes));
        if (speciesId != null) {
            enqueue(api.getPokemonSpecies(String.valueOf(speciesId)), this::onSpeciesLoaded, null);
        }
    }

    // ---- Header ----

    private void applyHeaderColor(int color) {
        int from = headerColor;
        headerColor = color;
        ValueAnimator animator = ValueAnimator.ofArgb(from, color);
        animator.setDuration(350);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            header.setBackgroundColor(value);
            toolbar.setBackgroundColor(value);
        });
        animator.start();

        int darkText = ContextCompat.getColor(this, R.color.textPrimary);
        boolean useDarkText = ColorUtils.calculateContrast(darkText, color)
                > ColorUtils.calculateContrast(Color.WHITE, color);
        onHeaderColor = useDarkText ? darkText : Color.WHITE;

        nameView.setTextColor(onHeaderColor);
        numberView.setTextColor(ColorUtils.setAlphaComponent(onHeaderColor, 190));
        genusView.setTextColor(ColorUtils.setAlphaComponent(onHeaderColor, 220));
        toolbar.setTitleTextColor(onHeaderColor);
        Drawable navigationIcon = toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.mutate().setTint(onHeaderColor);
        }
        watermarkView.setColorFilter(onHeaderColor, PorterDuff.Mode.SRC_IN);
        styleHeaderButton(cryButton);
        styleHeaderButton(shinyButton);
        styleHeaderButton(pixelButton);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(useDarkText);
        renderHeaderChips();
    }

    private void styleHeaderButton(MaterialButton button) {
        ColorStateList content = ColorStateList.valueOf(onHeaderColor);
        button.setTextColor(content);
        button.setIconTint(content);
        button.setStrokeColor(ColorStateList.valueOf(ColorUtils.setAlphaComponent(onHeaderColor, 120)));
        button.setRippleColor(ColorStateList.valueOf(ColorUtils.setAlphaComponent(onHeaderColor, 50)));
    }

    private void renderHeaderChips() {
        headerChips.removeAllViews();
        ColorStateList stroke = ColorStateList.valueOf(ColorUtils.setAlphaComponent(onHeaderColor, 140));
        for (String type : headerTypes) {
            int typeColor = PokeFormat.typeColor(type);
            Chip chip = createChip(NamedApiResource.prettify(type), typeColor, readableTextOn(typeColor));
            chip.setChipStrokeColor(stroke);
            chip.setChipStrokeWidth(dp(1));
            headerChips.addView(chip);
        }
        for (String badge : headerBadges) {
            // Chips draw an opaque surface layer, so a transparent background would render white.
            Chip chip = createChip(badge, headerColor, onHeaderColor);
            chip.setChipStrokeColor(stroke);
            chip.setChipStrokeWidth(dp(1));
            headerChips.addView(chip);
        }
    }

    private void loadArtwork() {
        boolean pixel = showingPixelArt;
        String primary = pixel ? spriteUrl() : artworkUrl();
        String fallback = pixel ? artworkUrl() : spriteUrl();
        Picasso.get().load(primary).into(artworkView, new Callback() {
            @Override
            public void onSuccess() {
                setPixelScaling(pixel);
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(fallback).into(artworkView, new Callback() {
                    @Override
                    public void onSuccess() {
                        setPixelScaling(!pixel);
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
            }
        });
    }

    private String artworkUrl() {
        String url = pokemon != null && pokemon.getSprites() != null
                ? pokemon.getSprites().getArtworkUrl(showingShiny) : null;
        if (url != null) {
            return url;
        }
        return showingShiny ? PokeApiClient.shinyArtworkUrl(poke.getNum()) : PokeApiClient.artworkUrl(poke.getNum());
    }

    private String spriteUrl() {
        String url = pokemon != null && pokemon.getSprites() != null
                ? pokemon.getSprites().getSpriteUrl(showingShiny) : null;
        if (url != null) {
            return url;
        }
        return showingShiny ? PokeApiClient.shinySpriteUrl(poke.getNum()) : PokeApiClient.spriteUrl(poke.getNum());
    }

    // Sprites are ~96px; nearest-neighbour scaling keeps the pixels crisp instead of blurring them.
    private void setPixelScaling(boolean pixel) {
        Drawable drawable = artworkView.getDrawable();
        if (drawable != null) {
            drawable.setFilterBitmap(!pixel);
            drawable.invalidateSelf();
        }
    }

    private void toggleShiny() {
        showingShiny = !showingShiny;
        updateArtButtons();
        loadArtwork();
    }

    private void togglePixelArt() {
        showingPixelArt = !showingPixelArt;
        updateArtButtons();
        loadArtwork();
    }

    private void updateArtButtons() {
        shinyButton.setText(showingShiny ? R.string.poke_normal : R.string.poke_shiny);
        pixelButton.setText(showingPixelArt ? R.string.poke_artwork : R.string.poke_pixel_art);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SHINY, showingShiny);
        outState.putBoolean(STATE_PIXEL_ART, showingPixelArt);
    }

    private void setUpCryButton(Cries cries) {
        String cryUrl = null;
        if (cries != null) {
            cryUrl = cries.getLatest() != null ? cries.getLatest() : cries.getLegacy();
        }
        String finalCryUrl = cryUrl;
        setCryButtonEnabled(finalCryUrl != null);
        cryButton.setOnClickListener(v -> playCry(finalCryUrl));
    }

    private void setCryButtonEnabled(boolean enabled) {
        cryButton.setEnabled(enabled);
        cryButton.setAlpha(enabled ? 1f : 0.5f);
    }

    private void playCry(String url) {
        if (url == null) {
            return;
        }
        if (cryPlayer != null) {
            cryPlayer.release();
        }
        cryPlayer = new MediaPlayer();
        cryPlayer.setOnPreparedListener(MediaPlayer::start);
        cryPlayer.setOnCompletionListener(MediaPlayer::release);
        cryPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(LOG_TAG, "Failed to play pokemon cry: what=" + what + " extra=" + extra);
            Toast.makeText(this, R.string.poke_cry_error, Toast.LENGTH_SHORT).show();
            mp.release();
            return true;
        });
        try {
            cryPlayer.setDataSource(url);
            cryPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to load pokemon cry", e);
        }
    }

    // ---- Sections backed by /pokemon ----

    private void bindAbout(Pokemon loaded) {
        setTileValue(R.id.tile_height, orUnknown(loaded.getHeight(), R.string.poke_height_value));
        setTileValue(R.id.tile_weight, orUnknown(loaded.getWeight(), R.string.poke_weight_value));
    }

    private void bindStats(Pokemon loaded) {
        LinearLayout container = findViewById(R.id.stats_container);
        container.removeAllViews();
        Map<String, Stat> statsByName = statsByName(loaded);
        LayoutInflater inflater = getLayoutInflater();
        int total = 0;
        for (String key : STAT_ORDER) {
            Stat stat = statsByName.get(key);
            if (stat == null || stat.getBaseStat() == null) {
                continue;
            }
            int value = stat.getBaseStat();
            total += value;
            container.addView(ViewUtils.createStatRow(inflater, container, PokeFormat.statLabel(key), value));
        }
        ((TextView) findViewById(R.id.stat_total)).setText(String.valueOf(total));
    }

    private void bindTraining(Pokemon loaded) {
        String experience = loaded.getExperience();
        setTileValue(R.id.tile_base_exp, experience != null ? experience : getString(R.string.poke_unknown));

        List<String> evYield = new ArrayList<>();
        Map<String, Stat> statsByName = statsByName(loaded);
        for (String key : STAT_ORDER) {
            Stat stat = statsByName.get(key);
            if (stat != null && stat.getEffort() != null && stat.getEffort() > 0) {
                evYield.add(stat.getEffort() + " " + PokeFormat.statLabel(key));
            }
        }
        setTileValue(R.id.tile_ev_yield, evYield.isEmpty() ? getString(R.string.poke_unknown) : String.join(", ", evYield));
    }

    private Map<String, Stat> statsByName(Pokemon loaded) {
        Map<String, Stat> statsByName = new HashMap<>();
        if (loaded.getStats() != null) {
            for (Stat stat : loaded.getStats()) {
                statsByName.put(stat.getStat().getName(), stat);
            }
        }
        return statsByName;
    }

    private void bindAbilities(Pokemon loaded) {
        LinearLayout container = findViewById(R.id.abilities_container);
        container.removeAllViews();
        if (loaded.getAbilities() == null) {
            return;
        }
        LayoutInflater inflater = getLayoutInflater();
        for (AbilityList abilityList : loaded.getAbilities()) {
            String abilityName = abilityList.getAbility().getName();
            View item = inflater.inflate(R.layout.item_ability, container, false);
            ((TextView) item.findViewById(R.id.ability_name)).setText(NamedApiResource.prettify(abilityName));
            item.findViewById(R.id.ability_hidden)
                    .setVisibility(Boolean.TRUE.equals(abilityList.getIsHidden()) ? View.VISIBLE : View.GONE);
            TextView description = item.findViewById(R.id.ability_description);
            container.addView(item);

            enqueue(api.getAbility(abilityName), detail -> {
                String text = detail.getShortDescription();
                if (text != null) {
                    description.setText(text);
                } else {
                    description.setVisibility(View.GONE);
                }
            }, () -> description.setVisibility(View.GONE));
        }
    }

    private void loadTypeDefenses(List<String> types) {
        if (types.isEmpty()) {
            return;
        }
        TypeDetail[] details = new TypeDetail[types.size()];
        int[] remaining = {types.size()};
        for (int i = 0; i < types.size(); i++) {
            int index = i;
            enqueue(api.getType(types.get(i)), detail -> {
                details[index] = detail;
                remaining[0]--;
                if (remaining[0] == 0) {
                    renderTypeDefenses(Arrays.asList(details));
                }
            }, null);
        }
    }

    private void renderTypeDefenses(List<TypeDetail> details) {
        Map<String, Double> multipliers = TypeDetail.defensiveMultipliers(details);
        fillMultiplierGroup(R.id.label_weaknesses, R.id.chips_weaknesses, multipliers, value -> value > 1, true);
        fillMultiplierGroup(R.id.label_resistances, R.id.chips_resistances, multipliers, value -> value > 0 && value < 1, true);
        fillMultiplierGroup(R.id.label_immunities, R.id.chips_immunities, multipliers, value -> value == 0, false);
        findViewById(R.id.section_defenses).setVisibility(View.VISIBLE);
    }

    private void fillMultiplierGroup(int labelId, int groupId, Map<String, Double> multipliers,
                                     DoublePredicate include, boolean showMultiplier) {
        ChipGroup group = findViewById(groupId);
        group.removeAllViews();
        for (Map.Entry<String, Double> entry : multipliers.entrySet()) {
            if (!include.test(entry.getValue())) {
                continue;
            }
            String suffix = showMultiplier ? PokeFormat.multiplier(entry.getValue()) : null;
            group.addView(ViewUtils.createTypeChip(this, entry.getKey(), suffix));
        }
        int visibility = group.getChildCount() > 0 ? View.VISIBLE : View.GONE;
        group.setVisibility(visibility);
        findViewById(labelId).setVisibility(visibility);
    }

    // ---- Sections backed by /pokemon-species ----

    private void onSpeciesLoaded(Species species) {
        String genus = species.getEnglishGenus();
        if (genus != null) {
            genusView.setText(genus);
            genusView.setVisibility(View.VISIBLE);
        }

        // Only the default form shares the species name; forms like "charizard-mega-x" keep their own.
        String englishName = species.getEnglishName();
        if (englishName != null && pokemon != null && species.getName() != null
                && species.getName().equalsIgnoreCase(pokemon.getName())) {
            displayName = englishName;
            nameView.setText(englishName);
        }

        headerBadges.clear();
        if (species.isLegendary()) {
            headerBadges.add(getString(R.string.poke_legendary));
        }
        if (species.isMythical()) {
            headerBadges.add(getString(R.string.poke_mythical));
        }
        if (species.isBaby()) {
            headerBadges.add(getString(R.string.poke_baby));
        }
        String generation = species.getGeneration() != null
                ? PokeFormat.generation(species.getGeneration().getName()) : null;
        if (generation != null) {
            headerBadges.add(generation);
        }
        renderHeaderChips();

        bindFlavorText(species.getLatestEnglishFlavorText());
        setTileValue(R.id.tile_habitat, prettifyOrUnknown(species.getHabitat()));
        bindSpeciesTraining(species);
        bindBreeding(species);

        NamedApiResource chain = species.getEvolutionChain();
        Integer chainId = chain != null ? chain.getId() : null;
        if (chainId != null) {
            enqueue(api.getEvolutionChain(chainId), this::renderEvolution, null);
        }
    }

    private void bindFlavorText(FlavorTextEntry entry) {
        if (entry == null) {
            return;
        }
        TextView flavorView = findViewById(R.id.poke_flavor_text);
        flavorView.setText(entry.getCleanText());
        flavorView.setVisibility(View.VISIBLE);
        if (entry.getVersion() != null) {
            TextView sourceView = findViewById(R.id.poke_flavor_source);
            sourceView.setText(getString(R.string.poke_flavor_source,
                    NamedApiResource.prettify(entry.getVersion().getName())));
            sourceView.setVisibility(View.VISIBLE);
        }
    }

    private void bindSpeciesTraining(Species species) {
        Float rawCaptureRate = species.getRawCaptureRate();
        setTileValue(R.id.tile_capture_rate, rawCaptureRate != null
                ? getString(R.string.poke_capture_rate_value, rawCaptureRate.intValue(),
                PokeFormat.percent(species.getCaptureRate()))
                : getString(R.string.poke_unknown));
        Integer happiness = species.getBaseHappiness();
        setTileValue(R.id.tile_happiness, happiness != null ? String.valueOf(happiness) : getString(R.string.poke_unknown));
        setTileValue(R.id.tile_growth_rate, prettifyOrUnknown(species.getGrowthRate()));
    }

    private void bindBreeding(Species species) {
        Integer genderRate = species.getGenderRate();
        TextView genderText = findViewById(R.id.gender_text);
        if (genderRate == null) {
            genderText.setText(R.string.poke_unknown);
        } else if (genderRate < 0) {
            genderText.setText(R.string.poke_genderless);
        } else {
            double female = genderRate * 12.5;
            double male = 100 - female;
            LinearProgressIndicator bar = findViewById(R.id.gender_bar);
            bar.setVisibility(View.VISIBLE);
            bar.setProgressCompat((int) Math.round(male), true);
            ((TextView) findViewById(R.id.gender_male)).setText(getString(R.string.poke_gender_male, PokeFormat.percent(male)));
            ((TextView) findViewById(R.id.gender_female)).setText(getString(R.string.poke_gender_female, PokeFormat.percent(female)));
            findViewById(R.id.gender_labels).setVisibility(View.VISIBLE);
            genderText.setVisibility(View.GONE);
        }

        List<String> eggGroups = new ArrayList<>();
        if (species.getEggGroups() != null) {
            for (NamedApiResource group : species.getEggGroups()) {
                eggGroups.add(NamedApiResource.prettify(group.getName()));
            }
        }
        setTileValue(R.id.tile_egg_groups, eggGroups.isEmpty() ? getString(R.string.poke_unknown) : String.join(", ", eggGroups));
        Integer hatchCounter = species.getHatchCounter();
        setTileValue(R.id.tile_egg_cycles, hatchCounter != null ? String.valueOf(hatchCounter) : getString(R.string.poke_unknown));
    }

    // ---- Evolution ----

    private void renderEvolution(EvolutionChain chain) {
        List<List<EvolutionChain.ChainLink>> stages = chain.getStages();
        LinearLayout container = findViewById(R.id.evolution_container);
        container.removeAllViews();

        boolean evolves = stages.size() > 1;
        findViewById(R.id.evolution_scroll).setVisibility(evolves ? View.VISIBLE : View.GONE);
        findViewById(R.id.evolution_none).setVisibility(evolves ? View.GONE : View.VISIBLE);

        if (evolves) {
            int widestStage = 0;
            for (List<EvolutionChain.ChainLink> stage : stages) {
                widestStage = Math.max(widestStage, stage.size());
            }
            // Heavily branched chains (Eevee, Tyrogue) read better top-to-bottom with each stage as a grid.
            boolean vertical = widestStage > 2;
            container.setOrientation(vertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
            container.setGravity(vertical ? Gravity.CENTER_HORIZONTAL : Gravity.CENTER_HORIZONTAL | Gravity.TOP);

            for (int depth = 0; depth < stages.size(); depth++) {
                if (depth > 0) {
                    ImageView arrow = new ImageView(this);
                    arrow.setImageResource(R.drawable.ic_chevron_right);
                    arrow.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                    LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(dp(24), dp(24));
                    if (vertical) {
                        arrow.setRotation(90);
                    } else {
                        // Line the arrow up with the centre of the sprite (6dp padding + 76dp / 2).
                        arrowParams.topMargin = dp(32);
                    }
                    container.addView(arrow, arrowParams);
                }

                List<EvolutionChain.ChainLink> stage = stages.get(depth);
                ViewGroup group;
                if (vertical) {
                    GridLayout grid = new GridLayout(this);
                    grid.setColumnCount(Math.min(3, stage.size()));
                    group = grid;
                } else {
                    LinearLayout column = new LinearLayout(this);
                    column.setOrientation(LinearLayout.VERTICAL);
                    column.setGravity(Gravity.CENTER_HORIZONTAL);
                    group = column;
                }
                for (EvolutionChain.ChainLink link : stage) {
                    group.addView(createEvolutionItem(group, link, depth > 0));
                }
                container.addView(group, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
        findViewById(R.id.section_evolution).setVisibility(View.VISIBLE);
    }

    private View createEvolutionItem(ViewGroup parent, EvolutionChain.ChainLink link, boolean showRequirement) {
        View item = getLayoutInflater().inflate(R.layout.item_evolution, parent, false);
        NamedApiResource species = link.getSpecies();
        Integer speciesId = species.getId();
        ImageView image = item.findViewById(R.id.evo_image);
        TextView name = item.findViewById(R.id.evo_name);
        name.setText(NamedApiResource.prettify(species.getName()));
        if (speciesId != null) {
            Picasso.get().load(PokeApiClient.artworkUrl(speciesId)).into(image);
        }

        if (showRequirement) {
            EvolutionChain.EvolutionDetail detail = link.getPrimaryDetail();
            String requirement = detail != null ? detail.describe() : null;
            if (requirement != null) {
                TextView requirementView = item.findViewById(R.id.evo_requirement);
                requirementView.setText(requirement);
                requirementView.setVisibility(View.VISIBLE);
            }
        }

        if (speciesId != null && speciesId == currentSpeciesId) {
            name.setTypeface(name.getTypeface(), Typeface.BOLD);
            GradientDrawable ring = new GradientDrawable();
            ring.setShape(GradientDrawable.OVAL);
            ring.setColor(ContextCompat.getColor(this, R.color.tileBackground));
            ring.setStroke(dp(3), headerColor);
            image.setBackground(ring);
            item.setClickable(false);
        } else if (speciesId != null) {
            item.setOnClickListener(v -> openPokemon(species.getName(), speciesId));
        }
        return item;
    }

    private void openPokemon(String name, int speciesId) {
        PokemonResult target = new PokemonResult();
        target.setName(name);
        target.setNum(speciesId);
        Intent intent = new Intent(this, PokemonActivity.class);
        intent.putExtra("pokemon", target);
        startActivity(intent);
    }

    // ---- Helpers ----

    private Chip createChip(String text, int background, int textColor) {
        return ViewUtils.createChip(this, text, background, textColor);
    }

    private int readableTextOn(int background) {
        return ViewUtils.readableTextOn(this, background);
    }

    private void setTileLabel(int tileId, @StringRes int label) {
        ((TextView) findViewById(tileId).findViewById(R.id.tile_label)).setText(label);
    }

    private void setTileValue(int tileId, CharSequence value) {
        ((TextView) findViewById(tileId).findViewById(R.id.tile_value)).setText(value);
    }

    private String orUnknown(String value, @StringRes int format) {
        return value == null || value.isEmpty() ? getString(R.string.poke_unknown) : getString(format, value);
    }

    private String prettifyOrUnknown(NamedApiResource resource) {
        return resource != null ? NamedApiResource.prettify(resource.getName()) : getString(R.string.poke_unknown);
    }

    private int dp(int value) {
        return ViewUtils.dp(this, value);
    }
}
