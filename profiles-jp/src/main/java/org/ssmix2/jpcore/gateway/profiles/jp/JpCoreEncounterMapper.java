package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalEncounter;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

public class JpCoreEncounterMapper extends AbstractJpCoreResourceMapper<CanonicalEncounter, Encounter> {

    public JpCoreEncounterMapper(JpCoreMappingDefinitionCatalog catalog, JpClinsExtensionSupport jpClinsExtensionSupport) {
        super(CanonicalResourceType.ENCOUNTER, catalog, jpClinsExtensionSupport);
    }

    public MapperResult<Encounter> map(CanonicalEncounter source) {
        Encounter target = new Encounter();
        target.setId(new IdType("Encounter", source.encounterId()));
        MapperResult.Builder<Encounter> resultBuilder = MapperResult.builder(target);

        target.setIdentifier(applyBaseMetadata(source, target, resultBuilder));
        target.setSubject(new Reference("Patient/" + source.patientId()));

        if (source.status() != null && !source.status().isBlank()) {
            target.setStatus(Encounter.EncounterStatus.fromCode(source.status()));
        } else {
            resultBuilder.unresolvedField("status", "Encounter status is missing.");
        }

        org.hl7.fhir.r4.model.Coding classCoding = singleCodingOrWarning(
                source.standardCodes(),
                source.localCodes(),
                "classCode",
                resultBuilder
        );
        if (classCoding != null) {
            target.setClass_(classCoding);
        }

        if (source.sourceEventTime() != null) {
            target.setPeriod(new org.hl7.fhir.r4.model.Period()
                    .setStartElement(new org.hl7.fhir.r4.model.DateTimeType(formatDateTime(source.sourceEventTime()))));
        } else {
            resultBuilder.warning("sourceEventTime", "Encounter sourceEventTime is absent; period.start was not populated.");
        }

        return resultBuilder.build();
    }
}
