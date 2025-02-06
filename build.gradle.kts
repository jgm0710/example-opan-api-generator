import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask // openapi generator task


plugins {
    id("org.springframework.boot") version "2.7.12"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    id("org.openapi.generator") version "7.0.1" // openapi generator plugin
//    id("org.openapi.generator") version "6.6.0" // openapi generator plugin
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation ("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
}

sourceSets {
    main {
        java.srcDirs("$buildDir/generated")
    }
}

// api, invoker, model이 위치할 패키지 경로
val openApiPackages = listOf("openapi.api", "openapi.invoker", "openapi.model")

val dirs = mapOf(
    // 명세가 위치한 경로
    "openapi-specific" to "$rootDir/src/main/resources/openapi-specific",
    // 생성되는 코드들이 위치할 경로
    "openApiGenerate" to "$buildDir/openapi"
)

// 명세를 기반으로 코드를 생성하는 task들을 저장한다.
val generateOpenApiTasks: List<TaskProvider<GenerateTask>> = fileTree(dirs["openapi-specific"] ?: error("openapi-specific dir not found"))
    .files
    .filter { file -> (file.name.endsWith(".yaml") || file.name.endsWith(".yml")) }
    .map { file -> createOpenApiGenerateTask(file.name) }

fun createOpenApiGenerateTask(fileName: String): TaskProvider<GenerateTask> {
    return tasks.register<GenerateTask>("openApiGenerate_$fileName") {
        generatorName.set("spring")
        inputSpec.set("${dirs["openapi-specific"]}/$fileName")
        outputDir.set(dirs["openApiGenerate"] ?: error("Output directory not found"))
        apiPackage.set(openApiPackages[0])
        invokerPackage.set(openApiPackages[1])
        modelPackage.set(openApiPackages[2])

        // 다음 문서를 확인하여 적절한 옵션을 넣는다.
        // https://openapi-generator.tech/docs/generators/spring
        configOptions.set(
            mapOf(
                "dateLibrary" to "spring",
                "useTags" to "true",
                "openApiNullable" to "false",
                // API를 interface로 생성한다.
                "interfaceOnly" to "true",
//                "reactive" to "true" // WebFlux 설정
            )
        )
    }
}

tasks.register("createOpenApi") {
    doFirst {
        println("Creating Code By OpenAPI...")
    }
    doLast {
        println("OpenAPI Code created.")
    }
    // generateOpenApiTasks의 모든 Task에 의존한다.
    dependsOn(generateOpenApiTasks)
}

tasks.register("moveGeneratedSources") {
    doFirst {
        println("Moving generated sources...")
    }

    doLast {
        openApiPackages.forEach { packageName ->
            val packagePath = packageName.replace(".", "/")
            val originDir = file("${dirs["openApiGenerate"]}/src/main/java/$packagePath")
            val destinationDir = file("$buildDir/generated/$packagePath")
            copy {
                from(originDir)
                into(destinationDir)
            }
        }
        println("Generated sources moved.")
    }
    // 해당 작업은 createOpenApi Task에 의존한다.
    dependsOn("createOpenApi")
}

tasks.register("cleanGeneratedDirectory") {
    doFirst {
        println("Cleaning generated directory...")
    }
    doLast {
        val openApiGenerateDir = file(dirs["openApiGenerate"] ?: error("openApiGenerate directory not found"))
        if (openApiGenerateDir.exists()) {
            openApiGenerateDir.deleteRecursively()
            println("Directory [${openApiGenerateDir}] deleted.")
        } else {
            println("Directory [${openApiGenerateDir}] does not exist.")
        }
    }
    // 해당 작업은 moveGeneratedSources에 의존한다.
    dependsOn("moveGeneratedSources")
}

tasks.named("compileJava") {
    // 컴파일 이전에 코드 생성 작업이 수행된다.
    dependsOn("cleanGeneratedDirectory")
}

/* End Openapi generator setting */

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
