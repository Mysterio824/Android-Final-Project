plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.google.gms.google.services)
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.androidfinalproject.hacktok"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.androidfinalproject.hacktok"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/native-image/reflect-config.json",
                "META-INF/native-image/native-image.properties"
            )
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xlint:deprecation")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation (libs.kotlinx.coroutines.core.v164)
    implementation (libs.kotlinx.coroutines.android)
    // Use the Compose BOM to manage Compose dependency versions.
    val composeBom = platform("androidx.compose:compose-bom:2025.02.00")
    implementation(composeBom)
    testImplementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose UI and Material libraries (versions managed by the BOM)
    implementation(libs.material3)
    implementation(libs.androidx.compose.material.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.foundation)
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation("io.coil-kt:coil-compose:2.4.0")
    debugImplementation(libs.ui.tooling)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    coreLibraryDesugaring(libs.desugar.jdk.libs.v203)
    implementation(libs.kotlin.reflect)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation(libs.material.icons.extended)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.material.v190)

    // MongoDB dependencies
    implementation(libs.mongodb.driver.kotlin.coroutine)
    implementation(libs.mongodb.driver.sync.v490){
        exclude(group = "org.mongodb", module = "mongodb-driver-core")
    }
    implementation(libs.bson.kotlin)
    implementation(libs.realm.android.library)

    // Activity and Lifecycle support
    implementation(libs.androidx.activity.compose.v172)
    implementation(libs.androidx.lifecycle.viewmodel.compose.v261)
    implementation(libs.reactor.core)
    implementation(libs.slf4j.simple)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
    implementation(libs.mongodb.driver.kotlin.coroutine)

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Google Play Services - Add necessary ones back without versions for BOM management
    implementation("com.google.android.gms:play-services-auth")
    // implementation("com.google.android.gms:play-services-base:18.3.0") // Base might be transitively included
    // implementation("com.google.android.gms:play-services-identity:18.0.1") // Needed?
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    // Keep Credentials dependencies if needed and compatible, otherwise remove. Check Firebase/GMS docs.
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
}

