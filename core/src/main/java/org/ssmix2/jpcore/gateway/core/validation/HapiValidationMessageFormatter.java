package org.ssmix2.jpcore.gateway.core.validation;

import ca.uhn.fhir.validation.SingleValidationMessage;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Resource;

public class HapiValidationMessageFormatter {

    public ValidationIssue format(SingleValidationMessage message, IBaseResource resource) {
        String severity = message.getSeverity() == null ? "UNKNOWN" : message.getSeverity().name();
        String location = blankToFallback(message.getLocationString(), resource == null ? null : resource.fhirType());
        String text = blankToFallback(message.getMessage(), "Validation issue");
        String profile = detectProfile(resource);
        return new ValidationIssue(severity, location, text, profile);
    }

    public ValidationIssue formatFailure(IBaseResource resource, Throwable throwable) {
        String location = resource == null ? "unknown" : resource.fhirType();
        String message = throwable == null ? "Validation execution failed." : "Validation execution failed: " + throwable.getMessage();
        return new ValidationIssue("ERROR", location, message, detectProfile(resource));
    }

    private String detectProfile(IBaseResource resource) {
        if (!(resource instanceof Resource domainResource)) {
            return null;
        }

        Meta meta = domainResource.getMeta();
        if (meta == null || meta.getProfile().isEmpty()) {
            return null;
        }
        return meta.getProfile().getFirst().getValue();
    }

    private String blankToFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}

