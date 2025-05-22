plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pratica3mapa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pratica3mapa"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.fragment:fragment:1.6.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

configurations.all {
    exclude(group = "com.android.support", module = "support-compat")

    resolutionStrategy {
        force("androidx.core:core:1.13.0")
        force("androidx.appcompat:appcompat:1.6.1")
    }
}
