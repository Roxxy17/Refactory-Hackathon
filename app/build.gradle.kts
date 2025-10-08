plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}


android {
    namespace = "com.example.kalanacommerce"
    compileSdk = 36 // DIUBAH: Gunakan SDK stabil terbaru, yaitu 34

    defaultConfig {
        applicationId = "com.example.kalanacommerce"
        minSdk = 27
        targetSdk = 36 // DIUBAH: Sesuaikan dengan compileSdk
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
        sourceCompatibility = JavaVersion.VERSION_1_8 // DIUBAH: Standar untuk Compose adalah 1.8
        targetCompatibility = JavaVersion.VERSION_1_8 // DIUBAH: Standar untuk Compose adalah 1.8
    }
    kotlinOptions {
        jvmTarget = "1.8" // DIUBAH: Sesuaikan dengan compileOptions
    }
    buildFeatures {
        compose = true
    }

}
val nav_version = "2.7.7"

dependencies {
    // Dependensi inti Android (versi stabil)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences.core)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation(libs.androidx.compose.ui.geometry)

    // Compose Bill of Materials (BOM) - Sumber kebenaran versi Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Dependensi Compose (versi diatur oleh BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigasi
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Koin (Dependency Injection)
    // PERBAIKAN: Menggunakan versi Koin yang valid dan stabil
    val koinVersion = "3.5.6"
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")

    // Retrofit & Networking (OkHttp)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Kotlinx Serialization & Converter
    // PENINGKATAN: Menggunakan versi terbaru yang lebih kompatibel
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0") // Ganti versi jika perlu
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3") // Gunakan versi terbaru


    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")

    implementation("io.ktor:ktor-client-core:2.3.11")
    implementation("io.ktor:ktor-client-android:2.3.11")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
    implementation("io.ktor:ktor-client-logging:2.3.11")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

}