package org.ssmix2.jpcore.gateway.core.canonical;

public record CanonicalObservation(
        String id,
        String patientId,
        String encounterId,
        String status,
        String code,
        String value,
        String effectiveDateTime
) {
}

