package org.ssmix2.jpcore.gateway.core.canonical;

import org.ssmix2.jpcore.gateway.core.parser.ParsedSsmix2Dataset;

public interface CanonicalModelAssembler {
    CanonicalDataSet assemble(ParsedSsmix2Dataset parsedDataset);
}

