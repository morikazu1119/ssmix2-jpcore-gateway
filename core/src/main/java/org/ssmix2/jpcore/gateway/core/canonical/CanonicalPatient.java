package org.ssmix2.jpcore.gateway.core.canonical;

public record CanonicalPatient(
        String id,
        String familyName,
        String givenName,
        String gender,
        String birthDate
) {
}

