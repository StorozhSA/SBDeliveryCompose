buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}

group = "ru.skillbranch"
version = "1.0-SNAPSHOT"

tasks.register("clean", Delete::class) {
    dependsOn(":lintFormatAllModules")
    delete(rootProject.buildDir)
    rootProject.childProjects.values.forEach {
        delete(it.buildDir)
    }
}

tasks.register("lintFormatAllModules", DefaultTask::class) {
    description = "Lint format all modules"
    dependsOn(":app:ktlintFormat", ":models:ktlintFormat", ":common:ktlintFormat")
}


/*tasks.register("testKt", tasks.MyKtTask::class) {

}*/


