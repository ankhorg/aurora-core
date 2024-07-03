plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    compileOnly(project(":api"))
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT") {
        exclude(group = "com.google.guava", module = "guava") // we need checkerframework
    }
    compileOnly("org.checkerframework.annotatedlib:guava:33.1.0.2-jre") {
        exclude("com.google.code.findbugs", "jsr305")
    }

    implementation("org.inksnow.cputil:logger:1.8")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

tasks.shadowJar {
    relocate("org.inksnow.cputil", "aurora.core.loader.cputil")
    relocate("org.slf4j", "aurora.core.loader.slf4j")
    relocate("org.objectweb.asm", "aurora.core.loader.asm")

    mergeServiceFiles()
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
