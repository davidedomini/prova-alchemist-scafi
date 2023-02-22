plugins {
    java
    scala
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    //implementation("org.scala-lang:scala3-library_3:3.2.2")
    implementation("org.scala-lang:scala-library:2.11.12")
    implementation("it.unibo.alchemist:alchemist:25.7.1")
    implementation("it.unibo.alchemist:alchemist-incarnation-scafi:25.7.1")
    implementation("it.unibo.alchemist:alchemist-incarnation-protelis:25.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runExample"){
    mainClass.set("it.unibo.TestLauncher")
    classpath = sourceSets["main"].runtimeClasspath
}