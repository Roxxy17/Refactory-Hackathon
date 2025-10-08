plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

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
    // Dependensi inti Android
    implementation("androidx.core:core-ktx:1.13.1") // Gunakan versi stabil
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1") // Gunakan versi stabil
    implementation("androidx.activity:activity-compose:1.9.0") // Gunakan versi stabil

    // --- SUMBER KEBENARAN UNTUK COMPOSE ---
    // HANYA SATU BOM VERSI TERBARU
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00") // Versi stabil terbaru
    implementation(composeBom)
    androidTestImplementation(composeBom)
    // -------------------------------------

    // Dependensi Compose (tanpa menentukan versi, karena sudah diatur oleh BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Ikon Material
    implementation("androidx.compose.material:material-icons-extended")

    // Navigasi
    implementation("androidx.navigation:navigation-compose:2.7.7") // Versi ini masih kompatibel

    // Dependensi untuk testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
