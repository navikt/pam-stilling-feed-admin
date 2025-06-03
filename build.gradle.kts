import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.10"
    id("com.gradleup.shadow") version "8.3.2"
    application
}

application {
    mainClass.set("no.nav.pam.stilling.feed.admin.ApplicationKt")
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    archiveFileName.set("pam-stilling-feed-admin-all.jar")
    mergeServiceFiles()
}

val javalinVersion = "6.6.0"
val micrometerVersion = "1.14.6"
val jacksonVersion = "2.19.0"

dependencies {
    implementation("io.javalin:javalin:$javalinVersion")
    implementation("io.javalin:javalin-micrometer:$javalinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")

    implementation("io.micrometer:micrometer-core:$micrometerVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerVersion")

    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("net.logstash.logback:logstash-logback-encoder:8.1")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")
    implementation("org.codehaus.janino:janino:3.1.12")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("com.auth0:java-jwt:4.5.0")

    testImplementation(kotlin("test"))
    testImplementation("it.skrape:skrapeit:1.2.2")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("io.mockk:mockk:1.14.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
