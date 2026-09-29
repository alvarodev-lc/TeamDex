package es.upm.mssde.pokedex.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Generated("jsonschema2pojo")
public class Species {

    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("base_happiness")
    @Expose
    private Integer baseHappiness;
    @SerializedName("capture_rate")
    @Expose
    private Float captureRate;
    @SerializedName("gender_rate")
    private Integer genderRate;
    @SerializedName("hatch_counter")
    private Integer hatchCounter;
    @SerializedName("is_baby")
    private Boolean isBaby;
    @SerializedName("is_legendary")
    private Boolean isLegendary;
    @SerializedName("is_mythical")
    private Boolean isMythical;
    @SerializedName("growth_rate")
    private NamedApiResource growthRate;
    @SerializedName("habitat")
    private NamedApiResource habitat;
    @SerializedName("generation")
    private NamedApiResource generation;
    @SerializedName("evolution_chain")
    private NamedApiResource evolutionChain;
    @SerializedName("egg_groups")
    private List<NamedApiResource> eggGroups;
    @SerializedName("genera")
    private List<Genus> genera;
    @SerializedName("names")
    private List<LocalizedName> names;
    @SerializedName("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getBaseHappiness() {
        return baseHappiness;
    }

    public void setBaseHappiness(Integer baseHappiness) {
        this.baseHappiness = baseHappiness;
    }

    public Float getRawCaptureRate() {
        return captureRate;
    }

    public Float getCaptureRate() {
        // 255 is 100%, parse integer to percentage
        float perc = (captureRate * 100) / 255;
        return BigDecimal.valueOf(perc).setScale(2, RoundingMode.HALF_DOWN).floatValue();
    }

    public void setCaptureRate(Float captureRate) {
        this.captureRate = captureRate;
    }

    /** Chance of being female in eighths, or -1 for genderless species. */
    public Integer getGenderRate() {
        return genderRate;
    }

    public Integer getHatchCounter() {
        return hatchCounter;
    }

    public boolean isBaby() {
        return Boolean.TRUE.equals(isBaby);
    }

    public boolean isLegendary() {
        return Boolean.TRUE.equals(isLegendary);
    }

    public boolean isMythical() {
        return Boolean.TRUE.equals(isMythical);
    }

    public NamedApiResource getGrowthRate() {
        return growthRate;
    }

    public NamedApiResource getHabitat() {
        return habitat;
    }

    public NamedApiResource getGeneration() {
        return generation;
    }

    public NamedApiResource getEvolutionChain() {
        return evolutionChain;
    }

    public List<NamedApiResource> getEggGroups() {
        return eggGroups;
    }

    public String getEnglishGenus() {
        if (genera != null) {
            for (Genus genus : genera) {
                if (genus.language != null && "en".equals(genus.language.getName())) {
                    return genus.genus;
                }
            }
        }
        return null;
    }

    public String getEnglishName() {
        if (names != null) {
            for (LocalizedName localized : names) {
                if (localized.language != null && "en".equals(localized.language.getName())) {
                    return localized.name;
                }
            }
        }
        return null;
    }

    public FlavorTextEntry getLatestEnglishFlavorText() {
        return FlavorTextEntry.latestInLanguage(flavorTextEntries, "en");
    }

    public static class Genus {
        @SerializedName("genus")
        private String genus;
        @SerializedName("language")
        private NamedApiResource language;
    }

    public static class LocalizedName {
        @SerializedName("name")
        private String name;
        @SerializedName("language")
        private NamedApiResource language;
    }
}
