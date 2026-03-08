plugins {
    base
}

allprojects {
    group = "org.ssmix2.jpcore.gateway"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

