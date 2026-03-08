package org.ssmix2.jpcore.gateway.core.canonical;

public record CanonicalCode(
        String codeSystem,
        String code,
        String display
) {
}

