package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpCorePatientMapperTest {

    @Test
    void mapsCanonicalPatientToJpCorePatient() {
        JpCorePatientMapper mapper = new JpCorePatientMapper(JpCoreMapperTestSupport.CATALOG, new NoOpJpClinsExtensionSupport());
        MapperResult<Patient> result = mapper.map(JpCoreMapperTestSupport.samplePatient());
        Patient fixture = JpCoreMapperTestSupport.parseFixture("examples/resources/patient.json", Patient.class);

        assertEquals("PAT-001", result.resource().getIdElement().getIdPart());
        assertEquals("Yamada", result.resource().getNameFirstRep().getFamily());
        assertEquals("Hanako", result.resource().getNameFirstRep().getGivenAsSingleString());
        assertEquals(fixture.getMeta().getProfile().getFirst().getValue(), result.resource().getMeta().getProfile().getFirst().getValue());
        assertTrue(result.warnings().stream().anyMatch(issue -> issue.field().equals("rawText")));
        assertTrue(result.warnings().stream().anyMatch(issue -> issue.field().equals("jpClins")));
    }
}

