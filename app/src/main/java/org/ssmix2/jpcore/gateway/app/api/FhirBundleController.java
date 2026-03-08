package org.ssmix2.jpcore.gateway.app.api;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssmix2.jpcore.gateway.app.service.GatewayIngestService;

@RestController
@RequestMapping("/fhir")
public class FhirBundleController {

    private final GatewayIngestService gatewayIngestService;
    private final IParser jsonParser;

    public FhirBundleController(GatewayIngestService gatewayIngestService, FhirContext fhirContext) {
        this.gatewayIngestService = gatewayIngestService;
        this.jsonParser = fhirContext.newJsonParser().setPrettyPrint(true);
    }

    @GetMapping(value = "/Bundle/{id}", produces = "application/fhir+json")
    public ResponseEntity<String> getBundle(@PathVariable("id") String bundleId) {
        Bundle bundle = gatewayIngestService.getBundle(bundleId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/fhir+json"))
                .body(jsonParser.encodeResourceToString(bundle));
    }
}

