// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    kotlin("plugin.serialization") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    alias(libs.plugins.google.gms.google.services) apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}