import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.gradle.api.plugins.JavaPluginExtension
import fr.brouillard.oss.gradle.plugins.JGitverPluginExtension
import fr.brouillard.oss.gradle.plugins.JGitverPlugin

plugins {
    idea
    id("fr.brouillard.oss.gradle.jgitver")
    id("io.spring.dependency-management")
    id("org.springframework.boot") apply false
}

idea {
    project {
        languageLevel = IdeaLanguageLevel(17)
    }
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

allprojects {
    group = "work"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    val sockjs: String by project
    val stomp: String by project
    val bootstrap: String by project
    val springDocOpenapiUi: String by project
    val jsr305: String by project

    val r2dbcPostgresql: String by project

    apply(plugin = "io.spring.dependency-management")
    dependencyManagement {
        dependencies {
            imports {
                mavenBom(BOM_COORDINATES)
            }

            dependency("org.webjars:sockjs-client:$sockjs")
            dependency("org.webjars:stomp-websocket:$stomp")
            dependency("org.webjars:bootstrap:$bootstrap")
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocOpenapiUi")
            dependency("com.google.code.findbugs:jsr305:$jsr305")
            dependency("io.r2dbc:r2dbc-postgresql:$r2dbcPostgresql")
        }
    }

    configurations.all {
        resolutionStrategy {
            failOnVersionConflict()

            force("javax.servlet:servlet-api:2.4")
            force("commons-logging:commons-logging:1.1.1")
            force("commons-lang:commons-lang:2.5")
            force("org.codehaus.jackson:jackson-core-asl:1.8.8")
            force("org.codehaus.jackson:jackson-mapper-asl:1.8.3")
            force("org.codehaus.jettison:jettison:1.1")
            force("net.java.dev.jna:jna:5.8.0")
            force("com.google.errorprone:error_prone_annotations:2.7.1")
            force("org.ow2.asm:asm:9.4")
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:all,-serial,-processing", "-Werror"))
    }

    plugins.apply(JavaPlugin::class.java)
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    plugins.apply(JGitverPlugin::class.java)
    extensions.configure<JGitverPluginExtension> {
        strategy("PATTERN")
        nonQualifierBranches("main,master")
        tagVersionPattern("\${v}\${<meta.DIRTY_TEXT}")
        versionPattern(
                "\${v}\${<meta.COMMIT_DISTANCE}\${<meta.GIT_SHA1_8}" +
                        "\${<meta.QUALIFIED_BRANCH_NAME}\${<meta.DIRTY_TEXT}-SNAPSHOT"
        )
    }
}