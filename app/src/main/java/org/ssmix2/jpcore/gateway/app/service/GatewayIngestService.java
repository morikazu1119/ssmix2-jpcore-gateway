package org.ssmix2.jpcore.gateway.app.service;

import org.hl7.fhir.r4.model.Bundle;
import org.springframework.web.server.ResponseStatusException;
import org.ssmix2.jpcore.gateway.app.api.Ssmix2IngestRequest;
import org.ssmix2.jpcore.gateway.app.api.Ssmix2IngestResponse;
import org.ssmix2.jpcore.gateway.app.config.GatewayProperties;
import org.ssmix2.jpcore.gateway.app.store.BundleStore;
import org.ssmix2.jpcore.gateway.core.service.ConversionPipeline;
import org.ssmix2.jpcore.gateway.core.service.ConversionRequest;
import org.ssmix2.jpcore.gateway.core.service.ConversionResult;

import java.nio.file.Path;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class GatewayIngestService {

    private final ConversionPipeline conversionPipeline;
    private final BundleStore bundleStore;
    private final AuditService auditService;
    private final GatewayProperties gatewayProperties;

    public GatewayIngestService(
            ConversionPipeline conversionPipeline,
            BundleStore bundleStore,
            AuditService auditService,
            GatewayProperties gatewayProperties
    ) {
        this.conversionPipeline = conversionPipeline;
        this.bundleStore = bundleStore;
        this.auditService = auditService;
        this.gatewayProperties = gatewayProperties;
    }

    public Ssmix2IngestResponse ingest(Ssmix2IngestRequest request) {
        if (request.sourcePath() == null || request.sourcePath().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "sourcePath is required");
        }

        ConversionResult result = conversionPipeline.convert(new ConversionRequest(
                request.bundleId(),
                request.facilityId(),
                Path.of(request.sourcePath())
        ));

        if (gatewayProperties.isStrictValidation() && !result.validationSummary().valid()) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY, "FHIR validation failed in strict mode");
        }

        String bundleId = result.bundle().getIdElement().getIdPart();
        bundleStore.save(bundleId, result.bundle());
        auditService.logIngest(bundleId, request.facilityId(), request.sourcePath());

        return new Ssmix2IngestResponse(
                bundleId,
                request.sourcePath(),
                result.validationSummary().valid(),
                result.validationSummary().issues().size(),
                gatewayProperties.getFixtureRootHint()
        );
    }

    public Bundle getBundle(String bundleId) {
        auditService.logBundleFetch(bundleId);
        return bundleStore.findById(bundleId)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Bundle not found: " + bundleId));
    }
}

