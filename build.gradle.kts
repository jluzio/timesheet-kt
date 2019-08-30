import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.1.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.7.RELEASE"
	kotlin("jvm") version "1.3.50"
	kotlin("plugin.spring") version "1.3.50"
}

group = "org.example.apps"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
	runtimeClasspath {
		extendsFrom(developmentOnly)
	}
}

configurations.all {
	// unable to exclude from implementation
	exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-json")
	implementation("org.springframework.boot:spring-boot-starter-log4j2")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("javax.inject:javax.inject:1")
	implementation("com.google.guava:guava:28.0-jre")
	implementation("org.apache.poi:poi:4.1.0")
	implementation("javax.xml.bind:jaxb-api:2.3.0")
	implementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")
	implementation("io.github.threeten-jaxb:threeten-jaxb-core:1.2")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
