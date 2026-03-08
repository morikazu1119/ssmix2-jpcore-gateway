package org.ssmix2.jpcore.gateway.app.api;

public record Ssmix2IngestResponse(
        String bundleId,
        String sourcePath,
        boolean valid,
        int issueCount,
        String fixtureRootHint
) {
}

