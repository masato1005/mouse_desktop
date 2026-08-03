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
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("main.Main")
}