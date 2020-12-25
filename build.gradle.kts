import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless



//plugins {
//    kotlin("jvm") version "1.4.10"
//    id("io.kotless") version "0.1.6" apply true
//}
// kotlin 1.4 not supported yet:(
// with io.kotless 0.1.7 will be supported
plugins {
    kotlin("jvm") version "1.3.72" apply true
    id("io.kotless") version "0.1.7-beta-4" apply true
}

group = "dev.pgordon.kotless"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("io.kotless", "spring-boot-lang", "0.1.6")
}

tasks.test {
    useJUnit()
}

//tasks.withType<KotlinCompile>() {
//    kotlinOptions.jvmTarget = "1.8"
//}
tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.3"
        apiVersion = "1.3"
    }
}

kotless {
    config {
        bucket = "my.kotless.bucket.for.memrise"

        terraform {
            profile = "my.kotless.user"
            region = "eu-west-1"

        }
    }
    webapp {
        lambda {
            kotless {
                packages = setOf("dev.pgordon.kotless")
            }
        }
    }

}