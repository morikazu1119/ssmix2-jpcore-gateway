package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.Observation;
import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpCoreObservationMapperTest {

    @Test
    void mapsCanonicalObservationToJpCoreObservation() {
        JpCoreObservationMapper mapper = new JpCoreObservationMapper(JpCoreMapperTestSupport.CATALOG, new NoOpJpClinsExtensionSupport());
        MapperResult<Observation> result = mapper.map(JpCoreMapperTestSupport.sampleObservation());
        Observation fixture = JpCoreMapperTestSupport.parseFixture("examples/resources/observation.json", Observation.class);

        assertEquals("OBS-001", result.resource().getIdElement().getIdPart());
        assertEquals("60kg", result.resource().getValue().primitiveValue());
        assertEquals(2, result.resource().getCode().getCoding().size());
        assertEquals(fixture.getCode().getCodingFirstRep().getCode(), result.resource().getCode().getCodingFirstRep().getCode());
        assertTrue(result.unresolvedFields().stream().anyMatch(issue -> issue.message().contains("UCUM normalization")));
    }
}

