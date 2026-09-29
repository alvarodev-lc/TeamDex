package es.upm.mssde.pokedex.models;

import com.google.gson.annotations.SerializedName;

// Fields are filled in by Gson through reflection, which the IDE can't see.
@SuppressWarnings("unused")
public class Sprites {

    @SerializedName("front_default")
    private String frontDefault;
    @SerializedName("front_shiny")
    private String frontShiny;
    @SerializedName("other")
    private Other other;

    public String getArtworkUrl(boolean shiny) {
        if (other != null) {
            String url = pick(other.officialArtwork, shiny);
            if (url == null) {
                url = pick(other.home, shiny);
            }
            if (url != null) {
                return url;
            }
        }
        return shiny ? frontShiny : frontDefault;
    }

    /** In-game pixel sprite. */
    public String getSpriteUrl(boolean shiny) {
        return shiny ? frontShiny : frontDefault;
    }

    private static String pick(Artwork artwork, boolean shiny) {
        if (artwork == null) {
            return null;
        }
        return shiny ? artwork.frontShiny : artwork.frontDefault;
    }

    public static class Other {
        @SerializedName("official-artwork")
        private Artwork officialArtwork;
        @SerializedName("home")
        private Artwork home;
    }

    public static class Artwork {
        @SerializedName("front_default")
        private String frontDefault;
        @SerializedName("front_shiny")
        private String frontShiny;
    }
}
