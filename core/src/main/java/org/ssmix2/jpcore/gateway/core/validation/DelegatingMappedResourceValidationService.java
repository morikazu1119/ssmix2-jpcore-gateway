package org.ssmix2.jpcore.gateway.core.validation;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

public class DelegatingMappedResourceValidationService implements MappedResourceValidationService {

    private final FhirValidationService fhirValidationService;

    public DelegatingMappedResourceValidationService(FhirValidationService fhirValidationService) {
        this.fhirValidationService = fhirValidationService;
    }

    @Override
    public ValidationSummary validate(MapperResult<? extends IBaseResource> mapperResult) {
        return fhirValidationService.validate(mapperResult.resource());
    }
}
