package org.ssmix2.jpcore.gateway.core.validation;

import org.hl7.fhir.instance.model.api.IBaseResource;

public interface FhirValidationService {
    ValidationSummary validate(IBaseResource resource);
}

