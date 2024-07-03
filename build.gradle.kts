plugins {
    // java base
    id("java")
    id("java-library")
    id("maven-publish")

    // checks
    id("checkstyle")
    id("org.checkerframework") version "0.6.42"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    apply(plugin = "checkstyle")
    apply(plugin = "org.checkerframework")

    group = "org.inksnow.core"
    version = System.getenv("BUILD_NUMBER")
        ?.let { "1.$it" }
        ?: "1.0-SNAPSHOT"

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        withSourcesJar()
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
            content {
                includeGroup("org.spigotmc")
                includeGroup("org.bukkit")
            }
        }
        maven("https://oss.sonatype.org/content/repositories/snapshots") {
            content {
                includeModule("net.md-5", "bungeecord-chat")
                includeModule("net.md-5", "bungeecord-parent")
            }
        }
        maven("https://r.irepo.space/maven") {
            content {
                includeGroup("org.inksnow.cputil")
            }
        }
    }

    checkstyle {
        toolVersion = "10.17.0"
        configDirectory = rootProject.file(".checkstyle")
    }

    checkerFramework {
        checkers = listOf(
            "org.checkerframework.checker.nullness.NullnessChecker",
            "org.checkerframework.checker.units.UnitsChecker"
        )
    }

    dependencies {
        // annotations
        compileOnly("org.checkerframework:checker-qual:3.45.0")
        testCompileOnly("org.checkerframework:checker-qual:3.45.0")

        // lombok
        compileOnly("org.projectlombok:lombok:1.18.32")
        annotationProcessor("org.projectlombok:lombok:1.18.32")
        testCompileOnly("org.projectlombok:lombok:1.18.32")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                if (rootProject == project) {
                    artifactId = "impl"
                }
                from(components["java"])
            }
        }

        repositories {
            if (System.getenv("CI").toBoolean()) {
                maven("https://s0.blobs.inksnow.org/maven/") {
                    credentials {
                        username = System.getenv("IREPO_USERNAME")
                        password = System.getenv("IREPO_PASSWORD")
                    }
                }
            } else {
                maven(rootProject.buildDir.resolve("repo"))
            }

        }
    }

    tasks.test {
        useJUnitPlatform()
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT") {
        exclude(group = "com.google.guava", module = "guava") // we need checkerframework
    }
    compileOnly(project(":api"))

    compileOnly("org.checkerframework.annotatedlib:guava:33.1.0.2-jre") {
        exclude("com.google.code.findbugs", "jsr305")
    }
    api("org.slf4j:slf4j-api:1.7.36")
    api("org.inksnow.cputil:logger:1.9")
    api("com.google.inject:guice:7.0.0") {
        exclude(group = "com.google.guava", module = "guava") // we use guava from spigot
    }
}

tasks.create<Copy>("copyLibs") {
    from(configurations.runtimeClasspath.get())
    into("$buildDir/runtimeLibs")
}

tasks.assemble {
    dependsOn(tasks["copyLibs"])
}