package es.upm.mssde.pokedex.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NamedApiResource {

    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        if (url == null) {
            return null;
        }
        String[] parts = url.split("/");
        for (int i = parts.length - 1; i >= 0; i--) {
            if (!parts[i].isEmpty()) {
                try {
                    return Integer.parseInt(parts[i]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static String prettify(String slug) {
        if (slug == null || slug.isEmpty()) {
            return "";
        }
        List<String> words = new ArrayList<>();
        for (String word : slug.split("[-_ ]+")) {
            if (!word.isEmpty()) {
                words.add(word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1));
            }
        }
        return String.join(" ", words);
    }
}
