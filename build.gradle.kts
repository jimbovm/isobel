plugins {
	java
	jacoco
	signing
	`maven-publish`
	id("com.diffplug.spotless") version "8.+"
	// id("com.gradleup.shadow") version "9.0.0"
}

allprojects {
	plugins.apply("java")
	plugins.apply("jacoco")
	plugins.apply("maven-publish")
	// plugins.apply("com.gradleup.shadow")
}

group = "io.github.jimbovm"
version = "0.1.1"

repositories {
	mavenLocal()
	mavenCentral()
	gradlePluginPortal()
}

publishing {
	repositories {
		maven {
			name = "LocalStaging"
			url = uri(layout.buildDirectory.dir("staging-deploy"))
		}
	}
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			groupId = groupId
			artifactId = rootProject.name
			version = version
			pom {
				name = "Isobel"
				description = "A Java library for working with the level data of 1985's most popular platform game."
				url = "https://github.com/jimbovm/isobel"
				licenses {
					license {
						name = "MIT No Attribution"
						url = "https://spdx.org/licenses/MIT-0.html"
					}
				}
				developers {
					developer {
						id = "jimbovm"
						name = "Jimbo Brierley"
						email = "tdnwl216x@mozmail.com"
					}
				}
				scm {
					connection = "scm:git:git://github.com/jimbovm/isobel.git"
					developerConnection = "scm:git:git://github.com/jimbovm/isobel.git"
					url = "https://github.com/jimbovm/isobel"
				}
			}
		}
	}
}

spotless {
	java {
		eclipse().configFile("formatter.xml")
		removeUnusedImports()
		importOrder("java|javax", "jakarta", "lombok", "", "io.github.jimbovm.isobel")
		licenseHeader("/*\n * SPDX-License-Identifier: MIT-0\n *\n * This file is part of Isobel (https://github.com/jimbovm/isobel).\n */\n\n")
	}
}

dependencies {
	// Logging
	implementation("org.apache.logging.log4j:log4j-api:[2.17,)")
	implementation("org.apache.logging.log4j:log4j-core:[2.17,)")
	// XML serialization
	implementation("org.glassfish.jaxb:jaxb-runtime:[3.0,)")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:[3.0,)")
	// Validation
	implementation("jakarta.validation:jakarta.validation-api:[3.0,)")
	implementation("org.hibernate.validator:hibernate-validator:[8.0,)")
	implementation("org.glassfish:jakarta.el:[5.0,)")
	// Helpers
	implementation("org.apache.commons:commons-lang3:[3.0,)")
	compileOnly("org.projectlombok:lombok:[1.0,)")
	annotationProcessor("org.projectlombok:lombok:[1.18,)")
	testCompileOnly("org.projectlombok:lombok:[1.18,)")
	testAnnotationProcessor("org.projectlombok:lombok:[1.18,)")
	// Testing
	testImplementation("org.junit.jupiter:junit-jupiter:[5.0,)")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.glassfish:jakarta.el:[5.0,)")
}

repositories {
	mavenLocal()
	mavenCentral()		
}

signing {
	useGpgCmd()
	val signingKey = providers.gradleProperty("signingKey").orNull?.replace("\\n", "\n") ?: System.getenv("SIGNING_KEY")?.replace("\\n", "\n")
	val signingPassword = providers.gradleProperty("signingPassword")
	sign(publishing.publications["mavenJava"])
}

java {
	withSourcesJar()
	withJavadocJar()
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

tasks.withType<Javadoc> {
	source(sourceSets["main"].allJava)
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
