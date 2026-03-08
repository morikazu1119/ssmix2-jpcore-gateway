package org.ssmix2.jpcore.gateway.profiles.jp;

import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalPatient;
import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;
import org.ssmix2.jpcore.gateway.core.mapping.MapperResult;

public class JpCorePatientMapper extends AbstractJpCoreResourceMapper<CanonicalPatient, Patient> {

    public JpCorePatientMapper(JpCoreMappingDefinitionCatalog catalog, JpClinsExtensionSupport jpClinsExtensionSupport) {
        super(CanonicalResourceType.PATIENT, catalog, jpClinsExtensionSupport);
    }

    public MapperResult<Patient> map(CanonicalPatient source) {
        Patient target = new Patient();
        target.setId(new IdType("Patient", source.patientId()));
        MapperResult.Builder<Patient> resultBuilder = MapperResult.builder(target);

        target.setIdentifier(applyBaseMetadata(source, target, resultBuilder));
        warnIfUnusedCodes(source.standardCodes(), source.localCodes(), "codes", resultBuilder);

        if (source.familyName() == null || source.familyName().isBlank()) {
            resultBuilder.unresolvedField("familyName", "Patient familyName is missing.");
        }
        if (source.givenName() == null || source.givenName().isBlank()) {
            resultBuilder.unresolvedField("givenName", "Patient givenName is missing.");
        }

        target.addName()
                .setFamily(source.familyName())
                .addGiven(source.givenName());

        if (source.gender() != null && !source.gender().isBlank()) {
            target.setGender(Enumerations.AdministrativeGender.fromCode(source.gender()));
        } else {
            resultBuilder.warning("gender", "Patient gender is not mapped because the canonical value is blank.");
        }

        if (source.birthDate() != null) {
            target.setBirthDateElement(new org.hl7.fhir.r4.model.DateType(source.birthDate().toString()));
        } else {
            resultBuilder.warning("birthDate", "Patient birthDate is absent in the canonical model.");
        }

        return resultBuilder.build();
    }
}
