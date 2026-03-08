package org.ssmix2.jpcore.gateway.core.parser;

import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SimpleKeyValueSsmix2Parser implements Ssmix2Parser {

    private static final Map<String, CanonicalResourceType> SUPPORTED_DIRECTORIES = Map.of(
            "patient", CanonicalResourceType.PATIENT,
            "encounter", CanonicalResourceType.ENCOUNTER,
            "observation", CanonicalResourceType.OBSERVATION,
            "medication-request", CanonicalResourceType.MEDICATION_REQUEST,
            "document-reference", CanonicalResourceType.DOCUMENT_REFERENCE
    );

    @Override
    public ParsedSsmix2Dataset parse(Ssmix2InputSource inputSource) {
        Path rootDirectory = inputSource.rootDirectory();
        if (rootDirectory == null || !Files.exists(rootDirectory) || !Files.isDirectory(rootDirectory)) {
            throw new UnsupportedSsmix2InputException("Input path must be an existing directory: " + rootDirectory);
        }

        validateTopLevelDirectories(rootDirectory);

        List<ParsedSsmix2Record> records = new ArrayList<>();
        for (Map.Entry<String, CanonicalResourceType> entry : SUPPORTED_DIRECTORIES.entrySet()) {
            Path resourceDirectory = rootDirectory.resolve(entry.getKey());
            if (!Files.isDirectory(resourceDirectory)) {
                continue;
            }

            try (Stream<Path> stream = Files.list(resourceDirectory)) {
                stream.filter(Files::isRegularFile)
                        .sorted()
                        .forEach(path -> records.add(parseRecord(entry.getValue(), path)));
            } catch (IOException exception) {
                throw new UnsupportedSsmix2InputException(
                        "Failed to read resource directory: " + resourceDirectory + " (" + exception.getMessage() + ")"
                );
            }
        }

        if (records.isEmpty()) {
            throw new UnsupportedSsmix2InputException("No supported SS-MIX2 fixture files found under: " + rootDirectory);
        }

        return new ParsedSsmix2Dataset(inputSource.facilityId(), List.copyOf(records));
    }

    private void validateTopLevelDirectories(Path rootDirectory) {
        try (Stream<Path> stream = Files.list(rootDirectory)) {
            List<String> unsupported = stream.filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> !SUPPORTED_DIRECTORIES.containsKey(name))
                    .sorted()
                    .toList();
            if (!unsupported.isEmpty()) {
                throw new UnsupportedSsmix2InputException(
                        "Unsupported resource directories: " + unsupported + ". Supported directories are: " + SUPPORTED_DIRECTORIES.keySet()
                );
            }
        } catch (IOException exception) {
            throw new UnsupportedSsmix2InputException(
                    "Failed to inspect input directory: " + rootDirectory + " (" + exception.getMessage() + ")"
            );
        }
    }

    private ParsedSsmix2Record parseRecord(CanonicalResourceType resourceType, Path sourceFile) {
        Map<String, String> attributes = new HashMap<>();
        String rawText;
        try {
            List<String> lines = Files.readAllLines(sourceFile);
            rawText = String.join(System.lineSeparator(), lines);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                int separatorIndex = trimmed.indexOf('=');
                if (separatorIndex <= 0 || separatorIndex == trimmed.length() - 1) {
                    throw new UnsupportedSsmix2InputException(
                            "Malformed key=value line in " + sourceFile + ": " + trimmed
                    );
                }

                String key = trimmed.substring(0, separatorIndex).trim();
                String value = trimmed.substring(separatorIndex + 1).trim();
                attributes.put(key, value);
            }
        } catch (IOException exception) {
            throw new UnsupportedSsmix2InputException(
                    "Failed to read fixture file: " + sourceFile + " (" + exception.getMessage() + ")"
            );
        }

        String recordId = attributes.getOrDefault("id", sourceFile.getFileName().toString());
        return new ParsedSsmix2Record(resourceType, recordId, Map.copyOf(attributes), rawText, sourceFile);
    }
}
