plugins {
    application
}

dependencies {
    implementation("org.inksnow.cputil:logger:1.6")
    implementation("com.google.code.gson:gson:2.11.0")
}

tasks.publish {
    enabled = false
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.inksnow.core.builder.AuroraUploader"
    }
}

tasks.getByName<JavaExec>("run") {
    workingDir(rootProject.projectDir)
    mainClass.set("org.inksnow.core.builder.AuroraUploader")
}