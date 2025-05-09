// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
        //noinspection JcenterRepositoryObsolete
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    }
}

allprojects {
    repositories {
        google()
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
        //noinspection JcenterRepositoryObsolete
        jcenter()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
