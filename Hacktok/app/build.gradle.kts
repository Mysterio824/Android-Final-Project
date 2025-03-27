plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.google.gms.google.services)
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
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    //    implementation("androidx.room:room-ktx:2.6.1") nay la cgi z
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
    debugImplementation(libs.ui.tooling)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    coreLibraryDesugaring(libs.desugar.jdk.libs.v203)
    implementation(libs.kotlin.reflect)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.0")


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
}