package org.ssmix2.jpcore.gateway.core;

import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.validation.HapiFhirValidationService;
import org.ssmix2.jpcore.gateway.core.validation.HapiValidationMessageFormatter;
import org.ssmix2.jpcore.gateway.core.validation.ValidationSummary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HapiFhirValidationServiceTest {

    @Test
    void returnsStructuredFailureWhenValidatorExecutionThrows() {
        HapiFhirValidationService service = new HapiFhirValidationService(
                resource -> {
                    throw new IllegalStateException("validator unavailable");
                },
                true,
                new HapiValidationMessageFormatter()
        );

        Patient patient = new Patient();
        patient.setMeta(new Meta().addProfile("http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient"));

        ValidationSummary summary = service.validate(patient);

        assertFalse(summary.valid());
        assertEquals(1, summary.issues().size());
        assertEquals("ERROR", summary.issues().getFirst().severity());
        assertEquals("Patient", summary.issues().getFirst().location());
        assertTrue(summary.issues().getFirst().message().contains("validator unavailable"));
        assertEquals("http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient", summary.issues().getFirst().profile());
    }
}

