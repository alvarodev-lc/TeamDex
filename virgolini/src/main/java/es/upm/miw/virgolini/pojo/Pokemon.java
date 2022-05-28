package es.upm.miw.virgolini.pojo;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Generated("jsonschema2pojo")
public class Pokemon {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("abilities")
    @Expose
    private AbilityList abilities = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AbilityList getAbilities() {
        return abilities;
    }

    public void setAbilities(AbilityList abilities) {
        this.abilities = abilities;
    }

}