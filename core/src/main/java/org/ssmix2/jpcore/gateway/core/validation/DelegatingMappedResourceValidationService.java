package org.ssmix2.jpcore.gateway.core.validation;

import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import java.util.ArrayList;
import java.util.List;

public class DelegatingMappedResourceValidationService implements MappedResourceValidationService {

    private final FhirValidationService fhirValidationService;

    public DelegatingMappedResourceValidationService(FhirValidationService fhirValidationService) {
        this.fhirValidationService = fhirValidationService;
    }

    @Override
    public ValidationSummary validate(MapperResult<? extends IBaseResource> mapperResult) {
        IBaseResource resource = mapperResult.resource();
        if (!(resource instanceof Bundle bundle)) {
            return fhirValidationService.validate(resource);
        }

        List<ValidationSummary> summaries = new ArrayList<>();
        summaries.add(fhirValidationService.validate(bundle));
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            IBase entryResource = entry.getResource();
            if (entryResource instanceof IBaseResource iBaseResource) {
                summaries.add(fhirValidationService.validate(iBaseResource));
            }
        }
        return ValidationSummary.combine(summaries);
    }
}
