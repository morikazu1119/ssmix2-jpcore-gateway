package org.ssmix2.jpcore.gateway.core.service;

import java.nio.file.Path;

public record ConversionRequest(String bundleId, String facilityId, Path sourcePath) {
}

