package org.ssmix2.jpcore.gateway.profiles.jp;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JpCoreMappingDefinitionCatalog {

    private static final List<String> RESOURCE_FILES = List.of(
            "mappings/jp-core/patient.yaml",
            "mappings/jp-core/encounter.yaml",
            "mappings/jp-core/observation.yaml",
            "mappings/jp-core/medication-request.yaml",
            "mappings/jp-core/document-reference.yaml"
    );

    private final Map<String, JpCoreMappingDefinition> definitions;

    public JpCoreMappingDefinitionCatalog() {
        this.definitions = loadDefinitions();
    }

    public Map<String, JpCoreMappingDefinition> definitions() {
        return definitions;
    }

    private Map<String, JpCoreMappingDefinition> loadDefinitions() {
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        Map<String, JpCoreMappingDefinition> loadedDefinitions = new LinkedHashMap<>();

        for (String resourceFile : RESOURCE_FILES) {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceFile)) {
                if (inputStream == null) {
                    throw new IllegalStateException("Missing mapping definition resource: " + resourceFile);
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> raw = yaml.load(inputStream);
                JpCoreMappingDefinition definition = new JpCoreMappingDefinition(
                        stringValue(raw, "resourceType"),
                        stringValue(raw, "profileUrl"),
                        stringValue(raw, "description"),
                        listValue(raw, "requiredFields")
                );
                loadedDefinitions.put(definition.resourceType(), definition);
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to load JP Core mapping definition: " + resourceFile, exception);
            }
        }

        return Map.copyOf(loadedDefinitions);
    }

    private String stringValue(Map<String, Object> raw, String key) {
        Object value = raw.get(key);
        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            throw new IllegalStateException("Mapping field must be a non-empty string: " + key);
        }
        return stringValue;
    }

    @SuppressWarnings("unchecked")
    private List<String> listValue(Map<String, Object> raw, String key) {
        Object value = raw.get(key);
        if (!(value instanceof List<?> listValue)) {
            throw new IllegalStateException("Mapping field must be a list: " + key);
        }
        return ((List<Object>) listValue).stream().map(String::valueOf).toList();
    }
}

