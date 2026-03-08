package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalObservation;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

public class JpCoreObservationMapper extends AbstractJpCoreResourceMapper<CanonicalObservation, Observation> {

    public JpCoreObservationMapper(JpCoreMappingDefinitionCatalog catalog, JpClinsExtensionSupport jpClinsExtensionSupport) {
        super(CanonicalResourceType.OBSERVATION, catalog, jpClinsExtensionSupport);
    }

    public MapperResult<Observation> map(CanonicalObservation source) {
        Observation target = new Observation();
        target.setId(new IdType("Observation", source.observationId()));
        MapperResult.Builder<Observation> resultBuilder = MapperResult.builder(target);

        target.setIdentifier(applyBaseMetadata(source, target, resultBuilder));
        target.setSubject(new Reference("Patient/" + source.patientId()));
        if (source.encounterId() != null && !source.encounterId().isBlank()) {
            target.setEncounter(new Reference("Encounter/" + source.encounterId()));
        } else {
            resultBuilder.warning("encounterId", "Observation encounter reference is absent.");
        }

        if (source.status() != null && !source.status().isBlank()) {
            target.setStatus(Observation.ObservationStatus.fromCode(source.status()));
        } else {
            resultBuilder.unresolvedField("status", "Observation status is missing.");
        }

        CodeableConcept code = new CodeableConcept();
        applyConceptCodes(code, source.standardCodes(), source.localCodes(), "code", resultBuilder);
        target.setCode(code);

        if (source.valueText() != null && !source.valueText().isBlank()) {
            target.setValue(new StringType(source.valueText()));
        } else {
            resultBuilder.unresolvedField("valueText", "Observation valueText is missing.");
        }

        if (source.sourceEventTime() != null) {
            target.setEffective(new org.hl7.fhir.r4.model.DateTimeType(formatDateTime(source.sourceEventTime())));
        } else if (source.occurredAt() != null) {
            target.setEffective(new org.hl7.fhir.r4.model.DateTimeType(formatDateTime(source.occurredAt())));
            resultBuilder.warning("sourceEventTime", "Observation effective[x] fell back to occurredAt.");
        } else {
            resultBuilder.unresolvedField("effectiveDateTime", "Observation has neither sourceEventTime nor occurredAt.");
        }

        return resultBuilder.build();
    }
}
