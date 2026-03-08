package org.ssmix2.jpcore.gateway.core;

import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalCode;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDocument;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalEncounter;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalMedicationOrder;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalObservation;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalPatient;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResource;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CanonicalModelContractTest {

    @Test
    void canonicalModelsRemainFhirIndependentAndShareCommonMetadata() {
        OffsetDateTime occurredAt = OffsetDateTime.parse("2026-01-01T09:00:00+09:00");
        List<CanonicalCode> localCodes = List.of(new CanonicalCode("urn:local", "ABC", "Example"));
        List<CanonicalCode> standardCodes = List.of(new CanonicalCode("http://loinc.org", "1234-5", "Example"));
        List<String> missingFields = List.of("occurredAt");
        List<String> unresolvedMappings = List.of("No standard mapping resolved for local code");

        List<CanonicalResource> resources = List.of(
                new CanonicalPatient("PAT-001", "Yamada", "Hanako", "female", LocalDate.parse("1980-01-01"), "msg-1", "ssmix2", occurredAt, localCodes, standardCodes, "raw", missingFields, unresolvedMappings),
                new CanonicalEncounter("ENC-001", "PAT-001", "finished", "AMB", occurredAt, "msg-2", "ssmix2", occurredAt, localCodes, standardCodes, "raw", missingFields, unresolvedMappings),
                new CanonicalObservation("OBS-001", "PAT-001", "ENC-001", "final", "60kg", occurredAt, "msg-3", "ssmix2", occurredAt, localCodes, standardCodes, "raw", missingFields, unresolvedMappings),
                new CanonicalMedicationOrder("MED-001", "PAT-001", "ENC-001", "active", "order", "Amlodipine", occurredAt, "msg-4", "ssmix2", occurredAt, localCodes, standardCodes, "raw", missingFields, unresolvedMappings),
                new CanonicalDocument("DOC-001", "PAT-001", "current", "Discharge Summary", "text/plain", occurredAt, "msg-5", "ssmix2", occurredAt, localCodes, standardCodes, "raw", missingFields, unresolvedMappings)
        );

        for (CanonicalResource resource : resources) {
            assertEquals("ssmix2", resource.sourceSystem());
            assertEquals(occurredAt, resource.occurredAt());
            assertEquals(localCodes, resource.localCodes());
            assertEquals(standardCodes, resource.standardCodes());
            assertEquals("raw", resource.rawText());
            assertEquals(missingFields, resource.missingFields());
            assertEquals(unresolvedMappings, resource.unresolvedMappings());
        }

        assertInstanceOf(CanonicalPatient.class, resources.get(0));
        assertInstanceOf(CanonicalDocument.class, resources.get(4));
    }
}
