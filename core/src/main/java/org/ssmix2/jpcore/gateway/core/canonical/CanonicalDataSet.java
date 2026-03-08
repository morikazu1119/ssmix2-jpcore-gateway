package org.ssmix2.jpcore.gateway.core.canonical;

import java.util.List;

public record CanonicalDataSet(
        String facilityId,
        CanonicalPatient patient,
        List<CanonicalEncounter> encounters,
        List<CanonicalObservation> observations,
        List<CanonicalMedicationOrder> medicationOrders,
        List<CanonicalDocument> documents
) {
    public CanonicalDataSet {
        encounters = List.copyOf(encounters);
        observations = List.copyOf(observations);
        medicationOrders = List.copyOf(medicationOrders);
        documents = List.copyOf(documents);
    }
}
