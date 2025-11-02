object Versions {
    // App versions
    const val versionCode = 1
    const val versionName = "1.0.0"
    
    // SDK Versions
    const val compileSdk = 34
    const val minSdk = 26
    const val targetSdk = 34
    
    // Dependencies
    const val kotlinVersion = "1.9.10"
    const val composeVersion = "1.5.1"
    const val composeBomVersion = "2023.09.00"
    const val coroutinesVersion = "1.7.3"
    const val hiltVersion = "2.48"
    const val roomVersion = "2.5.2"
    const val lifecycleVersion = "2.6.2"
    const val navigationVersion = "2.7.3"
    
    // Testing
    const val junitVersion = "4.13.2"
    const val androidJunitVersion = "1.1.5"
    const val espressoVersion = "3.5.1"
    const val mockitoVersion = "5.5.0"
}

object Libs {
    // Kotlin
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}"
    
    // Android
    const val coreKtx = "androidx.core:core-ktx:1.12.0"
    const val appCompat = "androidx.appcompat:appcompat:1.6.1"
    const val material = "com.google.android.material:material:1.9.0"
    
    // Compose
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBomVersion}"
    const val composeUI = "androidx.compose.ui:ui"
    const val composeRuntime = "androidx.compose.runtime:runtime"
    const val composeMaterial3 = "androidx.compose.material3:material3"
    const val composeTooling = "androidx.compose.ui:ui-tooling-preview"
    const val composeActivity = "androidx.activity:activity-compose:1.7.2"
    
    // Navigation
    const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.navigationVersion}"
    
    // Lifecycle
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleVersion}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleVersion}"
    const val lifecycleCompose = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycleVersion}"
    
    // Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hiltVersion}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}"
    const val hiltNavigation = "androidx.hilt:hilt-navigation-compose:1.0.0"
    
    // Room
    const val roomRuntime = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"
    
    // Google ML Kit
    const val mlkitTextRecognition = "com.google.mlkit:text-recognition:16.0.0"
    
    // Testing
    const val junit = "junit:junit:${Versions.junitVersion}"
    const val androidJunit = "androidx.test.ext:junit:${Versions.androidJunitVersion}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espressoVersion}"
    const val mockito = "org.mockito:mockito-core:${Versions.mockitoVersion}"
    const val composeTestJunit4 = "androidx.compose.ui:ui-test-junit4"
    const val composeTestManifest = "androidx.compose.ui:ui-test-manifest"
    const val composeToolingTest = "androidx.compose.ui:ui-tooling"
}