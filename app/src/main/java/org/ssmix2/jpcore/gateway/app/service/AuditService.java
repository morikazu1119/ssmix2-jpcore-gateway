package org.ssmix2.jpcore.gateway.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditService.class);

    public void logIngest(String bundleId, String facilityId, String sourcePath) {
        LOGGER.info("audit ingest bundleId={} facilityId={} sourcePath={}", bundleId, facilityId, sourcePath);
    }

    public void logBundleFetch(String bundleId) {
        LOGGER.info("audit fetch bundleId={}", bundleId);
    }
}

