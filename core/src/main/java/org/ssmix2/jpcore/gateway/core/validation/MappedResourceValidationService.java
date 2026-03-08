package org.ssmix2.jpcore.gateway.core.validation;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

public interface MappedResourceValidationService {
    ValidationSummary validate(MapperResult<? extends IBaseResource> mapperResult);
}

