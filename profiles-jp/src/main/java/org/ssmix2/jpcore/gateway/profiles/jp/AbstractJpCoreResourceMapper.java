package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalCode;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResource;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

abstract class AbstractJpCoreResourceMapper<S extends CanonicalResource, T extends DomainResource> {

    private static final DateTimeFormatter OFFSET_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final CanonicalResourceType resourceType;
    private final Map<String, JpCoreMappingDefinition> definitions;
    private final JpClinsExtensionSupport jpClinsExtensionSupport;

    protected AbstractJpCoreResourceMapper(
            CanonicalResourceType resourceType,
            JpCoreMappingDefinitionCatalog catalog,
            JpClinsExtensionSupport jpClinsExtensionSupport
    ) {
        this.resourceType = resourceType;
        this.definitions = catalog.definitions();
        this.jpClinsExtensionSupport = jpClinsExtensionSupport;
    }

    protected List<Identifier> applyBaseMetadata(S source, T target, MapperResult.Builder<T> resultBuilder) {
        target.setMeta(new Meta().addProfile(profileUrl()));
        if (source.occurredAt() != null) {
            target.getMeta().setLastUpdated(Date.from(source.occurredAt().toInstant()));
        }

        List<Identifier> identifiers = new ArrayList<>();
        if (source.sourceMessageId() != null && !source.sourceMessageId().isBlank()) {
            identifiers.add(new Identifier().setSystem("urn:ssmix2:source-message-id").setValue(source.sourceMessageId()));
        } else {
            resultBuilder.unresolvedField("sourceMessageId", "sourceMessageId is missing and cannot be preserved in FHIR identifiers.");
        }

        if (source.sourceSystem() != null && !source.sourceSystem().isBlank()) {
            identifiers.add(new Identifier().setSystem("urn:ssmix2:source-system").setValue(source.sourceSystem()));
        } else {
            resultBuilder.unresolvedField("sourceSystem", "sourceSystem is missing and cannot be preserved in FHIR identifiers.");
        }

        if (source.rawText() != null && !source.rawText().isBlank()) {
            resultBuilder.warning("rawText", "rawText is retained only in the canonical layer and is not emitted into JP Core JSON.");
        }

        for (String missingField : source.missingFields()) {
            resultBuilder.unresolvedField(missingField, "Canonical model marked this field as missing before FHIR mapping.");
        }

        for (String unresolvedMapping : source.unresolvedMappings()) {
            resultBuilder.unresolvedField("mapping", unresolvedMapping);
        }

        jpClinsExtensionSupport.apply(source, target, resultBuilder);
        return identifiers;
    }

    protected void applyConceptCodes(
            CodeableConcept target,
            List<CanonicalCode> standardCodes,
            List<CanonicalCode> localCodes,
            String field,
            MapperResult.Builder<?> resultBuilder
    ) {
        standardCodes.forEach(code -> target.addCoding(toCoding(code)));
        localCodes.forEach(code -> target.addCoding(toCoding(code)));

        if (target.getCoding().isEmpty()) {
            resultBuilder.unresolvedField(field, "No local or standard codes were available for JP Core coding.");
        }
    }

    protected Coding singleCodingOrWarning(
            List<CanonicalCode> standardCodes,
            List<CanonicalCode> localCodes,
            String field,
            MapperResult.Builder<?> resultBuilder
    ) {
        List<CanonicalCode> combined = new ArrayList<>();
        combined.addAll(standardCodes);
        combined.addAll(localCodes);
        if (combined.isEmpty()) {
            resultBuilder.unresolvedField(field, "No coding available for a single-coding FHIR element.");
            return null;
        }

        if (combined.size() > 1) {
            resultBuilder.warning(field, "Multiple canonical codes were available but the JP Core target element supports only one primary coding.");
        }

        return toCoding(combined.getFirst());
    }

    protected void warnIfUnusedCodes(
            List<CanonicalCode> standardCodes,
            List<CanonicalCode> localCodes,
            String field,
            MapperResult.Builder<?> resultBuilder
    ) {
        if (!standardCodes.isEmpty() || !localCodes.isEmpty()) {
            resultBuilder.warning(field, "Canonical codes exist for this resource but no JP Core target element is mapped in the MVP.");
        }
    }

    protected String formatDateTime(OffsetDateTime dateTime) {
        return dateTime.format(OFFSET_DATE_TIME_FORMATTER);
    }

    private Coding toCoding(CanonicalCode code) {
        return new Coding()
                .setSystem(code.codeSystem())
                .setCode(code.code())
                .setDisplay(code.display());
    }

    private String profileUrl() {
        JpCoreMappingDefinition definition = definitions.get(resourceType.name());
        return definition == null ? JpCoreProfileCatalog.profileUrl(resourceType) : definition.profileUrl();
    }
}
