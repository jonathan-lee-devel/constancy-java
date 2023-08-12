plugins {
    id("java")
}

group = "io.jonathanlee"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client:3.1.2")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.1.2")
    implementation("org.springframework.boot:spring-boot-starter-security:3.1.2")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}