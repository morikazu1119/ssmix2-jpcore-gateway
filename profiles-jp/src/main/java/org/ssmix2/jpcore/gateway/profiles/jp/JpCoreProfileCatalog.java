package org.ssmix2.jpcore.gateway.profiles.jp;

import org.ssmix2.jpcore.gateway.core.canonical.CanonicalResourceType;

import java.util.EnumMap;
import java.util.Map;

public final class JpCoreProfileCatalog {

    private static final Map<CanonicalResourceType, String> PROFILE_URLS = new EnumMap<>(CanonicalResourceType.class);

    static {
        PROFILE_URLS.put(CanonicalResourceType.PATIENT, "http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient");
        PROFILE_URLS.put(CanonicalResourceType.ENCOUNTER, "http://jpfhir.jp/fhir/core/StructureDefinition/JP_Encounter");
        PROFILE_URLS.put(CanonicalResourceType.OBSERVATION, "http://jpfhir.jp/fhir/core/StructureDefinition/JP_Observation_Common");
        PROFILE_URLS.put(CanonicalResourceType.MEDICATION_REQUEST, "http://jpfhir.jp/fhir/core/StructureDefinition/JP_MedicationRequest");
        PROFILE_URLS.put(CanonicalResourceType.DOCUMENT_REFERENCE, "http://jpfhir.jp/fhir/core/StructureDefinition/JP_DocumentReference");
    }

    private JpCoreProfileCatalog() {
    }

    public static String profileUrl(CanonicalResourceType resourceType) {
        String profileUrl = PROFILE_URLS.get(resourceType);
        if (profileUrl == null) {
            throw new IllegalArgumentException("Unsupported JP Core profile type: " + resourceType);
        }
        return profileUrl;
    }
}

