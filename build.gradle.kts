@file:Suppress("UNUSED_VARIABLE")

val ktor_version = "2.2.3"

plugins {
    kotlin("multiplatform") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    id("org.jetbrains.compose") version "1.3.0"
    application
}

group = "io.github.krxwallo"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven("https://jitpack.io")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("com.github.krxwallo.synk:synk:1.0.0")
                implementation("ch.qos.logback:logback-classic:1.4.5")
                implementation(compose.runtime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:2.2.1")
                implementation("io.ktor:ktor-server-default-headers:$ktor_version")
                implementation("io.ktor:ktor-server-websockets:$ktor_version")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-js:$ktor_version")
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("io.github.krxwallo.application.ServerKt")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}
dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.2.3")
    implementation("io.ktor:ktor-server-websockets-jvm:2.2.3")
}
