import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.1.8.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.50"
	kotlin("plugin.spring") version "1.3.50"
}

group = "com.github.checketts"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencyManagement {
	dependencies {
		dependency("io.micrometer:micrometer-core:1.2.1")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.postgresql:postgresql")

	//Additions ==================================================================
	//Additions ==================================================================
	//Additions ==================================================================
	implementation("io.micrometer:micrometer-registry-prometheus:latest.release")  //Activates Prometheus Registry and Actuator
	implementation("com.github.ben-manes.caffeine:caffeine:2.8.0") // Cache library
	implementation("io.github.resilience4j:resilience4j-spring-boot2:1.1.0") // Circuit breaker library
	// ==========================================================================
	// ==========================================================================
	// ==========================================================================


	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
