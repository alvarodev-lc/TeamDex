package es.upm.miw.virgolini.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class PokemonResult {


    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("num")
    @Expose
    private int num;

    public String getName() {
        // Capitalize first letter
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
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

    public int getNum() {
        String[] split_url = url.split("/");
        return Integer.parseInt(split_url[split_url.length - 1]);
    }

    public void setNum(int num) {
        this.num = num;
    }
}