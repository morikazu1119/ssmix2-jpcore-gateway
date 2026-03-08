package org.ssmix2.jpcore.gateway.profiles.jp;

import java.util.List;

public record JpCoreMappingDefinition(
        String resourceType,
        String profileUrl,
        String description,
        List<String> requiredFields
) {
}

