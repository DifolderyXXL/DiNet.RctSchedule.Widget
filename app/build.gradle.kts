plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)


    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}



android {
    namespace = "com.example.rctschedule"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.rctschedule"
        minSdk = 35
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmToolchain(21)
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

configurations.all {
    resolutionStrategy {
        // 1. Форсируем использование "пустой" версии listenablefuture, чтобы не было дубликатов
        force("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

        // 2. Форсируем единую современную версию Guava для всех библиотек (включая POI и Glance)
        force("com.google.guava:guava:33.3.1-android")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // For Glance support
    implementation(libs.androidx.glance)

    // For AppWidgets support
    implementation(libs.androidx.glance.appwidget)

    // For interop APIs with Material 3
    implementation (libs.androidx.glance.material3) {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    // For interop APIs with Material 2
    implementation (libs.androidx.glance.material) {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    implementation("androidx.activity:activity-compose:1.12.3")
    implementation("androidx.compose.material:material:1.10.2")



    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

}

dependencies {
    ksp("com.google.guava:guava:33.3.1-jre")
    // Core Apache POI library
    implementation("org.apache.poi:poi:5.5.0") // Check for the latest version

    // Add poi-ooxml for .xlsx file support (Office Open XML format)
    implementation("org.apache.poi:poi-ooxml:5.5.1") // Use the same version
}


dependencies{
    val room_version = "2.8.4"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")
}

dependencies{
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
}