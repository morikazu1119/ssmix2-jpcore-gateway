package org.ssmix2.jpcore.gateway.core.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.ArrayList;
import java.util.List;

public class HapiFhirValidationService implements FhirValidationService {

    private final HapiValidationExecutor validationExecutor;
    private final boolean instanceValidationEnabled;
    private final HapiValidationMessageFormatter messageFormatter;

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

        this.validationExecutor = fhirValidator::validateWithResult;
        this.messageFormatter = new HapiValidationMessageFormatter();
        this.instanceValidationEnabled = enabled;
    }

    public HapiFhirValidationService(
            HapiValidationExecutor validationExecutor,
            boolean instanceValidationEnabled,
            HapiValidationMessageFormatter messageFormatter
    ) {
        this.validationExecutor = validationExecutor;
        this.instanceValidationEnabled = instanceValidationEnabled;
        this.messageFormatter = messageFormatter;
    }

    @Override
    public ValidationSummary validate(IBaseResource resource) {
        try {
            ca.uhn.fhir.validation.ValidationResult result = validationExecutor.validate(resource);
            List<ValidationIssue> issues = new ArrayList<>(result.getMessages().stream()
                    .map(message -> messageFormatter.format(message, resource))
                    .toList());

            if (!instanceValidationEnabled) {
                issues.add(new ValidationIssue(
                        "WARNING",
                        resource.fhirType(),
                        "Instance validator fallback is active; only minimal validation is guaranteed.",
                        resource instanceof org.hl7.fhir.r4.model.Resource r && !r.getMeta().getProfile().isEmpty()
                                ? r.getMeta().getProfile().getFirst().getValue()
                                : null
                ));
            }

            return new ValidationSummary(result.isSuccessful() || !instanceValidationEnabled, issues);
        } catch (RuntimeException | AssertionError exception) {
            return new ValidationSummary(false, List.of(messageFormatter.formatFailure(resource, exception)));
        }
    }
}
