plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.budilov.lambda.textract.invoker"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    val awsVersion = "1.12.173"
    val aws_v2_sdk_version = "2.17.143"

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    // Lambda
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.2")
    implementation("com.amazonaws:aws-lambda-java-log4j:1.0.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.11.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.11.0")

    // AWS
    implementation("software.amazon.awssdk:s3:$aws_v2_sdk_version")
    implementation("software.amazon.awssdk:textract:$aws_v2_sdk_version")
    implementation("com.amazonaws:aws-java-sdk-ssm:$awsVersion")
    implementation("software.amazon.awssdk:sqs:${aws_v2_sdk_version}")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.0")

    // GSON
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")

    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-api:2.19.0")

    }

tasks.jar {
    manifest.attributes["Main-Class"] = "com.budilov.lambda.textract.invoker.TextractAsyncInvoker"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree) // OR .map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}


tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to "com.budilov.lambda.textract.invoker.TextractAsyncInvoker")
        }
        from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
