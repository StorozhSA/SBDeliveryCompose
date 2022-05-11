import config.SignHelper
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

// Plugins
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.6.10"
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.2.0"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

group = "ru.skillbranch"
version = "1.0-SNAPSHOT"

android {
    compileSdk = 31

    // Default configuration
    defaultConfig {
        applicationId = "ru.skillbranch.sbdelivery"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
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
            applicationIdSuffix = ".release"
            isMinifyEnabled = true
            isDebuggable = false
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
            applicationIdSuffix = ".debug"
            isMinifyEnabled = true
            isDebuggable = true
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
        jvmTarget = "1.8"
        freeCompilerArgs =
            freeCompilerArgs + "-Xexplicit-api=strict" + "-opt-in=kotlin.RequiresOptIn"
    }

    kotlin {
        explicitApi = ExplicitApiMode.Strict
    }

    buildFeatures {
        viewBinding = false
        dataBinding = false
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }

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
    implementation(project(":models")) {
        exclude(module = ":common")
    }

    implementation("com.google.maps.android:maps-compose:1.2.0")
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.maps.android:maps-ktx:3.3.0")

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.lifecycle)
    implementation(libs.google.material)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.retrofit2.main)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    // Room
    kapt(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    testImplementation(libs.room.testing)

    // Paging
    implementation(libs.paging.common.ktx)
    implementation(libs.paging.runtime.ktx)
    implementation(libs.paging.compose)

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    implementation("io.coil-kt:coil-compose:1.3.2")
    implementation("io.coil-kt:coil-svg:1.3.2")

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0")

    // Testing dependencies
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.espresso.contrib)
    // Testing dependencies Compose
    androidTestImplementation(libs.compose.test.junit)
    androidTestImplementation(libs.compose.test.common)

    testImplementation(libs.junit)

    debugImplementation(libs.compose.test.manifest)
    debugImplementation(libs.compose.tooling)
}

secrets {
    // MAPS_API_KEY=YOUR_API_KEY
    defaultPropertiesFileName = "local.properties"
}
