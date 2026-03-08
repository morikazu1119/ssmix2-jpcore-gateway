package org.ssmix2.jpcore.gateway.core.mapping;

public record MappingIssue(
        MappingIssueSeverity severity,
        String field,
        String message
) {
}

