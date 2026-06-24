package es.upm.mssde.pokedex.models;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class SpeciesTest {

    private Species species;

    @Before
    public void setUp() {
        species = new Species();
    }

    @Test
    public void getCaptureRate_shouldConvertToPercentage() {
        species.setCaptureRate(255f);
        assertEquals(100.0f, species.getCaptureRate(), 0.01f);

        species.setCaptureRate(3f);
        assertEquals(1.17f, species.getCaptureRate(), 0.01f);
    }

    @Test
    public void getCaptureRate_shouldReturnSameValueOnRepeatedCalls() {
        species.setCaptureRate(45f);
        float first = species.getCaptureRate();
        float second = species.getCaptureRate();
        assertEquals("getCaptureRate() must be idempotent", first, second, 0.001f);
    }

    @Test
    public void getCaptureRate_shouldReturnZeroForZeroCaptureRate() {
        species.setCaptureRate(0f);
        assertEquals(0.0f, species.getCaptureRate(), 0.001f);
    }

    @Test
    public void setAndGetBaseHappiness() {
        species.setBaseHappiness(70);
        assertEquals(Integer.valueOf(70), species.getBaseHappiness());
    }
}