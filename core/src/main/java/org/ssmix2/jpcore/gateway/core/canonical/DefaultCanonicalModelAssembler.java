package org.ssmix2.jpcore.gateway.core.canonical;

import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Dataset;
import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Record;
import org.ssmix2.jpcore.gateway.core.parser.UnsupportedSsmix2InputException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
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

        CanonicalPatient patient = toPatient(patientRecords.getFirst(), parsedDataset.facilityId());

        List<CanonicalEncounter> encounters = new ArrayList<>();
        for (ParsedSsmix2Record record : recordsByType(parsedDataset, CanonicalResourceType.ENCOUNTER)) {
            encounters.add(new CanonicalEncounter(
                    required(record, "id"),
                    patient.patientId(),
                    valueOrDefault(record, "status", "finished"),
                    valueOrDefault(record, "classCode", "AMB"),
                    parseOptionalDateTime(record, "startDateTime"),
                    record.recordId(),
                    sourceSystem(record, parsedDataset.facilityId()),
                    resolveOccurredAt(record, "startDateTime"),
                    localCodes(record, "classCode"),
                    standardCodes(record, "classCode"),
                    record.rawText(),
                    missingFields(record, "startDateTime"),
                    unresolvedMappings(record, "classCode")
            ));
        }

        List<CanonicalObservation> observations = new ArrayList<>();
        for (ParsedSsmix2Record record : recordsByType(parsedDataset, CanonicalResourceType.OBSERVATION)) {
            observations.add(new CanonicalObservation(
                    required(record, "id"),
                    patient.patientId(),
                    valueOrDefault(record, "encounterId", encounters.isEmpty() ? null : encounters.getFirst().encounterId()),
                    valueOrDefault(record, "status", "final"),
                    valueOrDefault(record, "value", "unsupported"),
                    parseOptionalDateTime(record, "effectiveDateTime"),
                    record.recordId(),
                    sourceSystem(record, parsedDataset.facilityId()),
                    resolveOccurredAt(record, "effectiveDateTime"),
                    localCodes(record, "code"),
                    standardCodes(record, "code"),
                    record.rawText(),
                    missingFields(record, "code", "effectiveDateTime"),
                    unresolvedMappings(record, "code")
            ));
        }

        List<CanonicalMedicationOrder> medicationOrders = new ArrayList<>();
        for (ParsedSsmix2Record record : recordsByType(parsedDataset, CanonicalResourceType.MEDICATION_REQUEST)) {
            medicationOrders.add(new CanonicalMedicationOrder(
                    required(record, "id"),
                    patient.patientId(),
                    valueOrDefault(record, "encounterId", encounters.isEmpty() ? null : encounters.getFirst().encounterId()),
                    valueOrDefault(record, "status", "active"),
                    valueOrDefault(record, "intent", "order"),
                    valueOrDefault(record, "medicationText", valueOrDefault(record, "medicationCode", "unsupported-medication")),
                    parseOptionalDateTime(record, "orderedAt"),
                    record.recordId(),
                    sourceSystem(record, parsedDataset.facilityId()),
                    resolveOccurredAt(record, "orderedAt"),
                    localCodes(record, "medicationCode"),
                    standardCodes(record, "medicationCode"),
                    record.rawText(),
                    missingFields(record, "medicationCode", "orderedAt"),
                    unresolvedMappings(record, "medicationCode")
            ));
        }

        List<CanonicalDocument> documents = new ArrayList<>();
        for (ParsedSsmix2Record record : recordsByType(parsedDataset, CanonicalResourceType.DOCUMENT_REFERENCE)) {
            documents.add(new CanonicalDocument(
                    required(record, "id"),
                    patient.patientId(),
                    valueOrDefault(record, "status", "current"),
                    valueOrDefault(record, "title", record.recordId()),
                    valueOrDefault(record, "contentType", "text/plain"),
                    parseOptionalDateTime(record, "documentDateTime"),
                    record.recordId(),
                    sourceSystem(record, parsedDataset.facilityId()),
                    resolveOccurredAt(record, "documentDateTime"),
                    localCodes(record, "typeCode"),
                    standardCodes(record, "typeCode"),
                    record.rawText(),
                    missingFields(record, "typeCode", "documentDateTime"),
                    unresolvedMappings(record, "typeCode")
            ));
        }

        return new CanonicalDataSet(
                parsedDataset.facilityId(),
                patient,
                List.copyOf(encounters),
                List.copyOf(observations),
                List.copyOf(medicationOrders),
                List.copyOf(documents)
        );
    }

    private List<ParsedSsmix2Record> recordsByType(ParsedSsmix2Dataset dataset, CanonicalResourceType resourceType) {
        return dataset.records().stream()
                .filter(record -> record.resourceType() == resourceType)
                .toList();
    }

    private CanonicalPatient toPatient(ParsedSsmix2Record record, String defaultSourceSystem) {
        return new CanonicalPatient(
                required(record, "id"),
                valueOrDefault(record, "familyName", "Unknown"),
                valueOrDefault(record, "givenName", "Unknown"),
                valueOrDefault(record, "gender", "unknown"),
                parseOptionalDate(record, "birthDate"),
                record.recordId(),
                sourceSystem(record, defaultSourceSystem),
                resolveOccurredAt(record, "patientUpdatedAt"),
                localCodes(record),
                standardCodes(record),
                record.rawText(),
                missingFields(record, "birthDate", "patientUpdatedAt"),
                unresolvedMappings(record)
        );
    }

    private OffsetDateTime resolveOccurredAt(ParsedSsmix2Record record, String preferredField) {
        OffsetDateTime preferred = parseOptionalDateTime(record, preferredField);
        if (preferred != null) {
            return preferred;
        }
        return parseOptionalDateTime(record, "occurredAt");
    }

    private OffsetDateTime parseOptionalDateTime(ParsedSsmix2Record record, String key) {
        String value = record.attributes().get(key);
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            throw new UnsupportedSsmix2InputException(
                    "Attribute '" + key + "' must be ISO-8601 offset datetime for " + record.resourceType() + " in " + record.sourceFile()
            );
        }
    }

    private LocalDate parseOptionalDate(ParsedSsmix2Record record, String key) {
        String value = record.attributes().get(key);
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new UnsupportedSsmix2InputException(
                    "Attribute '" + key + "' must be ISO-8601 date for " + record.resourceType() + " in " + record.sourceFile()
            );
        }
    }

    private String sourceSystem(ParsedSsmix2Record record, String defaultValue) {
        return valueOrDefault(record, "sourceSystem", defaultValue);
    }

    private List<CanonicalCode> localCodes(ParsedSsmix2Record record, String... preferredKeys) {
        for (String preferredKey : preferredKeys) {
            String code = record.attributes().get(preferredKey);
            if (code != null && !code.isBlank()) {
                return List.of(new CanonicalCode("local:" + preferredKey, code, code));
            }
        }
        return List.of();
    }

    private List<CanonicalCode> standardCodes(ParsedSsmix2Record record, String... preferredKeys) {
        for (String preferredKey : preferredKeys) {
            String explicitStandardCode = record.attributes().get("standard" + capitalize(preferredKey));
            if (explicitStandardCode != null && !explicitStandardCode.isBlank()) {
                String system = valueOrDefault(record, "standard" + capitalize(preferredKey) + "System", "urn:unknown:standard");
                return List.of(new CanonicalCode(system, explicitStandardCode, explicitStandardCode));
            }
        }
        return List.of();
    }

    private List<String> missingFields(ParsedSsmix2Record record, String... fields) {
        List<String> missing = new ArrayList<>();
        for (String field : fields) {
            String value = record.attributes().get(field);
            if (value == null || value.isBlank()) {
                missing.add(field);
            }
        }
        return List.copyOf(missing);
    }

    private List<String> unresolvedMappings(ParsedSsmix2Record record, String... codeKeys) {
        List<String> unresolved = new ArrayList<>();
        for (String codeKey : codeKeys) {
            String code = record.attributes().get(codeKey);
            if (code != null && !code.isBlank()) {
                String standardCodeKey = "standard" + capitalize(codeKey);
                String standardCode = record.attributes().get(standardCodeKey);
                if (standardCode == null || standardCode.isBlank()) {
                    unresolved.add("No standard mapping resolved for " + codeKey + "=" + code);
                }
            }
        }
        return List.copyOf(unresolved);
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
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
