package org.ssmix2.jpcore.gateway.core.validation;

import java.util.List;

public record ValidationSummary(boolean valid, List<ValidationIssue> issues) {
}

