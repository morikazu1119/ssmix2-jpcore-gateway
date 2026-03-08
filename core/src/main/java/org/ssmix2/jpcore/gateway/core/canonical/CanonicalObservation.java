package org.ssmix2.jpcore.gateway.core.canonical;

import java.time.OffsetDateTime;
import java.util.List;

public record CanonicalObservation(
        String observationId,
        String patientId,
        String encounterId,
        String status,
        String valueText,
        OffsetDateTime sourceEventTime,
        String sourceMessageId,
        String sourceSystem,
        OffsetDateTime occurredAt,
        List<CanonicalCode> localCodes,
        List<CanonicalCode> standardCodes,
        String rawText,
        List<String> missingFields,
        List<String> unresolvedMappings
) implements CanonicalResource {

    public CanonicalObservation {
        localCodes = List.copyOf(localCodes);
        standardCodes = List.copyOf(standardCodes);
        missingFields = List.copyOf(missingFields);
        unresolvedMappings = List.copyOf(unresolvedMappings);
    }
}

