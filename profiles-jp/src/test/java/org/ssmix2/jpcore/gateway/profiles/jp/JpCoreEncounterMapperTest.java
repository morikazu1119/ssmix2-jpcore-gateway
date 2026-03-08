package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.Encounter;
import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpCoreEncounterMapperTest {

    @Test
    void mapsCanonicalEncounterToJpCoreEncounter() {
        JpCoreEncounterMapper mapper = new JpCoreEncounterMapper(JpCoreMapperTestSupport.CATALOG, new NoOpJpClinsExtensionSupport());
        MapperResult<Encounter> result = mapper.map(JpCoreMapperTestSupport.sampleEncounter());
        Encounter fixture = JpCoreMapperTestSupport.parseFixture("examples/resources/encounter.json", Encounter.class);

        assertEquals("ENC-001", result.resource().getIdElement().getIdPart());
        assertEquals("Patient/PAT-001", result.resource().getSubject().getReference());
        assertEquals("AMB", result.resource().getClass_().getCode());
        assertEquals(fixture.getClass_().getSystem(), result.resource().getClass_().getSystem());
        assertTrue(result.warnings().stream().anyMatch(issue -> issue.field().equals("classCode")));
    }
}

