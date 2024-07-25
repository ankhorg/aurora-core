import org.inksnow.ankhinvoke.gradle.ApplyReferenceTask
import org.inksnow.ankhinvoke.gradle.BuildMappingsTask

plugins {
    // java base
    id("java")
    id("java-library")
    id("maven-publish")

    // checks
    id("checkstyle")

    // ankh-invoke
    id("org.inksnow.ankh-invoke-gradle-plugin") version "1.0.13-SNAPSHOT"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    apply(plugin = "checkstyle")

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
                includeGroupAndSubgroups("org.inksnow")
            }
        }
    }

    checkstyle {
        toolVersion = "10.17.0"
        configDirectory = rootProject.file(".checkstyle")
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
    compileOnlyApi(project(":api"))
    implementation("org.inksnow:ankh-invoke-bukkit:1.0.13-SNAPSHOT")
    implementation("io.leangen.geantyref:geantyref:1.3.15")
    implementation("com.github.luben:zstd-jni:1.5.6-3")
    implementation("org.lz4:lz4-java:1.8.0")
    api("org.checkerframework.annotatedlib:guava:33.1.0.2-jre") {
        exclude("com.google.code.findbugs", "jsr305")
    }
    api("org.slf4j:slf4j-api:1.7.36")
    api("org.inksnow.cputil:logger:1.9")
    api("com.google.inject:guice:7.0.0") {
        exclude(group = "com.google.guava", module = "guava") // we use guava from spigot
    }
}

tasks.create<BuildMappingsTask>("build-mappings") {
    registryName = "aurora-core"
    outputDirectory = buildDir.resolve("cache/build-mappings")

    mapping("nms", "1.21") {
        predicates = arrayOf("craftbukkit_version:{v1_21_R1}")
    }
    mapping("nms", "1.20.6") {
        predicates = arrayOf("craftbukkit_version:{v1_20_R4}")
    }
    mapping("nms", "1.20.4") {
        predicates = arrayOf("craftbukkit_version:{v1_20_R3}")
    }
    mapping("nms", "1.20.2") {
        predicates = arrayOf("craftbukkit_version:{v1_20_R2}")
    }
    mapping("nms", "1.20.1") {
        predicates = arrayOf("craftbukkit_version:{v1_20_R1}")
    }
    mapping("nms", "1.19.4") {
        predicates = arrayOf("craftbukkit_version:{v1_19_R3}")
    }
    mapping("nms", "1.18.2") {
        predicates = arrayOf("craftbukkit_version:{v1_18_R2}")
    }
    mapping("nms", "1.17.1") {
        predicates = arrayOf("craftbukkit_version:{v1_17_R1}")
    }
}

tasks.processResources {
    dependsOn(tasks.getByName("build-mappings"))

    from(tasks.getByName("build-mappings").outputs)
}

tasks.create<ApplyReferenceTask>("apply-reference") {
    dependsOn(tasks.jar)

    appendReferencePackage("org.inksnow.core.impl.ref")
    inputJars = tasks.jar.get().outputs.files
    outputJar = file("$buildDir/runtimeLibs/aurora-core-$version.jar")
}

tasks.create<Copy>("copyLibs") {
    from(configurations.runtimeClasspath.get())
    into("$buildDir/runtimeLibs")
}

tasks.assemble {
    dependsOn(tasks["apply-reference"], tasks["copyLibs"])
}