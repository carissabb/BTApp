plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val tomtomApiKey: String by project

android {
    namespace = "com.example.btapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.btapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {

        jniLibs.pickFirsts.add("lib/**/libc++_shared.so")

    }

    buildFeatures {

        buildConfig = true

    }

    buildTypes.configureEach {

        buildConfigField("String", "TOMTOM_API_KEY", "\"$tomtomApiKey\"")

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.3")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("androidx.work:work-runtime-ktx:2.9.1")
    implementation ("org.simpleframework:simple-xml:2.7.1")
    implementation ("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("org.json:json:20210307")
    implementation("com.google.android.libraries.mapsplatform.transportation:transportation-consumer:2.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    //moshi
    implementation ("com.squareup.moshi:moshi-kotlin:1.14.0") // For Kotlin support
    implementation ("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0") // Retrofit Moshi converter
    //jackson
    implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0") // Check for the latest version
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.18.0")
    implementation ("com.squareup.retrofit2:converter-jackson:2.9.0") // or latest version

    // for bottom nav
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.8.3")
    implementation ("androidx.navigation:navigation-ui-ktx:2.8.3")
    val version = "1.18.1"
    implementation("com.tomtom.sdk.maps:map-display:$version")

}