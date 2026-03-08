package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.MedicationRequest;
import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpCoreMedicationRequestMapperTest {

    @Test
    void mapsCanonicalMedicationOrderToMedicationRequest() {
        JpCoreMedicationRequestMapper mapper = new JpCoreMedicationRequestMapper(JpCoreMapperTestSupport.CATALOG, new NoOpJpClinsExtensionSupport());
        MapperResult<MedicationRequest> result = mapper.map(JpCoreMapperTestSupport.sampleMedicationOrder());
        MedicationRequest fixture = JpCoreMapperTestSupport.parseFixture("examples/resources/medication-request.json", MedicationRequest.class);

        assertEquals("MED-001", result.resource().getIdElement().getIdPart());
        assertEquals("Patient/PAT-001", result.resource().getSubject().getReference());
        assertEquals("Amlodipine 5mg tablet", result.resource().getMedicationCodeableConcept().getText());
        assertEquals(fixture.getMedicationCodeableConcept().getCodingFirstRep().getCode(), result.resource().getMedicationCodeableConcept().getCodingFirstRep().getCode());
        assertTrue(result.warnings().stream().anyMatch(issue -> issue.field().equals("jpClins")));
    }
}

