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
        // Un ratio de captura de 255 (como Caterpie) debería ser 100%
        species.setCaptureRate(255f);
        assertEquals(100.0f, species.getCaptureRate(), 0.01f);

        // Un ratio de captura de 3 (como Mewtwo)
        // (3 * 100) / 255 = 1.1764... redondeado a 1.18 o 1.17 según RoundingMode
        species.setCaptureRate(3f);
        assertEquals(1.17f, species.getCaptureRate(), 0.01f);
    }

    @Test
    public void setAndGetBaseHappiness() {
        species.setBaseHappiness(70);
        assertEquals(Integer.valueOf(70), species.getBaseHappiness());
    }
}