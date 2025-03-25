plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "rb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.googlecode.lanterna", "lanterna", "3.1.2" )
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}



application {
    mainClass = "netris.Main"
}

