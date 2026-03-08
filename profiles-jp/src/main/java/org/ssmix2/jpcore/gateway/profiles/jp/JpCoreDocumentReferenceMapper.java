package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalDocument;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import java.util.Date;

public class JpCoreDocumentReferenceMapper extends AbstractJpCoreResourceMapper<CanonicalDocument, DocumentReference> {

    public JpCoreDocumentReferenceMapper(JpCoreMappingDefinitionCatalog catalog, JpClinsExtensionSupport jpClinsExtensionSupport) {
        super(CanonicalResourceType.DOCUMENT_REFERENCE, catalog, jpClinsExtensionSupport);
    }

    public MapperResult<DocumentReference> map(CanonicalDocument source) {
        DocumentReference target = new DocumentReference();
        target.setId(new IdType("DocumentReference", source.documentId()));
        MapperResult.Builder<DocumentReference> resultBuilder = MapperResult.builder(target);

        target.setIdentifier(applyBaseMetadata(source, target, resultBuilder));
        target.setSubject(new Reference("Patient/" + source.patientId()));

        if (source.status() != null && !source.status().isBlank()) {
            target.setStatus(Enumerations.DocumentReferenceStatus.fromCode(source.status()));
        } else {
            resultBuilder.unresolvedField("status", "DocumentReference status is missing.");
        }

        CodeableConcept type = new CodeableConcept();
        applyConceptCodes(type, source.standardCodes(), source.localCodes(), "type", resultBuilder);
        target.setType(type);

        if (source.title() != null && !source.title().isBlank()) {
            target.setDescription(source.title());
        } else {
            resultBuilder.unresolvedField("title", "Document title is missing.");
        }

        target.addContent(new DocumentReference.DocumentReferenceContentComponent()
                .setAttachment(new Attachment()
                        .setContentType(source.contentType())
                        .setTitle(source.title())
                        .setUrl("urn:ssmix2:document:" + source.documentId())));

        if (source.sourceEventTime() != null) {
            target.setDate(Date.from(source.sourceEventTime().toInstant()));
        } else if (source.occurredAt() != null) {
            target.setDate(Date.from(source.occurredAt().toInstant()));
            resultBuilder.warning("sourceEventTime", "DocumentReference date fell back to occurredAt.");
        } else {
            resultBuilder.unresolvedField("date", "DocumentReference has neither sourceEventTime nor occurredAt.");
        }

        return resultBuilder.build();
    }
}
