plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.obdcloud.core.data"
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
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    
    implementation(Libs.kotlinStdLib)
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)
    
    // Hilt
    implementation(Libs.hiltAndroid)
    kapt(Libs.hiltCompiler)
    
    // Room
    implementation(Libs.roomRuntime)
    implementation(Libs.roomKtx)
    kapt(Libs.roomCompiler)
    
    // Testing
    testImplementation(Libs.junit)
    testImplementation(Libs.mockito)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.espresso)
}