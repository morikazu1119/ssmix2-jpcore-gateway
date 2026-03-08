package org.ssmix2.jpcore.gateway.app.api;

import jakarta.validation.constraints.NotBlank;

public record Ssmix2IngestRequest(
        String bundleId,
        String facilityId,
        @NotBlank String sourcePath
) {
}

