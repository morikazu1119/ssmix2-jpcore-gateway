package org.ssmix2.jpcore.gateway.core.validation;

public record ValidationIssue(String severity, String location, String message, String profile) {
}
