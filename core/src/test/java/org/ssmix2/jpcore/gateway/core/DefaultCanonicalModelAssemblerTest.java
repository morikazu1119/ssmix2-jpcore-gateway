package org.ssmix2.jpcore.gateway.core;

import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalCode;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDataSet;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;
import org.ssmix2.jpcore.gateway.core.canonical.DefaultCanonicalModelAssembler;
import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Dataset;
import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Record;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultCanonicalModelAssemblerTest {

    @Test
    void assemblesCanonicalDataSetFromParsedRecords() {
        ParsedSsmix2Dataset dataset = new ParsedSsmix2Dataset(
                "facility-a",
                List.of(
                        new ParsedSsmix2Record(
                                CanonicalResourceType.PATIENT,
                                "PAT-001",
                                Map.of(
                                        "id", "PAT-001",
                                        "familyName", "Yamada",
                                        "givenName", "Hanako",
                                        "gender", "female",
                                        "birthDate", "1980-01-01",
                                        "patientUpdatedAt", "2026-01-01T08:30:00+09:00",
                                        "sourceSystem", "ssmix2-standardized"
                                ),
                                "patient raw text",
                                Path.of("patient/PAT-001.txt")
                        ),
                        new ParsedSsmix2Record(
                                CanonicalResourceType.ENCOUNTER,
                                "ENC-001",
                                Map.of(
                                        "id", "ENC-001",
                                        "status", "finished",
                                        "classCode", "AMB",
                                        "startDateTime", "2026-01-01T09:00:00+09:00",
                                        "standardClassCode", "AMB",
                                        "standardClassCodeSystem", "http://terminology.hl7.org/CodeSystem/v3-ActCode"
                                ),
                                "encounter raw text",
                                Path.of("encounter/ENC-001.txt")
                        ),
                        new ParsedSsmix2Record(
                                CanonicalResourceType.OBSERVATION,
                                "OBS-001",
                                Map.of(
                                        "id", "OBS-001",
                                        "encounterId", "ENC-001",
                                        "status", "final",
                                        "code", "body-weight",
                                        "value", "60kg"
                                ),
                                "observation raw text",
                                Path.of("observation/OBS-001.txt")
                        )
                )
        );

        CanonicalDataSet canonicalDataSet = new DefaultCanonicalModelAssembler().assemble(dataset);

        assertEquals("PAT-001", canonicalDataSet.patient().patientId());
        assertEquals(LocalDate.parse("1980-01-01"), canonicalDataSet.patient().birthDate());
        assertEquals(OffsetDateTime.parse("2026-01-01T08:30:00+09:00"), canonicalDataSet.patient().occurredAt());
        assertEquals("ssmix2-standardized", canonicalDataSet.patient().sourceSystem());
        assertEquals("patient raw text", canonicalDataSet.patient().rawText());
        assertEquals(1, canonicalDataSet.encounters().size());
        assertEquals("PAT-001", canonicalDataSet.encounters().getFirst().patientId());
        assertEquals(List.of(new CanonicalCode("local:classCode", "AMB", "AMB")), canonicalDataSet.encounters().getFirst().localCodes());
        assertEquals(
                List.of(new CanonicalCode("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB", "AMB")),
                canonicalDataSet.encounters().getFirst().standardCodes()
        );
        assertTrue(canonicalDataSet.observations().getFirst().missingFields().contains("effectiveDateTime"));
        assertTrue(canonicalDataSet.observations().getFirst().unresolvedMappings().contains("No standard mapping resolved for code=body-weight"));
    }

    @Test
    void canonicalRecordsExposeSharedMetadataContract() {
        ParsedSsmix2Dataset dataset = new ParsedSsmix2Dataset(
                "facility-a",
                List.of(
                        new ParsedSsmix2Record(
                                CanonicalResourceType.PATIENT,
                                "PAT-001",
                                Map.of(
                                        "id", "PAT-001",
                                        "familyName", "Yamada",
                                        "givenName", "Hanako"
                                ),
                                "patient raw text",
                                Path.of("patient/PAT-001.txt")
                        ),
                        new ParsedSsmix2Record(
                                CanonicalResourceType.MEDICATION_REQUEST,
                                "MED-001",
                                Map.of(
                                        "id", "MED-001",
                                        "medicationCode", "amlodipine-5mg",
                                        "medicationText", "Amlodipine 5mg tablet"
                                ),
                                "medication raw text",
                                Path.of("medication-request/MED-001.txt")
                        ),
                        new ParsedSsmix2Record(
                                CanonicalResourceType.DOCUMENT_REFERENCE,
                                "DOC-001",
                                Map.of(
                                        "id", "DOC-001",
                                        "title", "Discharge Summary",
                                        "typeCode", "discharge-summary"
                                ),
                                "document raw text",
                                Path.of("document-reference/DOC-001.txt")
                        )
                )
        );

        CanonicalDataSet canonicalDataSet = new DefaultCanonicalModelAssembler().assemble(dataset);

        assertEquals("MED-001", canonicalDataSet.medicationOrders().getFirst().sourceMessageId());
        assertEquals("facility-a", canonicalDataSet.medicationOrders().getFirst().sourceSystem());
        assertEquals("Amlodipine 5mg tablet", canonicalDataSet.medicationOrders().getFirst().medicationText());
        assertTrue(canonicalDataSet.medicationOrders().getFirst().missingFields().contains("orderedAt"));
        assertEquals("DOC-001", canonicalDataSet.documents().getFirst().documentId());
        assertEquals("document raw text", canonicalDataSet.documents().getFirst().rawText());
    }
}
