plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api("ca.uhn.hapi.fhir:hapi-fhir-base:8.6.0")
    api("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:8.6.0")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-validation:8.6.0")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-validation-resources-r4:8.6.0")

    testImplementation(platform("org.junit:junit-bom:5.12.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
