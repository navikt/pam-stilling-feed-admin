plugins {
    kotlin("jvm") version "2.4.20"
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

val javalinVersion = "7.2.3"
val micrometerVersion = "1.17.1"
val jacksonVersion = "2.22.3"

dependencies {
    implementation("io.javalin:javalin:$javalinVersion")
    implementation("io.javalin:javalin-micrometer:$javalinVersion")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.31.1")
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0")

    implementation("io.micrometer:micrometer-core:$micrometerVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerVersion")

    implementation("ch.qos.logback:logback-classic:1.6.4")
    implementation("net.logstash.logback:logstash-logback-encoder:9.0")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")
    implementation("org.codehaus.janino:janino:3.1.12")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("com.auth0:java-jwt:4.6.1")

    testImplementation(kotlin("test"))
    testImplementation("it.skrape:skrapeit:1.2.2")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("io.mockk:mockk:1.14.11")
}

val skrapeitKompatibelCoroutinesVersjon = "1.6.4"

configurations.testRuntimeClasspath {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlinx" && requested.name.startsWith("kotlinx-coroutines")) {
            useVersion(skrapeitKompatibelCoroutinesVersjon)
            because("skrapeit 1.2.2 bruker Ktor 1.x, som krever klasser fjernet i kotlinx-coroutines 1.7")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(25)
}
