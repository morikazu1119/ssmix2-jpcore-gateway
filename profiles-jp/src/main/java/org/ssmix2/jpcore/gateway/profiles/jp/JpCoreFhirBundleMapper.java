package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDataSet;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDocumentReference;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalEncounter;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalMedicationRequest;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalObservation;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalPatient;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;
import org.ssmix2.jpcore.gateway.core.mapping.FhirBundleMapper;

import java.time.Instant;
import java.util.Map;

public class JpCoreFhirBundleMapper implements FhirBundleMapper {

    private final Map<String, JpCoreMappingDefinition> definitions;

    public JpCoreFhirBundleMapper(JpCoreMappingDefinitionCatalog catalog) {
        this.definitions = catalog.definitions();
    }

    @Override
    public Bundle map(CanonicalDataSet canonicalDataSet, String bundleId) {
        Bundle bundle = new Bundle();
        bundle.setId(bundleId);
        bundle.setType(Bundle.BundleType.COLLECTION);
        bundle.setTimestampElement(new InstantType(Instant.now().toString()));

        addResource(bundle, toPatient(canonicalDataSet.patient()));
        canonicalDataSet.encounters().forEach(encounter -> addResource(bundle, toEncounter(encounter)));
        canonicalDataSet.observations().forEach(observation -> addResource(bundle, toObservation(observation)));
        canonicalDataSet.medicationRequests().forEach(request -> addResource(bundle, toMedicationRequest(request)));
        canonicalDataSet.documentReferences().forEach(documentReference -> addResource(bundle, toDocumentReference(documentReference)));

        // TODO: Externalize more field-level mapping behavior into YAML/JSON instead of code.
        return bundle;
    }

    private void addResource(Bundle bundle, org.hl7.fhir.r4.model.Resource resource) {
        bundle.addEntry()
                .setFullUrl("urn:uuid:" + resource.getIdElement().getIdPart())
                .setResource(resource);
    }

    private Patient toPatient(CanonicalPatient patient) {
        Patient resource = new Patient();
        resource.setId(new IdType("Patient", patient.id()));
        resource.setMeta(metaFor(CanonicalResourceType.PATIENT));
        resource.addName()
                .setFamily(patient.familyName())
                .addGiven(patient.givenName());
        resource.setGender(patient.gender() == null ? null : Enumerations.AdministrativeGender.fromCode(patient.gender()));
        resource.setBirthDateElement(new org.hl7.fhir.r4.model.DateType(patient.birthDate()));
        return resource;
    }

    private Encounter toEncounter(CanonicalEncounter encounter) {
        Encounter resource = new Encounter();
        resource.setId(new IdType("Encounter", encounter.id()));
        resource.setMeta(metaFor(CanonicalResourceType.ENCOUNTER));
        resource.setStatus(Encounter.EncounterStatus.fromCode(encounter.status()));
        resource.setSubject(new Reference("Patient/" + encounter.patientId()));
        resource.setClass_(new Coding().setCode(encounter.classCode()));
        resource.setPeriod(new org.hl7.fhir.r4.model.Period().setStartElement(new org.hl7.fhir.r4.model.DateTimeType(encounter.startDateTime())));
        return resource;
    }

    private Observation toObservation(CanonicalObservation observation) {
        Observation resource = new Observation();
        resource.setId(new IdType("Observation", observation.id()));
        resource.setMeta(metaFor(CanonicalResourceType.OBSERVATION));
        resource.setStatus(Observation.ObservationStatus.fromCode(observation.status()));
        resource.setSubject(new Reference("Patient/" + observation.patientId()));
        if (observation.encounterId() != null) {
            resource.setEncounter(new Reference("Encounter/" + observation.encounterId()));
        }
        resource.setCode(new CodeableConcept().addCoding(new Coding().setCode(observation.code())));
        resource.setValue(new StringType(observation.value()));
        resource.setEffective(new org.hl7.fhir.r4.model.DateTimeType(observation.effectiveDateTime()));
        return resource;
    }

    private MedicationRequest toMedicationRequest(CanonicalMedicationRequest request) {
        MedicationRequest resource = new MedicationRequest();
        resource.setId(new IdType("MedicationRequest", request.id()));
        resource.setMeta(metaFor(CanonicalResourceType.MEDICATION_REQUEST));
        resource.setStatus(MedicationRequest.MedicationRequestStatus.fromCode(request.status()));
        resource.setIntent(MedicationRequest.MedicationRequestIntent.fromCode(request.intent()));
        resource.setSubject(new Reference("Patient/" + request.patientId()));
        if (request.encounterId() != null) {
            resource.setEncounter(new Reference("Encounter/" + request.encounterId()));
        }
        resource.setMedication(new CodeableConcept().addCoding(new Coding().setCode(request.medicationCode())));
        return resource;
    }

    private DocumentReference toDocumentReference(CanonicalDocumentReference documentReference) {
        DocumentReference resource = new DocumentReference();
        resource.setId(new IdType("DocumentReference", documentReference.id()));
        resource.setMeta(metaFor(CanonicalResourceType.DOCUMENT_REFERENCE));
        resource.setStatus(Enumerations.DocumentReferenceStatus.fromCode(documentReference.status()));
        resource.setSubject(new Reference("Patient/" + documentReference.patientId()));
        resource.setType(new CodeableConcept().addCoding(new Coding().setCode(documentReference.typeCode())));
        resource.setDescription(documentReference.title());
        resource.addContent(new DocumentReference.DocumentReferenceContentComponent()
                .setAttachment(new Attachment()
                        .setContentType(documentReference.contentType())
                        .setTitle(documentReference.title())
                        .setUrl("urn:ssmix2:document:" + documentReference.id())));
        return resource;
    }

    private Meta metaFor(CanonicalResourceType resourceType) {
        JpCoreMappingDefinition definition = definitions.get(resourceType.name());
        String profileUrl = definition == null ? JpCoreProfileCatalog.profileUrl(resourceType) : definition.profileUrl();
        return new Meta().addProfile(profileUrl);
    }
}
