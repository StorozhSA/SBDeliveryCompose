import config.SignHelper
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

// Plugins
plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.2.0"
}

group = "ru.skillbranch"
version = "1.0-SNAPSHOT"

android {
    compileSdk = 31

    // Default configuration
    defaultConfig {
        minSdk = 21
        targetSdk = 31
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    // Sign configuration
    signingConfigs {
        create("shared") {
            storeFile = file(SignHelper.keyStore ?: "test-keystore.jks")
            storePassword = SignHelper.keyStorePassword ?: "1234567890"
            keyAlias = SignHelper.keyAlias ?: "test_app"
            keyPassword = SignHelper.keyPassword ?: "1234567890"
        }
    }

    buildTypes {
        // Release build
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("shared")
            buildConfigField("String", "SERVER", "\"https://sandbox.skill-branch.ru/\"")
            multiDexEnabled = true
        }

        // Debug build
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // signingConfig = signingConfigs.getByName("shared")
            buildConfigField("String", "SERVER", "\"https://sandbox.skill-branch.ru/\"")
            multiDexEnabled = true
        }
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        freeCompilerArgs =
            freeCompilerArgs + "-Xexplicit-api=strict" + "-opt-in=kotlin.RequiresOptIn"
    }

    kotlin {
        // Explicit mode
        explicitApi = ExplicitApiMode.Strict
    }

    buildFeatures {
        viewBinding = true
        dataBinding = false
        compose = false
    }

    /*composeOptions {
        kotlinCompilerExtensionVersion = "1.0.3"
    }*/

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        // Include res if multi modules project
        unitTests.isIncludeAndroidResources = true
        // false - Generate exception if method not mocked
        unitTests.isReturnDefaultValues = false
    }

    ktlint {
        debug.set(false)
    }
}
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":common"))
    implementation(libs.okhttp3.main)
    implementation(libs.okhttp3.logging)
    implementation(libs.retrofit2.main)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    // Room
    kapt(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    testImplementation(libs.room.testing)

    // Paging
    implementation(libs.paging.common.ktx)
    implementation(libs.paging.runtime.ktx)
}
