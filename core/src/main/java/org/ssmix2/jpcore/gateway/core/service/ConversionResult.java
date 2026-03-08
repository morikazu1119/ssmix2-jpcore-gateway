package org.ssmix2.jpcore.gateway.core.service;

import org.hl7.fhir.r4.model.Bundle;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;
import org.ssmix2.jpcore.gateway.core.validation.ValidationSummary;

import java.util.List;

public record ConversionResult(MapperResult<Bundle> mappingResult, ValidationSummary validationSummary) {
    public Bundle bundle() {
        return mappingResult.resource();
    }

    public List<org.ssmix2.jpcore.gateway.core.mapping.MappingIssue> mappingIssues() {
        return mappingResult.issues();
    }
}
