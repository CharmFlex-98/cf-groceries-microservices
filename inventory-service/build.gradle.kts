plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
//	id("com.github.davidmc24.avro") version "1.9.1"
}

group = "com.charmflex.app.groceryapp"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

//sourceSets.main {
//	java.srcDir("build/generated-main-avro-java")
//	resources.srcDir("src/main/resources")
//}

repositories {
	mavenCentral()
}

dependencies {
//	implementation("io.confluent:kafka-avro-serializer:7.6.0")
//	implementation("io.confluent:kafka-schema-registry-client:7.6.0")
//	implementation("org.apache.avro:avro:1.11.3")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.kafka:spring-kafka")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
//tasks.withType<com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask> {
//	source("src/main/resources/avro")
//	outputDir.set(file("build/generated-avro-java"))
//}
