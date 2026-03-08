package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.InstantType;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDataSet;
import org.ssmix2.jpcore.gateway.core.mapping.FhirBundleMapper;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import java.time.Clock;
import java.time.Instant;

public class JpCoreFhirBundleMapper implements FhirBundleMapper {

    private final JpCorePatientMapper patientMapper;
    private final JpCoreEncounterMapper encounterMapper;
    private final JpCoreObservationMapper observationMapper;
    private final JpCoreMedicationRequestMapper medicationRequestMapper;
    private final JpCoreDocumentReferenceMapper documentReferenceMapper;
    private final Clock clock;

    public JpCoreFhirBundleMapper(JpCoreMappingDefinitionCatalog catalog) {
        this(catalog, new NoOpJpClinsExtensionSupport(), Clock.systemUTC());
    }

    public JpCoreFhirBundleMapper(
            JpCoreMappingDefinitionCatalog catalog,
            JpClinsExtensionSupport jpClinsExtensionSupport
    ) {
        this(catalog, jpClinsExtensionSupport, Clock.systemUTC());
    }

    public JpCoreFhirBundleMapper(
            JpCoreMappingDefinitionCatalog catalog,
            JpClinsExtensionSupport jpClinsExtensionSupport,
            Clock clock
    ) {
        this.patientMapper = new JpCorePatientMapper(catalog, jpClinsExtensionSupport);
        this.encounterMapper = new JpCoreEncounterMapper(catalog, jpClinsExtensionSupport);
        this.observationMapper = new JpCoreObservationMapper(catalog, jpClinsExtensionSupport);
        this.medicationRequestMapper = new JpCoreMedicationRequestMapper(catalog, jpClinsExtensionSupport);
        this.documentReferenceMapper = new JpCoreDocumentReferenceMapper(catalog, jpClinsExtensionSupport);
        this.clock = clock;
    }

    @Override
    public MapperResult<Bundle> map(CanonicalDataSet canonicalDataSet, String bundleId) {
        Bundle bundle = new Bundle();
        bundle.setId(bundleId);
        bundle.setType(Bundle.BundleType.COLLECTION);
        bundle.setTimestampElement(new InstantType(Instant.now(clock).toString()));
        MapperResult.Builder<Bundle> resultBuilder = MapperResult.builder(bundle);

        addResource(bundle, patientMapper.map(canonicalDataSet.patient()), resultBuilder);
        canonicalDataSet.encounters().forEach(encounter -> addResource(bundle, encounterMapper.map(encounter), resultBuilder));
        canonicalDataSet.observations().forEach(observation -> addResource(bundle, observationMapper.map(observation), resultBuilder));
        canonicalDataSet.medicationOrders().forEach(order -> addResource(bundle, medicationRequestMapper.map(order), resultBuilder));
        canonicalDataSet.documents().forEach(document -> addResource(bundle, documentReferenceMapper.map(document), resultBuilder));

        return resultBuilder.build();
    }

    private void addResource(
            Bundle bundle,
            MapperResult<? extends org.hl7.fhir.r4.model.Resource> resourceResult,
            MapperResult.Builder<Bundle> bundleResultBuilder
    ) {
        org.hl7.fhir.r4.model.Resource resource = resourceResult.resource();
        bundle.addEntry()
                .setFullUrl("urn:uuid:" + resource.getIdElement().getIdPart())
                .setResource(resource);
        bundleResultBuilder.addAll(resourceResult);
    }
}
