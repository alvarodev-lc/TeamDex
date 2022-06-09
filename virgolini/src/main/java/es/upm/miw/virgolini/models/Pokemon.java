package es.upm.miw.virgolini.models;

import android.util.Log;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Pokemon {

    @SerializedName("types")
    @Expose
    private List<TypeList> types = null;

    @SerializedName("abilities")
    @Expose
    private List<AbilityList> abilities = null;

    @SerializedName("base_experience")
    @Expose
    private String experience = null;

    @SerializedName("name")
    @Expose
    private String name = null;

    @SerializedName("height")
    @Expose
    private String height = null;

    @SerializedName("weight")
    @Expose
    private String weight = null;

    @SerializedName("stats")
    @Expose
    private List<Stat> stats = null;

    public List<TypeList> getTypes() {
        return types;
    }

    public void setTypes(List<TypeList> types) {
        this.types = types;
    }

    public List<AbilityList> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<AbilityList> abilities) {
        this.abilities = abilities;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getName() {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        // Height comes in decimeters, parse to meters
        float meters = Float.parseFloat(height) / 10;
        return String.valueOf(meters);
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        // Weight comes in hectograms, parse into kilograms
        float kg = Float.parseFloat(weight) / 10;
        return String.valueOf(kg);
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public List<Stat> getStats() {
        return stats;
    }

    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }
}