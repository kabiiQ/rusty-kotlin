import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "moe.kabii"
version = "1.1"

plugins {
    kotlin("jvm") version "1.3.40"
}

repositories {
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}