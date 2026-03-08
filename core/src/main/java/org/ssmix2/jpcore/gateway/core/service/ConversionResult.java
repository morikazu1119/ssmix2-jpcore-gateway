package org.ssmix2.jpcore.gateway.core.service;

import org.hl7.fhir.r4.model.Bundle;
import org.ssmix2.jpcore.gateway.core.validation.ValidationSummary;

public record ConversionResult(Bundle bundle, ValidationSummary validationSummary) {
}

