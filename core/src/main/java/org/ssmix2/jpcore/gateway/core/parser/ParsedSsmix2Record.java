package org.ssmix2.jpcore.gateway.core.parser;

import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;

import java.nio.file.Path;
import java.util.Map;

public record ParsedSsmix2Record(
        CanonicalResourceType resourceType,
        String recordId,
        Map<String, String> attributes,
        Path sourceFile
) {
}

