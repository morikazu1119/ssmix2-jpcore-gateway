package org.ssmix2.jpcore.gateway.core.canonical;

public record CanonicalMedicationRequest(
        String id,
        String patientId,
        String encounterId,
        String status,
        String intent,
        String medicationCode
) {
}

