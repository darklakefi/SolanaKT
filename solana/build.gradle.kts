plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "2.0.21"
    `maven-publish`
}

group = "com.solana"
version = "2.1.1"

android {
    namespace = "com.solana.solana"
    compileSdk = 36

    defaultConfig {
        minSdk = 19
        targetSdk = 36
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // read RPC URL properties for testing
        val defaultRpcUrl = project.properties["testing.rpc.defaultUrl"]
        val rpcUrl = project.properties["rpcUrl"] ?: defaultRpcUrl

        val useLocalValidator = project.properties["localValidator"]?.toString()?.toBoolean() ?: false
        val localRpcUrl = project.properties["testing.rpc.localUrl"]
        val finalRpcUrl = if (useLocalValidator && localRpcUrl != null) localRpcUrl else rpcUrl

        // set RPC URL (used in testing)
        buildConfigField("String", "RPC_URL", "\"${finalRpcUrl.toString().replace("\"", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("org.bitcoinj:bitcoinj-core:0.16.2")
    implementation("net.i2p.crypto:eddsa:0.3.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(layout.buildDirectory.file("outputs/aar/solana-release.aar")) {
                builtBy(tasks.getByName("assemble"))
            }
            artifactId = "solana"
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                configurations.implementation.get().allDependencies.forEach {
                    if (it.group != null) {
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", it.version)
                    }
                }
            }
        }
    }
}