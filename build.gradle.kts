
plugins {
    id("com.github.ElytraServers.elytra-conventions")
    id("com.gtnewhorizons.gtnhconvention")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.processResources {
    filesMatching("META-INF/rfb-plugin/webnei.properties") {
        expand("version" to project.version)
    }
}

tasks.test {
    useJUnitPlatform()
}
