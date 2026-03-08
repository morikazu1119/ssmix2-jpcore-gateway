package org.ssmix2.jpcore.gateway.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;
import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Dataset;
import org.ssmix2.jpcore.gateway.core.parser.SimpleKeyValueSsmix2Parser;
import org.ssmix2.jpcore.gateway.core.parser.Ssmix2InputSource;
import org.ssmix2.jpcore.gateway.core.parser.UnsupportedSsmix2InputException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleKeyValueSsmix2ParserTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesSupportedFixtureStructure() throws IOException {
        Path patientDirectory = Files.createDirectories(tempDir.resolve("patient"));
        Files.writeString(patientDirectory.resolve("PAT-001.txt"), """
                id=PAT-001
                familyName=Yamada
                givenName=Hanako
                """);

        SimpleKeyValueSsmix2Parser parser = new SimpleKeyValueSsmix2Parser();
        ParsedSsmix2Dataset dataset = parser.parse(new Ssmix2InputSource(tempDir, "facility-a"));

        assertEquals("facility-a", dataset.facilityId());
        assertEquals(1, dataset.records().size());
        assertEquals(CanonicalResourceType.PATIENT, dataset.records().getFirst().resourceType());
        assertEquals("PAT-001", dataset.records().getFirst().recordId());
    }

    @Test
    void failsLoudlyOnUnsupportedDirectories() throws IOException {
        Files.createDirectories(tempDir.resolve("allergies"));

        SimpleKeyValueSsmix2Parser parser = new SimpleKeyValueSsmix2Parser();

        assertThrows(
                UnsupportedSsmix2InputException.class,
                () -> parser.parse(new Ssmix2InputSource(tempDir, "facility-a"))
        );
    }
}

