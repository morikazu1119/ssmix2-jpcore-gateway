package org.ssmix2.jpcore.gateway.app.store;

import org.hl7.fhir.r4.model.Bundle;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBundleStore implements BundleStore {

    private final Map<String, Bundle> bundles = new ConcurrentHashMap<>();

    @Override
    public void save(String bundleId, Bundle bundle) {
        bundles.put(bundleId, bundle);
    }

    @Override
    public Optional<Bundle> findById(String bundleId) {
        return Optional.ofNullable(bundles.get(bundleId));
    }
}

