package es.upm.mssde.pokedex.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PokemonDetailModelsTest {

    private static NamedApiResource resource(String name) {
        NamedApiResource resource = new NamedApiResource();
        resource.setName(name);
        return resource;
    }

    private static List<NamedApiResource> resources(String... names) {
        NamedApiResource[] list = new NamedApiResource[names.length];
        for (int i = 0; i < names.length; i++) {
            list[i] = resource(names[i]);
        }
        return Arrays.asList(list);
    }

    private static TypeDetail type(List<NamedApiResource> doubleFrom, List<NamedApiResource> halfFrom,
                                   List<NamedApiResource> noFrom) {
        TypeDetail.DamageRelations relations = new TypeDetail.DamageRelations();
        relations.setDoubleDamageFrom(doubleFrom);
        relations.setHalfDamageFrom(halfFrom);
        relations.setNoDamageFrom(noFrom);
        TypeDetail type = new TypeDetail();
        type.setDamageRelations(relations);
        return type;
    }

    @Test
    public void defensiveMultipliers_combinesDualTypes() {
        TypeDetail grass = type(resources("fire", "ice", "flying"), resources("water", "ground", "electric"), resources());
        TypeDetail flying = type(resources("ice", "rock", "electric"), resources("grass"), resources("ground"));

        Map<String, Double> multipliers = TypeDetail.defensiveMultipliers(Arrays.asList(grass, flying));

        assertEquals(Double.valueOf(4.0), multipliers.get("ice"));
        assertEquals(Double.valueOf(2.0), multipliers.get("fire"));
        assertEquals(Double.valueOf(0.5), multipliers.get("water"));
        assertEquals(Double.valueOf(0.0), multipliers.get("ground"));
        assertFalse("Neutral matchups are omitted", multipliers.containsKey("electric"));
        assertEquals("ice", multipliers.keySet().iterator().next());
    }

    @Test
    public void countMatchups_countsWeakAndResistantMembers() {
        Map<String, Double> charizard = new java.util.HashMap<>();
        charizard.put("rock", 4.0);
        charizard.put("water", 2.0);
        charizard.put("ground", 0.0);
        Map<String, Double> blastoise = new java.util.HashMap<>();
        blastoise.put("electric", 2.0);
        blastoise.put("water", 0.5);
        blastoise.put("fire", 0.5);

        Map<String, TeamCoverage.Matchup> matchups =
                TeamCoverage.countMatchups(Arrays.asList(charizard, blastoise));

        assertEquals(1, matchup(matchups, "rock").getWeak());
        assertEquals(1, matchup(matchups, "water").getWeak());
        assertEquals(1, matchup(matchups, "water").getResist());
        assertEquals("Immunities count as resistances", 1, matchup(matchups, "ground").getResist());
        assertEquals(0, matchup(matchups, "fire").getWeak());
    }

    private static TeamCoverage.Matchup matchup(Map<String, TeamCoverage.Matchup> matchups, String type) {
        TeamCoverage.Matchup matchup = matchups.get(type);
        assertNotNull("Missing matchup for " + type, matchup);
        return matchup;
    }

    @Test
    public void describe_levelUp() {
        EvolutionChain.EvolutionDetail detail = new EvolutionChain.EvolutionDetail();
        detail.setTrigger(resource("level-up"));
        detail.setMinLevel(16);
        assertEquals("Lv. 16", detail.describe());
    }

    @Test
    public void describe_useItem() {
        EvolutionChain.EvolutionDetail detail = new EvolutionChain.EvolutionDetail();
        detail.setTrigger(resource("use-item"));
        detail.setItem(resource("water-stone"));
        assertEquals("Use Water Stone", detail.describe());
    }

    @Test
    public void describe_friendshipAtTimeOfDay() {
        EvolutionChain.EvolutionDetail detail = new EvolutionChain.EvolutionDetail();
        detail.setTrigger(resource("level-up"));
        detail.setMinHappiness(160);
        detail.setTimeOfDay("night");
        assertEquals("Level up, high friendship, during the night", detail.describe());
    }

    @Test
    public void primaryDetail_prefersDefaultEntry() {
        EvolutionChain.EvolutionDetail legacy = new EvolutionChain.EvolutionDetail();
        legacy.setTrigger(resource("level-up"));
        EvolutionChain.EvolutionDetail current = new EvolutionChain.EvolutionDetail();
        current.setTrigger(resource("use-item"));
        current.setItem(resource("leaf-stone"));
        current.setIsDefault(true);

        EvolutionChain.ChainLink link = new EvolutionChain.ChainLink();
        link.setEvolutionDetails(Arrays.asList(legacy, current));

        assertEquals("Use Leaf Stone", link.getPrimaryDetail().describe());
    }

    @Test
    public void getStages_groupsBranchesByDepth() {
        EvolutionChain.ChainLink vaporeon = new EvolutionChain.ChainLink();
        vaporeon.setSpecies(resource("vaporeon"));
        EvolutionChain.ChainLink jolteon = new EvolutionChain.ChainLink();
        jolteon.setSpecies(resource("jolteon"));
        EvolutionChain.ChainLink eevee = new EvolutionChain.ChainLink();
        eevee.setSpecies(resource("eevee"));
        eevee.setEvolvesTo(Arrays.asList(vaporeon, jolteon));

        EvolutionChain chain = new EvolutionChain();
        chain.setChain(eevee);

        List<List<EvolutionChain.ChainLink>> stages = chain.getStages();
        assertEquals(2, stages.size());
        assertEquals(Collections.singletonList(eevee), stages.get(0));
        assertEquals(Arrays.asList(vaporeon, jolteon), stages.get(1));
    }

    @Test
    public void getStages_emptyChain() {
        assertEquals(0, new EvolutionChain().getStages().size());
    }

    @Test
    public void namedApiResource_parsesIdAndPrettifies() {
        NamedApiResource resource = new NamedApiResource();
        resource.setUrl("https://pokeapi.co/api/v2/pokemon-species/133/");
        assertEquals(Integer.valueOf(133), resource.getId());
        assertEquals("Charizard Mega X", NamedApiResource.prettify("charizard-mega-x"));
        assertEquals("", NamedApiResource.prettify(null));
    }

    @Test
    public void namedApiResource_nullUrlHasNoId() {
        assertNull(new NamedApiResource().getId());
    }
}
