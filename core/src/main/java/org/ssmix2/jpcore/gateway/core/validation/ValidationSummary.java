package org.ssmix2.jpcore.gateway.core.validation;

import java.util.ArrayList;
import java.util.List;

public record ValidationSummary(boolean valid, List<ValidationIssue> issues) {
    public ValidationSummary {
        issues = List.copyOf(issues);
    }

    public static ValidationSummary combine(List<ValidationSummary> summaries) {
        List<ValidationIssue> combinedIssues = new ArrayList<>();
        boolean allValid = true;
        for (ValidationSummary summary : summaries) {
            combinedIssues.addAll(summary.issues());
            if (!summary.valid()) {
                allValid = false;
            }
        }
        return new ValidationSummary(allValid, combinedIssues);
    }
}
