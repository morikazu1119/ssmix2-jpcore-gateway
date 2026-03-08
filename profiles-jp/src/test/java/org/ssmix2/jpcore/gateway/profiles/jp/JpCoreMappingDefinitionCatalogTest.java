package org.ssmix2.jpcore.gateway.profiles.jp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpCoreMappingDefinitionCatalogTest {

    @Test
    void loadsAllMappingDefinitions() {
        JpCoreMappingDefinitionCatalog catalog = new JpCoreMappingDefinitionCatalog();

        assertEquals(5, catalog.definitions().size());
        assertTrue(catalog.definitions().containsKey("PATIENT"));
        assertTrue(catalog.definitions().containsKey("DOCUMENT_REFERENCE"));
    }
}

