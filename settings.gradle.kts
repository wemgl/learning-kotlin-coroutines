plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "learning-kotlin-coroutines"

include("basics")
includeBuild("intro-coroutines")
