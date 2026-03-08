package org.ssmix2.jpcore.gateway.app.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssmix2.jpcore.gateway.app.config.GatewayProperties;

import java.util.Map;

@RestController
public class HealthController {

    private final GatewayProperties gatewayProperties;

    public HealthController(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "mode", "read-only",
                "fixtureRootHint", gatewayProperties.getFixtureRootHint()
        );
    }
}

