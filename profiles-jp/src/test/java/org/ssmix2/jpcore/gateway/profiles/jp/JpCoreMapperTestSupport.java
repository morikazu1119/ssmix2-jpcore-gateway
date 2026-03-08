package org.ssmix2.jpcore.gateway.profiles.jp;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalCode;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDataSet;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDocument;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalEncounter;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalMedicationOrder;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalObservation;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalPatient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

final class JpCoreMapperTestSupport {

    static final FhirContext FHIR_CONTEXT = FhirContext.forR4();
    static final IParser JSON_PARSER = FHIR_CONTEXT.newJsonParser().setPrettyPrint(true);
    static final JpCoreMappingDefinitionCatalog CATALOG = new JpCoreMappingDefinitionCatalog();
    static final OffsetDateTime OCCURRED_AT = OffsetDateTime.parse("2026-01-15T09:00:00+09:00");

    private JpCoreMapperTestSupport() {
    }

    static CanonicalPatient samplePatient() {
        return new CanonicalPatient(
                "PAT-001",
                "Yamada",
                "Hanako",
                "female",
                LocalDate.parse("1980-01-01"),
                "MSG-PAT-001",
                "ssmix2-standardized-storage",
                OCCURRED_AT,
                List.of(new CanonicalCode("urn:local:patient", "PAT-LOCAL", "Local Patient Code")),
                List.of(),
                "patient raw",
                List.of(),
                List.of()
        );
    }

    static CanonicalEncounter sampleEncounter() {
        return new CanonicalEncounter(
                "ENC-001",
                "PAT-001",
                "finished",
                "AMB",
                OCCURRED_AT,
                "MSG-ENC-001",
                "ssmix2-standardized-storage",
                OCCURRED_AT,
                List.of(new CanonicalCode("urn:local:encounter-class", "AMB", "Ambulatory")),
                List.of(new CanonicalCode("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB", "ambulatory")),
                "encounter raw",
                List.of(),
                List.of()
        );
    }

    static CanonicalObservation sampleObservation() {
        return new CanonicalObservation(
                "OBS-001",
                "PAT-001",
                "ENC-001",
                "final",
                "60kg",
                OCCURRED_AT,
                "MSG-OBS-001",
                "ssmix2-standardized-storage",
                OCCURRED_AT,
                List.of(new CanonicalCode("urn:local:lab", "body-weight", "Body Weight")),
                List.of(new CanonicalCode("http://loinc.org", "29463-7", "Body weight")),
                "observation raw",
                List.of(),
                List.of("UCUM normalization not resolved")
        );
    }

    static CanonicalMedicationOrder sampleMedicationOrder() {
        return new CanonicalMedicationOrder(
                "MED-001",
                "PAT-001",
                "ENC-001",
                "active",
                "order",
                "Amlodipine 5mg tablet",
                OCCURRED_AT,
                "MSG-MED-001",
                "ssmix2-standardized-storage",
                OCCURRED_AT,
                List.of(new CanonicalCode("urn:local:medication", "amlodipine-5mg", "Amlodipine 5mg tablet")),
                List.of(new CanonicalCode("http://example.org/codeSystem/yj", "2171040F1024", "Amlodipine 5mg tablet")),
                "medication raw",
                List.of(),
                List.of()
        );
    }

    static CanonicalDocument sampleDocument() {
        return new CanonicalDocument(
                "DOC-001",
                "PAT-001",
                "current",
                "Discharge Summary",
                "text/plain",
                OCCURRED_AT,
                "MSG-DOC-001",
                "ssmix2-standardized-storage",
                OCCURRED_AT,
                List.of(new CanonicalCode("urn:local:document-type", "discharge-summary", "Discharge Summary")),
                List.of(new CanonicalCode("http://loinc.org", "18842-5", "Discharge summary")),
                "document raw",
                List.of(),
                List.of()
        );
    }

    static CanonicalDataSet sampleDataSet() {
        return new CanonicalDataSet(
                "facility-a",
                samplePatient(),
                List.of(sampleEncounter()),
                List.of(sampleObservation()),
                List.of(sampleMedicationOrder()),
                List.of(sampleDocument())
        );
    }

    static <T extends IBaseResource> T parseFixture(String resourcePath, Class<T> expectedType) {
        String fixtureJson = readFixture(resourcePath);
        return expectedType.cast(JSON_PARSER.parseResource(fixtureJson));
    }

    static String readFixture(String resourcePath) {
        try (InputStream inputStream = JpCoreMapperTestSupport.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Fixture not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load fixture: " + resourcePath, exception);
        }
    }
}

