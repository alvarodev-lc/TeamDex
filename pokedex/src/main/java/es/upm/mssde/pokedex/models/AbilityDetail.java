package es.upm.mssde.pokedex.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Fields are filled in by Gson through reflection, which the IDE can't see.
@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public class AbilityDetail {

    @SerializedName("effect_entries")
    private List<EffectEntry> effectEntries;
    @SerializedName("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries;

    // Newer abilities often lack English effect entries, so fall back to the in-game text.
    public String getShortDescription() {
        if (effectEntries != null) {
            for (EffectEntry entry : effectEntries) {
                if (entry.language != null && "en".equals(entry.language.getName())
                        && entry.shortEffect != null && !entry.shortEffect.isEmpty()) {
                    return entry.shortEffect.replaceAll("\\s+", " ").trim();
                }
            }
        }
        FlavorTextEntry flavor = FlavorTextEntry.latestInLanguage(flavorTextEntries, "en");
        return flavor != null ? flavor.getCleanText() : null;
    }

    public static class EffectEntry {
        @SerializedName("short_effect")
        private String shortEffect;
        @SerializedName("language")
        private NamedApiResource language;
    }
}
