plugins {
    id("org.checkerframework") version "0.6.42"
}

checkerFramework {
    checkers = listOf(
        "org.checkerframework.checker.nullness.NullnessChecker",
        "org.checkerframework.checker.units.UnitsChecker"
    )
    extraJavacArgs = listOf(
        "-AskipDefs=^org\\.inksnow\\.core\\.impl\\.ref\\.",
        "-Awarns"
    )
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT") {
        exclude(group = "com.google.guava", module = "guava") // we need checkerframework
    }

    compileOnly("org.checkerframework.annotatedlib:guava:33.1.0.2-jre")

    api("io.leangen.geantyref:geantyref:1.3.15")
}
