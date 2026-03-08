package org.ssmix2.jpcore.gateway.core;

import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDataSet;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalModelAssembler;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalPatient;
import org.ssmix2.jpcore.gateway.core.mapping.FhirBundleMapper;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;
import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Dataset;
import org.ssmix2.jpcore.gateway.core.parser.Ssmix2Parser;
import org.ssmix2.jpcore.gateway.core.service.ConversionPipeline;
import org.ssmix2.jpcore.gateway.core.service.ConversionRequest;
import org.ssmix2.jpcore.gateway.core.service.ConversionResult;
import org.ssmix2.jpcore.gateway.core.validation.MappedResourceValidationService;
import org.ssmix2.jpcore.gateway.core.validation.ValidationSummary;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversionPipelineTest {

    @Test
    void convertsAndValidatesBundle() {
        Ssmix2Parser parser = input -> new ParsedSsmix2Dataset("facility-a", List.of());
        CanonicalModelAssembler assembler = parsed -> new CanonicalDataSet(
                parsed.facilityId(),
                new CanonicalPatient(
                        "PAT-001",
                        "Yamada",
                        "Hanako",
                        "female",
                        LocalDate.parse("1980-01-01"),
                        "msg-1",
                        "ssmix2",
                        OffsetDateTime.parse("2026-01-01T09:00:00+09:00"),
                        List.of(),
                        List.of(),
                        "raw",
                        List.of(),
                        List.of()
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
        FhirBundleMapper mapper = (canonical, bundleId) -> {
            Bundle bundle = new Bundle();
            bundle.setId(bundleId);
            return new MapperResult<>(bundle, List.of());
        };
        MappedResourceValidationService validator = resource -> new ValidationSummary(true, List.of());

        ConversionPipeline pipeline = new ConversionPipeline(parser, assembler, mapper, validator);
        ConversionResult result = pipeline.convert(new ConversionRequest("bundle-001", "facility-a", Path.of("/tmp/input")));

        assertEquals("bundle-001", result.bundle().getId());
        assertTrue(result.validationSummary().valid());
        assertEquals(0, result.mappingIssues().size());
    }
}
