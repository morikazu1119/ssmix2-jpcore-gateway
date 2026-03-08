package org.ssmix2.jpcore.gateway.app.config;

import ca.uhn.fhir.context.FhirContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ssmix2.jpcore.gateway.app.service.AuditService;
import org.ssmix2.jpcore.gateway.app.service.GatewayIngestService;
import org.ssmix2.jpcore.gateway.app.store.BundleStore;
import org.ssmix2.jpcore.gateway.app.store.InMemoryBundleStore;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalModelAssembler;
import org.ssmix2.jpcore.gateway.core.canonical.DefaultCanonicalModelAssembler;
import org.ssmix2.jpcore.gateway.core.mapping.FhirBundleMapper;
import org.ssmix2.jpcore.gateway.core.parser.SimpleKeyValueSsmix2Parser;
import org.ssmix2.jpcore.gateway.core.parser.Ssmix2Parser;
import org.ssmix2.jpcore.gateway.core.service.ConversionPipeline;
import org.ssmix2.jpcore.gateway.core.validation.FhirValidationService;
import org.ssmix2.jpcore.gateway.core.validation.HapiFhirValidationService;
import org.ssmix2.jpcore.gateway.profiles.jp.JpCoreFhirBundleMapper;
import org.ssmix2.jpcore.gateway.profiles.jp.JpCoreMappingDefinitionCatalog;

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class GatewayConfiguration {

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    @Bean
    public Ssmix2Parser ssmix2Parser() {
        return new SimpleKeyValueSsmix2Parser();
    }

    @Bean
    public CanonicalModelAssembler canonicalModelAssembler() {
        return new DefaultCanonicalModelAssembler();
    }

    @Bean
    public JpCoreMappingDefinitionCatalog jpCoreMappingDefinitionCatalog() {
        return new JpCoreMappingDefinitionCatalog();
    }

    @Bean
    public FhirBundleMapper fhirBundleMapper(JpCoreMappingDefinitionCatalog catalog) {
        return new JpCoreFhirBundleMapper(catalog);
    }

    @Bean
    public FhirValidationService fhirValidationService(FhirContext fhirContext) {
        return new HapiFhirValidationService(fhirContext);
    }

    @Bean
    public ConversionPipeline conversionPipeline(
            Ssmix2Parser parser,
            CanonicalModelAssembler assembler,
            FhirBundleMapper mapper,
            FhirValidationService validationService
    ) {
        return new ConversionPipeline(parser, assembler, mapper, validationService);
    }

    @Bean
    public BundleStore bundleStore() {
        return new InMemoryBundleStore();
    }

    @Bean
    public AuditService auditService() {
        return new AuditService();
    }

    @Bean
    public GatewayIngestService gatewayIngestService(
            ConversionPipeline conversionPipeline,
            BundleStore bundleStore,
            AuditService auditService,
            GatewayProperties properties
    ) {
        return new GatewayIngestService(conversionPipeline, bundleStore, auditService, properties);
    }
}

