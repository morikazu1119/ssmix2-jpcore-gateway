package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.DomainResource;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResource;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

public class NoOpJpClinsExtensionSupport implements JpClinsExtensionSupport {

    @Override
    public void apply(CanonicalResource source, DomainResource target, MapperResult.Builder<?> resultBuilder) {
        resultBuilder.warning("jpClins", "JP-CLINS specific extensions are not implemented yet.");
    }
}

