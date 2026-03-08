package org.ssmix2.jpcore.gateway.core.canonical;

public record CanonicalEncounter(
        String id,
        String patientId,
        String status,
        String classCode,
        String startDateTime
) {
}

