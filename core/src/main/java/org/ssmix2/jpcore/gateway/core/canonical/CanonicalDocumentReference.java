package org.ssmix2.jpcore.gateway.core.canonical;

public record CanonicalDocumentReference(
        String id,
        String patientId,
        String status,
        String typeCode,
        String title,
        String contentType
) {
}

