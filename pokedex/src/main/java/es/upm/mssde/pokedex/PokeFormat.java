package es.upm.mssde.pokedex;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class PokeFormat {

    public static final String[] STAT_ORDER =
            {"hp", "attack", "defense", "special-attack", "special-defense", "speed"};

    private static final int UNKNOWN_TYPE_COLOR = 0xFF68A090;

    private static final Map<String, Integer> TYPE_COLORS = new HashMap<>();

    static {
        TYPE_COLORS.put("normal", 0xFFA8A77A);
        TYPE_COLORS.put("fire", 0xFFEE8130);
        TYPE_COLORS.put("water", 0xFF6390F0);
        TYPE_COLORS.put("electric", 0xFFF7D02C);
        TYPE_COLORS.put("grass", 0xFF7AC74C);
        TYPE_COLORS.put("ice", 0xFF96D9D6);
        TYPE_COLORS.put("fighting", 0xFFC22E28);
        TYPE_COLORS.put("poison", 0xFFA33EA1);
        TYPE_COLORS.put("ground", 0xFFE2BF65);
        TYPE_COLORS.put("flying", 0xFFA98FF3);
        TYPE_COLORS.put("psychic", 0xFFF95587);
        TYPE_COLORS.put("bug", 0xFFA6B91A);
        TYPE_COLORS.put("rock", 0xFFB6A136);
        TYPE_COLORS.put("ghost", 0xFF735797);
        TYPE_COLORS.put("dragon", 0xFF6F35FC);
        TYPE_COLORS.put("dark", 0xFF705746);
        TYPE_COLORS.put("steel", 0xFFB7B7CE);
        TYPE_COLORS.put("fairy", 0xFFD685AD);
        TYPE_COLORS.put("stellar", 0xFF40B5A5);
    }

    private PokeFormat() {
    }

    public static int typeColor(String type) {
        Integer color = type != null ? TYPE_COLORS.get(type.toLowerCase(Locale.ROOT)) : null;
        return color != null ? color : UNKNOWN_TYPE_COLOR;
    }

    public static String statLabel(String statName) {
        return switch (statName) {
            case "hp" -> "HP";
            case "attack" -> "Attack";
            case "defense" -> "Defense";
            case "special-attack" -> "Sp. Atk";
            case "special-defense" -> "Sp. Def";
            case "speed" -> "Speed";
            default -> statName;
        };
    }

    public static int statColor(int value) {
        if (value < 30) return 0xFFF34444;
        if (value < 60) return 0xFFFF7F0F;
        if (value < 90) return 0xFFFFC631;
        if (value < 120) return 0xFFA0E515;
        if (value < 150) return 0xFF23CD5E;
        return 0xFF00C2B8;
    }

    public static String multiplier(double value) {
        if (value == 0.25) return "¼×";
        if (value == 0.5) return "½×";
        if (value == Math.rint(value)) return (int) value + "×";
        return value + "×";
    }

    public static String generation(String slug) {
        if (slug == null || !slug.startsWith("generation-")) {
            return null;
        }
        return "Gen " + slug.substring("generation-".length()).toUpperCase(Locale.ROOT);
    }

    public static String percent(double value) {
        return new DecimalFormat("0.#").format(value);
    }
}
