package org.ssmix2.jpcore.gateway.app.store;

import org.hl7.fhir.r4.model.Bundle;

import java.util.Optional;

public interface BundleStore {
    void save(String bundleId, Bundle bundle);

    Optional<Bundle> findById(String bundleId);
}

