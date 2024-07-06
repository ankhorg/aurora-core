rootProject.name = "aurora-core"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://r.irepo.space/maven/") {
            content { includeGroupByRegex("^org\\.inksnow(\\..+|)\$") }
        }
    }
}

include("api")
include("plugin")
include("uploader")
include("devnms")
