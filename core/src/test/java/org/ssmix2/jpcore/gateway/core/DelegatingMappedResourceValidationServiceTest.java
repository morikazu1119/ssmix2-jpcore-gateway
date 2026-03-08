package org.ssmix2.jpcore.gateway.core;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;
import org.ssmix2.jpcore.gateway.core.validation.DelegatingMappedResourceValidationService;
import org.ssmix2.jpcore.gateway.core.validation.FhirValidationService;
import org.ssmix2.jpcore.gateway.core.validation.ValidationIssue;
import org.ssmix2.jpcore.gateway.core.validation.ValidationSummary;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DelegatingMappedResourceValidationServiceTest {

    @Test
    void validatesGeneratedBundleAndItsEntryResources() {
        AtomicInteger invocationCount = new AtomicInteger();
        FhirValidationService validationService = resource -> {
            invocationCount.incrementAndGet();
            if (resource instanceof Patient patient) {
                return new ValidationSummary(false, List.of(
                        new ValidationIssue("ERROR", "Patient.name[0]", "Family name is required", patient.getMeta().getProfile().getFirst().getValue())
                ));
            }
            return new ValidationSummary(true, List.of());
        };

        Patient patient = new Patient();
        patient.setId("PAT-001");
        patient.setMeta(new Meta().addProfile("http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient"));

        Bundle bundle = new Bundle();
        bundle.setId("bundle-001");
        bundle.setType(Bundle.BundleType.COLLECTION);
        bundle.addEntry().setResource(patient);

        DelegatingMappedResourceValidationService service = new DelegatingMappedResourceValidationService(validationService);
        ValidationSummary summary = service.validate(new MapperResult<>(bundle, List.of()));

        assertEquals(2, invocationCount.get());
        assertFalse(summary.valid());
        assertEquals(1, summary.issues().size());
        assertEquals("http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient", summary.issues().getFirst().profile());
    }
}
