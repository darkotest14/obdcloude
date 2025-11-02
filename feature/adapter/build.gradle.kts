plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.obdcloud.feature.adapter"
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeVersion
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:design-system"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    
    // Core Android dependencies
    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleRuntime)
    implementation(Libs.lifecycleViewModel)
    
    // Compose
    implementation(platform(Libs.composeBom))
    implementation(Libs.composeUI)
    implementation(Libs.composeRuntime)
    implementation(Libs.composeMaterial3)
    implementation(Libs.composeTooling)
    implementation(Libs.lifecycleCompose)
    
    // Hilt
    implementation(Libs.hiltAndroid)
    implementation(Libs.hiltNavigation)
    kapt(Libs.hiltCompiler)
    
    // Testing
    testImplementation(Libs.junit)
    testImplementation(Libs.mockito)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.espresso)
    androidTestImplementation(platform(Libs.composeBom))
    androidTestImplementation(Libs.composeTestJunit4)
    debugImplementation(Libs.composeTestManifest)
    debugImplementation(Libs.composeToolingTest)
}