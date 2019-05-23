import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.31"
    antlr
    idea
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    antlr("org.antlr:antlr4:4.7")
    implementation("org.antlr:antlr4-runtime:4.7")
}

val genDir = file("src/gen")

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

sourceSets["main"].java {
    srcDir(genDir)
}

idea {
    module {
        generatedSourceDirs.add(genDir)
    }
}

tasks.withType<AntlrTask> {
    outputDirectory = genDir
    arguments.addAll(listOf(
        "-visitor",
        "-no-listener",
        "-long-messages",
        "-Werror"))
}

tasks.named<Jar>("jar") {
    manifest.attributes["Main-Class"] = "EditorKt"
    from(configurations.runtimeClasspath.map { if (it.isDirectory) it as Any else zipTree(it) })
    from(sourceSets["main"].output)
    archiveName = "editor.jar"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}