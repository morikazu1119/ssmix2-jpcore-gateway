package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.DocumentReference;
import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpCoreDocumentReferenceMapperTest {

    @Test
    void mapsCanonicalDocumentToDocumentReference() {
        JpCoreDocumentReferenceMapper mapper = new JpCoreDocumentReferenceMapper(JpCoreMapperTestSupport.CATALOG, new NoOpJpClinsExtensionSupport());
        MapperResult<DocumentReference> result = mapper.map(JpCoreMapperTestSupport.sampleDocument());
        DocumentReference fixture = JpCoreMapperTestSupport.parseFixture("examples/resources/document-reference.json", DocumentReference.class);

        assertEquals("DOC-001", result.resource().getIdElement().getIdPart());
        assertEquals("Discharge Summary", result.resource().getDescription());
        assertEquals("text/plain", result.resource().getContentFirstRep().getAttachment().getContentType());
        assertEquals(fixture.getType().getCodingFirstRep().getCode(), result.resource().getType().getCodingFirstRep().getCode());
        assertTrue(result.warnings().stream().anyMatch(issue -> issue.field().equals("rawText")));
    }
}

