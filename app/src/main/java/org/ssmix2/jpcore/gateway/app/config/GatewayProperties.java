package org.ssmix2.jpcore.gateway.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

    private boolean strictValidation = false;
    private String fixtureRootHint = "/fixtures/ssmix2";

    public boolean isStrictValidation() {
        return strictValidation;
    }

    public void setStrictValidation(boolean strictValidation) {
        this.strictValidation = strictValidation;
    }

    public String getFixtureRootHint() {
        return fixtureRootHint;
    }

    public void setFixtureRootHint(String fixtureRootHint) {
        this.fixtureRootHint = fixtureRootHint;
    }
}

