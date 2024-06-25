plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")

    api(project(":api"))

    implementation("org.inksnow.cputil:logger:1.6")
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
