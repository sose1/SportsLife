plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("de.mannodermaus.android-junit5") version "1.8.2.1"
    id("kotlin-android")
    kotlin("kapt")
}

android {
    namespace = "com.kwasowski.sportslife"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.kwasowski.sportslife"
        minSdk = 28
        targetSdk = 33

        versionCode = 1
        versionName = "0.0.1"

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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    kotlinOptions {
        jvmTarget = "18"
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    //core
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    //android
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //koin
    implementation("io.insert-koin:koin-android:3.4.0")

    //logger
    implementation("com.jakewharton.timber:timber:5.0.1")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:31.5.0"))
    implementation("com.google.firebase:firebase-analytics-ktx:21.2.2")
    implementation("com.google.firebase:firebase-auth-ktx:21.3.0")

    implementation("com.google.android.gms:play-services-auth:20.5.0")

    //tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}