package org.ssmix2.jpcore.gateway.app.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.ssmix2.jpcore.gateway.app.service.GatewayIngestService;

@RestController
@RequestMapping("/ingest")
public class IngestController {

    private final GatewayIngestService gatewayIngestService;

    public IngestController(GatewayIngestService gatewayIngestService) {
        this.gatewayIngestService = gatewayIngestService;
    }

    @PostMapping("/ssmix2")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Ssmix2IngestResponse ingest(@Valid @RequestBody Ssmix2IngestRequest request) {
        return gatewayIngestService.ingest(request);
    }
}

