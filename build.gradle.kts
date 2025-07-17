plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

group = "org.oife.passport"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.oife.passport.ApplicationKt")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.slf4j)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.openhtmltopdf.pdfbox)
    implementation(libs.openhtmltopdf.slf4j)
    implementation(libs.openhtmltopdf.svg)

    implementation(libs.markdown)

    implementation(libs.logback.classic)
    implementation(libs.logback.encoder)
    implementation(libs.janino)

    testImplementation(libs.kotest.runner)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}