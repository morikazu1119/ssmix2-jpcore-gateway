package org.ssmix2.jpcore.gateway.core.canonical;

import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Dataset;
import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Record;
import org.ssmix2.jpcore.gateway.core.parser.UnsupportedSsmix2InputException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultCanonicalModelAssembler implements CanonicalModelAssembler {

    @Override
    public CanonicalDataSet assemble(ParsedSsmix2Dataset parsedDataset) {
        List<ParsedSsmix2Record> patientRecords = recordsByType(parsedDataset, CanonicalResourceType.PATIENT);
        if (patientRecords.size() != 1) {
            throw new UnsupportedSsmix2InputException(
                    "Exactly one patient record is required for the MVP scaffold, found: " + patientRecords.size()
            );
        }

        CanonicalPatient patient = toPatient(patientRecords.getFirst());

        List<CanonicalEncounter> encounters = new ArrayList<>();
        for (ParsedSsmix2Record record : recordsByType(parsedDataset, CanonicalResourceType.ENCOUNTER)) {
            encounters.add(new CanonicalEncounter(
                    required(record, "id"),
                    patient.id(),
                    valueOrDefault(record, "status", "finished"),
                    valueOrDefault(record, "classCode", "AMB"),
                    valueOrDefault(record, "startDateTime", "1970-01-01T00:00:00+09:00")
            ));
        }

        List<CanonicalObservation> observations = new ArrayList<>();
        for (ParsedSsmix2Record record : recordsByType(parsedDataset, CanonicalResourceType.OBSERVATION)) {
            observations.add(new CanonicalObservation(
                    required(record, "id"),
                    patient.id(),
                    valueOrDefault(record, "encounterId", encounters.isEmpty() ? null : encounters.getFirst().id()),
                    valueOrDefault(record, "status", "final"),
                    required(record, "code"),
                    valueOrDefault(record, "value", "unsupported"),
                    valueOrDefault(record, "effectiveDateTime", "1970-01-01T00:00:00+09:00")
            ));
        }

        List<CanonicalMedicationRequest> medicationRequests = new ArrayList<>();
        for (ParsedSsmix2Record record : recordsByType(parsedDataset, CanonicalResourceType.MEDICATION_REQUEST)) {
            medicationRequests.add(new CanonicalMedicationRequest(
                    required(record, "id"),
                    patient.id(),
                    valueOrDefault(record, "encounterId", encounters.isEmpty() ? null : encounters.getFirst().id()),
                    valueOrDefault(record, "status", "active"),
                    valueOrDefault(record, "intent", "order"),
                    required(record, "medicationCode")
            ));
        }

        List<CanonicalDocumentReference> documentReferences = new ArrayList<>();
        for (ParsedSsmix2Record record : recordsByType(parsedDataset, CanonicalResourceType.DOCUMENT_REFERENCE)) {
            documentReferences.add(new CanonicalDocumentReference(
                    required(record, "id"),
                    patient.id(),
                    valueOrDefault(record, "status", "current"),
                    required(record, "typeCode"),
                    valueOrDefault(record, "title", record.recordId()),
                    valueOrDefault(record, "contentType", "text/plain")
            ));
        }

        return new CanonicalDataSet(
                parsedDataset.facilityId(),
                patient,
                List.copyOf(encounters),
                List.copyOf(observations),
                List.copyOf(medicationRequests),
                List.copyOf(documentReferences)
        );
    }

    private List<ParsedSsmix2Record> recordsByType(ParsedSsmix2Dataset dataset, CanonicalResourceType resourceType) {
        return dataset.records().stream()
                .filter(record -> record.resourceType() == resourceType)
                .toList();
    }

    private CanonicalPatient toPatient(ParsedSsmix2Record record) {
        return new CanonicalPatient(
                required(record, "id"),
                valueOrDefault(record, "familyName", "Unknown"),
                valueOrDefault(record, "givenName", "Unknown"),
                valueOrDefault(record, "gender", "unknown"),
                valueOrDefault(record, "birthDate", "1900-01-01")
        );
    }

    private String required(ParsedSsmix2Record record, String key) {
        String value = record.attributes().get(key);
        if (value == null || value.isBlank()) {
            throw new UnsupportedSsmix2InputException(
                    "Required attribute '" + key + "' is missing for " + record.resourceType() + " in " + record.sourceFile()
            );
        }
        return value;
    }

    private String valueOrDefault(ParsedSsmix2Record record, String key, String defaultValue) {
        String value = record.attributes().get(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}

