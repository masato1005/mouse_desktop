plugins {
    java
    application
    kotlin("jvm") version "2.4.10"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("net.java.dev.jna:jna-platform:5.19.1")
    implementation("net.java.dev.jna:jna:5.17.0")
    implementation("net.java.dev.jna:jna-platform:5.17.0")
    implementation("com.github.mmarquee:ui-automation:0.7.0")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("main.Main")
}
