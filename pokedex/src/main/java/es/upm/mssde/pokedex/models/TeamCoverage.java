package es.upm.mssde.pokedex.models;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class TeamCoverage {

    public static final class Matchup {
        private int weak;
        private int resist;

        /** Members that take more than 1× damage from this attacking type. */
        public int getWeak() {
            return weak;
        }

        /** Members that take less than 1× damage (including immunities) from this attacking type. */
        public int getResist() {
            return resist;
        }
    }

    private TeamCoverage() {
    }

    /**
     * Counts, for each attacking type, how many members are weak to it and how many resist it.
     * Each map is one member's result from {@link TypeDetail#defensiveMultipliers}.
     */
    public static Map<String, Matchup> countMatchups(List<Map<String, Double>> memberMultipliers) {
        Map<String, Matchup> matchups = new TreeMap<>();
        for (Map<String, Double> multipliers : memberMultipliers) {
            for (Map.Entry<String, Double> entry : multipliers.entrySet()) {
                Matchup matchup = matchups.get(entry.getKey());
                if (matchup == null) {
                    matchup = new Matchup();
                    matchups.put(entry.getKey(), matchup);
                }
                if (entry.getValue() > 1) {
                    matchup.weak++;
                } else if (entry.getValue() < 1) {
                    matchup.resist++;
                }
            }
        }
        return matchups;
    }
}
