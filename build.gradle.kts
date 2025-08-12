plugins {
	java
	jacoco
	`maven-publish`
	id("com.gradleup.shadow") version "9.0.0"
}

allprojects {
     plugins.apply("java")
     plugins.apply("jacoco")
     plugins.apply("maven-publish")
     plugins.apply("com.gradleup.shadow")
}

group = "com.github.jimbovm"
version = 0.0

repositories {
	mavenLocal()
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	// Logging
	implementation("org.apache.logging.log4j:log4j-api:2.17.+")
	implementation("org.apache.logging.log4j:log4j-core:2.17.+")
	// XML serialization
	implementation("org.glassfish.jaxb:jaxb-runtime:3.+")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.+")
	// Validation
	implementation("jakarta.validation:jakarta.validation-api:3.+")
	implementation("org.hibernate.validator:hibernate-validator:8.+")
	implementation("org.glassfish:jakarta.el:5.+")
	// Helpers
	implementation("org.apache.commons:commons-lang3:3.+")
	compileOnly("org.projectlombok:lombok:1.+")
	annotationProcessor("org.projectlombok:lombok:1.+")
	testCompileOnly("org.projectlombok:lombok:1.18.32")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
	// Testing
	testImplementation("org.junit.jupiter:junit-jupiter:5.+")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.glassfish:jakarta.el:5.+")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

tasks.withType<Javadoc> {
	source(sourceSets["main"].allJava)
	// Generate HTML5 Javadocs
	(options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
	(options as StandardJavadocDocletOptions).overview("src/main/java/overview.html")
}

tasks.named<Test>("test") {
	useJUnitPlatform()
	testLogging {
		events("passed", "failed", "skipped")
		setExceptionFormat(org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL)
		setShowStandardStreams(true)
	}
	finalizedBy("jacocoTestReport")
}
