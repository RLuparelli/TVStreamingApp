plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.tvstreaming.app"
    compileSdk = 35
    buildToolsVersion = "35.0.1"

    defaultConfig {
        applicationId = "com.tvstreaming.app"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField("String", "API_BASE_URL", "\"http://172.29.19.189:3000/api/\"")
            buildConfigField("Boolean", "DEBUG_MODE", "true")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_BASE_URL", "\"https://api.tvstreaming.com/\"")
            buildConfigField("Boolean", "DEBUG_MODE", "false")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
    
    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
        warningsAsErrors = false
        disable.addAll(listOf("MissingTvBanner", "Deprecated", "MissingPermission"))
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    
    // Compose for TV
    implementation("androidx.tv:tv-foundation:1.0.0-alpha10")
    implementation("androidx.tv:tv-material:1.0.0-alpha10")
    
    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    
    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    
    // ExoPlayer
    implementation(libs.exoplayer)
    implementation(libs.exoplayer.hls)
    implementation(libs.exoplayer.ui)
    
    // DataStore
    implementation(libs.datastore.preferences)
    
    // Security
    implementation(libs.security.crypto)
    
    // Leanback for TV
    implementation(libs.leanback)
    
    // Gson
    implementation(libs.gson)
    
    // QR Code
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.1")
    
    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    
    // Timber for logging
    implementation("com.jakewharton.timber:timber:5.0.1")
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}