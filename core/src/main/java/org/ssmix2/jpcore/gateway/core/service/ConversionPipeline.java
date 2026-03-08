package org.ssmix2.jpcore.gateway.core.service;

import org.hl7.fhir.r4.model.Bundle;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDataSet;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalModelAssembler;
import org.ssmix2.jpcore.gateway.core.mapping.FhirBundleMapper;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;
import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Dataset;
import org.ssmix2.jpcore.gateway.core.parser.Ssmix2InputSource;
import org.ssmix2.jpcore.gateway.core.parser.Ssmix2Parser;
import org.ssmix2.jpcore.gateway.core.validation.MappedResourceValidationService;
import org.ssmix2.jpcore.gateway.core.validation.ValidationSummary;

import java.util.UUID;

public class ConversionPipeline {

    private final Ssmix2Parser ssmix2Parser;
    private final CanonicalModelAssembler canonicalModelAssembler;
    private final FhirBundleMapper fhirBundleMapper;
    private final MappedResourceValidationService mappedResourceValidationService;

    public ConversionPipeline(
            Ssmix2Parser ssmix2Parser,
            CanonicalModelAssembler canonicalModelAssembler,
            FhirBundleMapper fhirBundleMapper,
            MappedResourceValidationService mappedResourceValidationService
    ) {
        this.ssmix2Parser = ssmix2Parser;
        this.canonicalModelAssembler = canonicalModelAssembler;
        this.fhirBundleMapper = fhirBundleMapper;
        this.mappedResourceValidationService = mappedResourceValidationService;
    }

    public ConversionResult convert(ConversionRequest request) {
        ParsedSsmix2Dataset parsedDataset = ssmix2Parser.parse(new Ssmix2InputSource(request.sourcePath(), request.facilityId()));
        CanonicalDataSet canonicalDataSet = canonicalModelAssembler.assemble(parsedDataset);
        String bundleId = request.bundleId() == null || request.bundleId().isBlank()
                ? "bundle-" + UUID.randomUUID()
                : request.bundleId();
        MapperResult<Bundle> mappingResult = fhirBundleMapper.map(canonicalDataSet, bundleId);
        ValidationSummary validationSummary = mappedResourceValidationService.validate(mappingResult);
        return new ConversionResult(mappingResult, validationSummary);
    }
}
