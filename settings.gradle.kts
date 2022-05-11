enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            alias("ktLint-main").toPluginId("org.jlleitschuh.gradle.ktlint").version("10.2.0")

            val composeVersion = "1.1.1"
            alias("compose-activity").to("androidx.activity:activity-compose:1.3.1")
            alias("compose-foundation").to("androidx.compose.foundation:foundation:$composeVersion")
            alias("compose-material-common").to("androidx.compose.material:material:$composeVersion")
            alias("compose-material-icons-core").to("androidx.compose.material:material-icons-core:$composeVersion")
            alias("compose-material-icons-ext").to("androidx.compose.material:material-icons-extended:$composeVersion")
            alias("compose-preview").to("androidx.compose.ui:ui-tooling-preview:$composeVersion")
            alias("compose-runtime-livedata").to("androidx.compose.runtime:runtime-livedata:$composeVersion")
            alias("compose-runtime-rxjava2").to("androidx.compose.runtime:runtime-rxjava2:$composeVersion")
            alias("compose-tooling").to("androidx.compose.ui:ui-tooling:$composeVersion")
            alias("compose-ui").to("androidx.compose.ui:ui:$composeVersion")
            alias("compose-test-common").to("androidx.compose.ui:ui-test:$composeVersion")
            alias("compose-test-junit").to("androidx.compose.ui:ui-test-junit4:$composeVersion")
            alias("compose-test-manifest").to("androidx.compose.ui:ui-test-manifest:$composeVersion")



            bundle(
                "compose",
                listOf(
                    "compose-ui",
                    "compose-material-common",
                    "compose-material-icons-core",
                    "compose-material-icons-ext",
                    "compose-preview",
                    "compose-activity",
                    "compose-foundation",
                    "compose-runtime-livedata",
                    "compose-runtime-rxjava2"
                )
            )

            alias("androidx-core").to("androidx.core:core-ktx:1.6.0")
            alias("androidx-appcompat").to("androidx.appcompat:appcompat:1.3.1")
            alias("google-material").to("com.google.android.material:material:1.5.0")
            alias("junit").to("junit:junit:4.13.2")
            alias("androidx-test-junit").to("androidx.test.ext:junit:1.1.3")
            alias("androidx-test-espresso-core").to("androidx.test.espresso:espresso-core:3.4.0")
            alias("androidx-test-espresso-contrib").to("androidx.test.espresso:espresso-contrib:3.4.0")

            // JSON Moshi
            val moshiVersion = "1.13.0"
            alias("moshi-main").to("com.squareup.moshi:moshi:$moshiVersion")
            alias("moshi-kotlin").to("com.squareup.moshi:moshi-kotlin:$moshiVersion")
            alias("moshi-codegen").to("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

            // Retrofit2
            val retrofitVersion = "2.9.0"
            alias("retrofit2-main").to("com.squareup.retrofit2:retrofit:$retrofitVersion")
            alias("retrofit2-converter-jackson").to("com.squareup.retrofit2:converter-jackson:$retrofitVersion")
            alias("retrofit2-converter-moshi").to("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

            // OkHttp
            val okHttpVersion = "4.9.2"
            alias("okhttp3-main").to("com.squareup.okhttp3:okhttp:$okHttpVersion")
            alias("okhttp3-logging").to("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")

            // Kotlin coroutines
            version("coroutines", "1.6.0")
            alias("kotlinx-coroutines-android").to(
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-android"
            )
                .versionRef("coroutines")

            // Livecycle
            val lifecycleVersion = "2.4.0"
            alias("lifecycle-common-java8").to("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
            alias("lifecycle-livedata-core-ktx").to("androidx.lifecycle:lifecycle-livedata-core-ktx:$lifecycleVersion")
            alias("lifecycle-livedata-ktx").to("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
            alias("lifecycle-process").to("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
            alias("lifecycle-reactivestreams-ktx").to("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycleVersion")
            alias("lifecycle-runtime-ktx").to("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
            alias("lifecycle-service").to("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
            alias("lifecycle-viewmodel-ktx").to("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
            alias("lifecycle-viewmodel-compose").to("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
            alias("lifecycle-viewmodel-savedstate").to("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")

            bundle(
                "lifecycle",
                listOf(
                    "lifecycle-common-java8",
                    "lifecycle-livedata-core-ktx",
                    "lifecycle-livedata-ktx",
                    "lifecycle-process",
                    "lifecycle-reactivestreams-ktx",
                    "lifecycle-runtime-ktx",
                    "lifecycle-service",
                    "lifecycle-viewmodel-compose",
                    "lifecycle-viewmodel-ktx",
                    "lifecycle-viewmodel-savedstate",
                )
            )

            //Hilt
            val hiltVersion = "2.40.5"
            alias("hilt-android").to("com.google.dagger:hilt-android:$hiltVersion")
            alias("hilt-android-compiler").to("com.google.dagger:hilt-android-compiler:$hiltVersion")
            alias("hilt-navigation-compose").to("androidx.hilt:hilt-navigation-compose:1.0.0")

            //Navigation
            val navigationVersion = "2.4.0"
            alias("navigation-fragment-ktx").to("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
            alias("navigation-ui-ktx").to("androidx.navigation:navigation-ui-ktx:$navigationVersion")
            alias("navigation-compose").to("androidx.navigation:navigation-compose:$navigationVersion")

            //Room
            val roomVersion = "2.4.1"
            alias("room-compiler").to("androidx.room:room-compiler:$roomVersion")
            alias("room-runtime").to("androidx.room:room-runtime:$roomVersion")
            alias("room-ktx").to("androidx.room:room-ktx:$roomVersion")
            alias("room-testing").to("androidx.room:room-testing:$roomVersion")
            alias("room-paging").to("androidx.room:room-paging:$roomVersion")

            // Paging
            val pagingVersion = "3.1.0"
            alias("paging-common-ktx").to("androidx.paging:paging-common-ktx:$pagingVersion")
            alias("paging-runtime-ktx").to("androidx.paging:paging-runtime-ktx:$pagingVersion")
            alias("paging-compose").to("androidx.paging:paging-compose:1.0.0-alpha14")
        }
    }

}

rootProject.name = "SBDelivery"
include(":app")
include(":models")
include(":common")
