plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.obdcloud.app"
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "com.obdcloud.app"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = Versions.versionCode
        versionName = Versions.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core modules
    implementation(project(":core:ui"))
    implementation(project(":core:design-system"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    
    // Feature modules
    implementation(project(":feature:adapter"))
    implementation(project(":feature:diagnostics"))
    implementation(project(":feature:vehicle"))
    implementation(project(":feature:coding"))
    implementation(project(":feature:service"))
    
    // Core Android dependencies
    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleRuntime)
    implementation(Libs.appCompat)
    implementation(Libs.material)
    
    // Compose
    implementation(platform(Libs.composeBom))
    implementation(Libs.composeUI)
    implementation(Libs.composeRuntime)
    implementation(Libs.composeMaterial3)
    implementation(Libs.composeTooling)
    implementation(Libs.composeActivity)
    
    // Navigation
    implementation(Libs.navigationCompose)
    
    // Hilt DI
    implementation(Libs.hiltAndroid)
    implementation(Libs.hiltNavigation)
    kapt(Libs.hiltCompiler)
    
    // Testing
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.espresso)
    androidTestImplementation(platform(Libs.composeBom))
    androidTestImplementation(Libs.composeTestJunit4)
    debugImplementation(Libs.composeTestManifest)
    debugImplementation(Libs.composeToolingTest)
}