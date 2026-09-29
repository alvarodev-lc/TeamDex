package es.upm.mssde.pokedex.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvolutionChain {

    @SerializedName("chain")
    private ChainLink chain;

    public void setChain(ChainLink chain) {
        this.chain = chain;
    }

    /** Groups the chain by depth: stage 0 is the base form, stage 1 its evolutions, etc. */
    public List<List<ChainLink>> getStages() {
        List<List<ChainLink>> stages = new ArrayList<>();
        List<ChainLink> level = chain != null ? Collections.singletonList(chain) : Collections.emptyList();
        while (!level.isEmpty()) {
            stages.add(level);
            List<ChainLink> next = new ArrayList<>();
            for (ChainLink link : level) {
                if (link.evolvesTo != null) {
                    next.addAll(link.evolvesTo);
                }
            }
            level = next;
        }
        return stages;
    }

    public static class ChainLink {
        @SerializedName("species")
        private NamedApiResource species;
        @SerializedName("evolution_details")
        private List<EvolutionDetail> evolutionDetails;
        @SerializedName("evolves_to")
        private List<ChainLink> evolvesTo;

        public NamedApiResource getSpecies() {
            return species;
        }

        public void setSpecies(NamedApiResource species) {
            this.species = species;
        }

        public void setEvolutionDetails(List<EvolutionDetail> evolutionDetails) {
            this.evolutionDetails = evolutionDetails;
        }

        public void setEvolvesTo(List<ChainLink> evolvesTo) {
            this.evolvesTo = evolvesTo;
        }

        // The API lists one entry per game generation; is_default marks the current method.
        public EvolutionDetail getPrimaryDetail() {
            if (evolutionDetails == null || evolutionDetails.isEmpty()) {
                return null;
            }
            for (EvolutionDetail detail : evolutionDetails) {
                if (Boolean.TRUE.equals(detail.isDefault)) {
                    return detail;
                }
            }
            return evolutionDetails.get(0);
        }
    }

    // Fields without setters are filled in by Gson through reflection, which the IDE can't see.
    @SuppressWarnings("unused")
    public static class EvolutionDetail {
        @SerializedName("is_default")
        private Boolean isDefault;
        @SerializedName("trigger")
        private NamedApiResource trigger;
        @SerializedName("min_level")
        private Integer minLevel;
        @SerializedName("item")
        private NamedApiResource item;
        @SerializedName("held_item")
        private NamedApiResource heldItem;
        @SerializedName("known_move")
        private NamedApiResource knownMove;
        @SerializedName("known_move_type")
        private NamedApiResource knownMoveType;
        @SerializedName("location")
        private NamedApiResource location;
        @SerializedName("trade_species")
        private NamedApiResource tradeSpecies;
        @SerializedName("min_happiness")
        private Integer minHappiness;
        @SerializedName("min_affection")
        private Integer minAffection;
        @SerializedName("min_beauty")
        private Integer minBeauty;
        @SerializedName("time_of_day")
        private String timeOfDay;
        @SerializedName("gender")
        private Integer gender;
        @SerializedName("needs_overworld_rain")
        private Boolean needsOverworldRain;
        @SerializedName("turn_upside_down")
        private Boolean turnUpsideDown;

        public void setIsDefault(Boolean isDefault) {
            this.isDefault = isDefault;
        }

        public void setTrigger(NamedApiResource trigger) {
            this.trigger = trigger;
        }

        public void setMinLevel(Integer minLevel) {
            this.minLevel = minLevel;
        }

        public void setItem(NamedApiResource item) {
            this.item = item;
        }

        public void setMinHappiness(Integer minHappiness) {
            this.minHappiness = minHappiness;
        }

        public void setTimeOfDay(String timeOfDay) {
            this.timeOfDay = timeOfDay;
        }

        public String describe() {
            String triggerName = trigger != null ? trigger.getName() : null;
            List<String> parts = new ArrayList<>();
            if ("level-up".equals(triggerName)) {
                parts.add(minLevel != null ? "Lv. " + minLevel : "Level up");
            } else if ("use-item".equals(triggerName) && item != null) {
                parts.add("Use " + NamedApiResource.prettify(item.getName()));
            } else if ("trade".equals(triggerName)) {
                parts.add("Trade");
            } else if (triggerName != null) {
                parts.add(NamedApiResource.prettify(triggerName));
            }

            if (heldItem != null) {
                parts.add("holding " + NamedApiResource.prettify(heldItem.getName()));
            }
            if (tradeSpecies != null) {
                parts.add("for " + NamedApiResource.prettify(tradeSpecies.getName()));
            }
            if (minHappiness != null) {
                parts.add("high friendship");
            }
            if (minAffection != null) {
                parts.add("high affection");
            }
            if (minBeauty != null) {
                parts.add("high beauty");
            }
            if (knownMove != null) {
                parts.add("knowing " + NamedApiResource.prettify(knownMove.getName()));
            }
            if (knownMoveType != null) {
                parts.add("knowing a " + NamedApiResource.prettify(knownMoveType.getName()) + " move");
            }
            if (location != null) {
                parts.add("at " + NamedApiResource.prettify(location.getName()));
            }
            if (timeOfDay != null && !timeOfDay.isEmpty()) {
                parts.add("during the " + timeOfDay);
            }
            if (gender != null) {
                parts.add(gender == 1 ? "female only" : "male only");
            }
            if (Boolean.TRUE.equals(needsOverworldRain)) {
                parts.add("while raining");
            }
            if (Boolean.TRUE.equals(turnUpsideDown)) {
                parts.add("holding the console upside down");
            }

            return parts.isEmpty() ? null : String.join(", ", parts);
        }
    }
}
