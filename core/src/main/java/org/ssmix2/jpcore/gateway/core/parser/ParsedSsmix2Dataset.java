package org.ssmix2.jpcore.gateway.core.parser;

import java.util.List;

public record ParsedSsmix2Dataset(String facilityId, List<ParsedSsmix2Record> records) {
}

