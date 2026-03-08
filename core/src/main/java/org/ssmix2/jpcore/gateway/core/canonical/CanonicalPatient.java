package org.ssmix2.jpcore.gateway.core.canonical;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record CanonicalPatient(
        String patientId,
        String familyName,
        String givenName,
        String gender,
        LocalDate birthDate,
        String sourceMessageId,
        String sourceSystem,
        OffsetDateTime occurredAt,
        List<CanonicalCode> localCodes,
        List<CanonicalCode> standardCodes,
        String rawText,
        List<String> missingFields,
        List<String> unresolvedMappings
) implements CanonicalResource {

    public CanonicalPatient {
        localCodes = List.copyOf(localCodes);
        standardCodes = List.copyOf(standardCodes);
        missingFields = List.copyOf(missingFields);
        unresolvedMappings = List.copyOf(unresolvedMappings);
    }
}

