package es.upm.mssde.pokedex.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Fields are filled in by Gson through reflection, which the IDE can't see.
@SuppressWarnings("unused")
public class FlavorTextEntry {

    @SerializedName("flavor_text")
    private String flavorText;
    @SerializedName("language")
    private NamedApiResource language;
    @SerializedName("version")
    private NamedApiResource version;

    public NamedApiResource getVersion() {
        return version;
    }

    // Game text uses hard line breaks and form feeds for the in-game text box.
    public String getCleanText() {
        if (flavorText == null) {
            return null;
        }
        return flavorText
                .replaceAll("(?<=[-—])[\\n\\f\\r]+", "")
                .replaceAll("[\\n\\f\\r\\u00AD]+", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    // Entries are ordered oldest game first, so the last match is the most recent text.
    public static FlavorTextEntry latestInLanguage(List<FlavorTextEntry> entries, String languageName) {
        if (entries == null) {
            return null;
        }
        FlavorTextEntry latest = null;
        for (FlavorTextEntry entry : entries) {
            if (entry.flavorText != null && entry.language != null
                    && languageName.equals(entry.language.getName())) {
                latest = entry;
            }
        }
        return latest;
    }
}
