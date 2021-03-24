import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.puha.io/repo/")
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("me.clip:placeholderapi:2.10.9")
    compileOnly("io.puharesource.mc:TitleManager:2.2.0")

}

group = "net.craftersland"
version = "1.16.4"
java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks {
    withType<ShadowJar> {
        archiveFileName.set(rootProject.name + ".jar")
        relocate("kotlin", "com.github.secretx33.dependencies.kotlin")
        relocate("kotlinx", "com.github.secretx33.dependencies.kotlinx")
        relocate("org.jetbrains", "com.github.secretx33.dependencies.jetbrains")
        relocate("org.intellij", "com.github.secretx33.dependencies.jetbrains.intellij")
        exclude("DebugProbesKt.bin")
        exclude("META-INF/**")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}