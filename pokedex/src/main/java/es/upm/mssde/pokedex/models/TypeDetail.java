package es.upm.mssde.pokedex.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TypeDetail {

    @SerializedName("damage_relations")
    private DamageRelations damageRelations;

    public void setDamageRelations(DamageRelations damageRelations) {
        this.damageRelations = damageRelations;
    }

    /**
     * Damage multiplier each attacking type deals to a Pokémon with all of the given types.
     * Neutral (1×) matchups are omitted; entries are sorted from most to least effective.
     */
    public static Map<String, Double> defensiveMultipliers(List<TypeDetail> defendingTypes) {
        Map<String, Double> multipliers = new HashMap<>();
        for (TypeDetail type : defendingTypes) {
            if (type == null || type.damageRelations == null) {
                continue;
            }
            apply(multipliers, type.damageRelations.doubleDamageFrom, 2.0);
            apply(multipliers, type.damageRelations.halfDamageFrom, 0.5);
            apply(multipliers, type.damageRelations.noDamageFrom, 0.0);
        }
        multipliers.values().removeIf(value -> value == 1.0);

        List<Map.Entry<String, Double>> entries = new ArrayList<>(multipliers.entrySet());
        entries.sort((a, b) -> {
            int byValue = Double.compare(b.getValue(), a.getValue());
            return byValue != 0 ? byValue : a.getKey().compareTo(b.getKey());
        });
        Map<String, Double> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : entries) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        return sorted;
    }

    private static void apply(Map<String, Double> multipliers, List<NamedApiResource> attackers, double factor) {
        if (attackers == null) {
            return;
        }
        for (NamedApiResource attacker : attackers) {
            multipliers.merge(attacker.getName(), factor, (current, f) -> current * f);
        }
    }

    public static class DamageRelations {
        @SerializedName("double_damage_from")
        private List<NamedApiResource> doubleDamageFrom;
        @SerializedName("half_damage_from")
        private List<NamedApiResource> halfDamageFrom;
        @SerializedName("no_damage_from")
        private List<NamedApiResource> noDamageFrom;

        public void setDoubleDamageFrom(List<NamedApiResource> doubleDamageFrom) {
            this.doubleDamageFrom = doubleDamageFrom;
        }

        public void setHalfDamageFrom(List<NamedApiResource> halfDamageFrom) {
            this.halfDamageFrom = halfDamageFrom;
        }

        public void setNoDamageFrom(List<NamedApiResource> noDamageFrom) {
            this.noDamageFrom = noDamageFrom;
        }
    }
}
