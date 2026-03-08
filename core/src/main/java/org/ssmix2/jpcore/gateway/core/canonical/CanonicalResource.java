package org.ssmix2.jpcore.gateway.core.canonical;

import java.time.OffsetDateTime;
import java.util.List;

public interface CanonicalResource {
    String sourceMessageId();

    String sourceSystem();

    OffsetDateTime occurredAt();

    List<CanonicalCode> localCodes();

    List<CanonicalCode> standardCodes();

    String rawText();

    List<String> missingFields();

    List<String> unresolvedMappings();
}

