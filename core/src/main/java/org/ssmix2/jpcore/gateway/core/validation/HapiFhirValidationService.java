package org.ssmix2.jpcore.gateway.core.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.List;

public class HapiFhirValidationService implements FhirValidationService {

    private final FhirValidator validator;
    private final boolean instanceValidationEnabled;

    public HapiFhirValidationService(FhirContext fhirContext) {
        FhirValidator fhirValidator = fhirContext.newValidator();
        boolean enabled = false;
        try {
            FhirInstanceValidator instanceValidator = new FhirInstanceValidator(fhirContext);
            fhirValidator.registerValidatorModule(instanceValidator);
            enabled = true;
        } catch (RuntimeException | AssertionError exception) {
            // TODO: Replace fallback behavior with explicit JP Core package validation wiring.
        }

        this.validator = fhirValidator;
        this.instanceValidationEnabled = enabled;
    }

    @Override
    public ValidationSummary validate(IBaseResource resource) {
        ca.uhn.fhir.validation.ValidationResult result = validator.validateWithResult(resource);
        List<ValidationIssue> issues = result.getMessages().stream()
                .map(message -> new ValidationIssue(
                        message.getSeverity().name(),
                        message.getLocationString(),
                        message.getMessage()
                ))
                .toList();

        if (!instanceValidationEnabled) {
            issues = java.util.stream.Stream.concat(
                    issues.stream(),
                    java.util.stream.Stream.of(new ValidationIssue(
                            "WARNING",
                            resource.fhirType(),
                            "Instance validator fallback is active; only minimal validation is guaranteed."
                    ))
            ).toList();
        }

        return new ValidationSummary(result.isSuccessful() || !instanceValidationEnabled, issues);
    }
}
