package org.ssmix2.jpcore.gateway.core.validation;

import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseResource;

@FunctionalInterface
public interface HapiValidationExecutor {
    ValidationResult validate(IBaseResource resource);
}

