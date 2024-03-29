plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("de.mannodermaus.android-junit5") version "1.8.2.1"
    id("kotlin-android")
    kotlin("kapt")
}

android {
    namespace = "com.kwasowski.sportslife"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kwasowski.sportslife"
        minSdk = 28
        targetSdk = 34

        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
        viewBinding = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
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
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("pub.devrel:easypermissions:3.0.0")

    //koin
    implementation("io.insert-koin:koin-android:3.4.0")

    //logger
    implementation("com.jakewharton.timber:timber:5.0.1")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:31.5.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation("com.google.android.gms:play-services-auth:20.5.0")

    //tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("io.mockk:mockk:1.13.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0-alpha01")
    androidTestImplementation("androidx.test.ext:junit:1.2.0-alpha01")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}