dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT") {
        exclude(group = "com.google.guava", module = "guava") // we need checkerframework
    }

    compileOnly("org.checkerframework.annotatedlib:guava:33.1.0.2-jre")
}
