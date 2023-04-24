plugins {
    kotlin("jvm").version("1.8.20")
}

val pluginGroup: String by project
val pluginAuthor: String by project
val pluginVersion: String by project
val serverVersion: String by project

group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("net.kyori:adventure-api:4.13.1")
    compileOnly("io.papermc.paper:paper-api:$serverVersion")
}

tasks {
    processResources {
        val properties = mapOf("version" to pluginVersion, "author" to pluginAuthor)
        inputs.properties(properties)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml"){
            expand(properties)
        }
    }
    jar {
        from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
