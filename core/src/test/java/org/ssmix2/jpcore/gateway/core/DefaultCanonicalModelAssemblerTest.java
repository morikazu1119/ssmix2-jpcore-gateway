package org.ssmix2.jpcore.gateway.core;

import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDataSet;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;
import org.ssmix2.jpcore.gateway.core.canonical.DefaultCanonicalModelAssembler;
import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Dataset;
import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Record;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                                        "birthDate", "1980-01-01"
                                ),
                                Path.of("patient/PAT-001.txt")
                        ),
                        new ParsedSsmix2Record(
                                CanonicalResourceType.ENCOUNTER,
                                "ENC-001",
                                Map.of(
                                        "id", "ENC-001",
                                        "status", "finished",
                                        "classCode", "AMB",
                                        "startDateTime", "2026-01-01T09:00:00+09:00"
                                ),
                                Path.of("encounter/ENC-001.txt")
                        )
                )
        );

        CanonicalDataSet canonicalDataSet = new DefaultCanonicalModelAssembler().assemble(dataset);

        assertEquals("PAT-001", canonicalDataSet.patient().id());
        assertEquals(1, canonicalDataSet.encounters().size());
        assertEquals("PAT-001", canonicalDataSet.encounters().getFirst().patientId());
    }
}

