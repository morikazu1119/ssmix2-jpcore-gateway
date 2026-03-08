package org.ssmix2.jpcore.gateway.core.canonical;

import java.util.List;

public record CanonicalDataSet(
        String facilityId,
        CanonicalPatient patient,
        List<CanonicalEncounter> encounters,
        List<CanonicalObservation> observations,
        List<CanonicalMedicationRequest> medicationRequests,
        List<CanonicalDocumentReference> documentReferences
) {
}

