package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpCoreFhirBundleMapperTest {

    @Test
    void aggregatesDedicatedMapperResultsIntoBundle() {
        JpCoreFhirBundleMapper mapper = new JpCoreFhirBundleMapper(
                JpCoreMapperTestSupport.CATALOG,
                new NoOpJpClinsExtensionSupport(),
                Clock.fixed(Instant.parse("2026-01-15T00:00:00Z"), ZoneOffset.UTC)
        );

        MapperResult<Bundle> result = mapper.map(JpCoreMapperTestSupport.sampleDataSet(), "bundle-001");
        Bundle fixture = JpCoreMapperTestSupport.parseFixture("examples/bundles/sample-bundle.json", Bundle.class);

        assertEquals("bundle-001", result.resource().getIdElement().getIdPart());
        assertEquals(5, result.resource().getEntry().size());
        assertEquals(fixture.getTimestamp(), result.resource().getTimestamp());
        assertTrue(result.issues().size() >= 5);
    }
}
