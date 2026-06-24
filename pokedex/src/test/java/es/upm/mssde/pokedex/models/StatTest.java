package es.upm.mssde.pokedex.models;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class StatTest {

    private Stat stat;

    @Before
    public void setUp() {
        stat = new Stat();
    }

    @Test
    public void setAndGetBaseStat() {
        stat.setBaseStat(45);
        assertEquals(Integer.valueOf(45), stat.getBaseStat());
    }

    @Test
    public void setAndGetEffort() {
        stat.setEffort(1);
        assertEquals(Integer.valueOf(1), stat.getEffort());
    }

    @Test
    public void setAndGetStatName() {
        StatName statName = new StatName();
        statName.setName("hp");
        stat.setStat(statName);
        assertEquals("hp", stat.getStat().getName());
    }
}