package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Reference;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalMedicationOrder;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import java.util.Date;

public class JpCoreMedicationRequestMapper extends AbstractJpCoreResourceMapper<CanonicalMedicationOrder, MedicationRequest> {

    public JpCoreMedicationRequestMapper(JpCoreMappingDefinitionCatalog catalog, JpClinsExtensionSupport jpClinsExtensionSupport) {
        super(CanonicalResourceType.MEDICATION_REQUEST, catalog, jpClinsExtensionSupport);
    }

    public MapperResult<MedicationRequest> map(CanonicalMedicationOrder source) {
        MedicationRequest target = new MedicationRequest();
        target.setId(new IdType("MedicationRequest", source.orderId()));
        MapperResult.Builder<MedicationRequest> resultBuilder = MapperResult.builder(target);

        target.setIdentifier(applyBaseMetadata(source, target, resultBuilder));
        target.setSubject(new Reference("Patient/" + source.patientId()));
        if (source.encounterId() != null && !source.encounterId().isBlank()) {
            target.setEncounter(new Reference("Encounter/" + source.encounterId()));
        } else {
            resultBuilder.warning("encounterId", "MedicationRequest encounter reference is absent.");
        }

        if (source.status() != null && !source.status().isBlank()) {
            target.setStatus(MedicationRequest.MedicationRequestStatus.fromCode(source.status()));
        } else {
            resultBuilder.unresolvedField("status", "MedicationRequest status is missing.");
        }

        if (source.intent() != null && !source.intent().isBlank()) {
            target.setIntent(MedicationRequest.MedicationRequestIntent.fromCode(source.intent()));
        } else {
            resultBuilder.unresolvedField("intent", "MedicationRequest intent is missing.");
        }

        CodeableConcept medication = new CodeableConcept().setText(source.medicationText());
        applyConceptCodes(medication, source.standardCodes(), source.localCodes(), "medication", resultBuilder);
        target.setMedication(medication);

        if (source.sourceEventTime() != null) {
            target.setAuthoredOn(Date.from(source.sourceEventTime().toInstant()));
        } else if (source.occurredAt() != null) {
            target.setAuthoredOn(Date.from(source.occurredAt().toInstant()));
            resultBuilder.warning("sourceEventTime", "MedicationRequest authoredOn fell back to occurredAt.");
        } else {
            resultBuilder.unresolvedField("authoredOn", "MedicationRequest has neither sourceEventTime nor occurredAt.");
        }

        return resultBuilder.build();
    }
}
