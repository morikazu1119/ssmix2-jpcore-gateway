package org.ssmix2.jpcore.gateway.core.mapping;

import org.hl7.fhir.r4.model.Bundle;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDataSet;

public interface FhirBundleMapper {
    MapperResult<Bundle> map(CanonicalDataSet canonicalDataSet, String bundleId);
}
