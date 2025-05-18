plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //Ksp
    id("com.google.devtools.ksp")
    //Hilt
    id("com.google.dagger.hilt.android")
    // Safe Args
    id("androidx.navigation.safeargs")
    // Kotlin Parcelize
    id("kotlin-parcelize")
    // Google Service
    id("com.google.gms.google-services")
}

android {
    namespace = "ir.mahan.histore"
    compileSdk = 35

    defaultConfig {
        applicationId = "ir.mahan.histore"
        minSdk = 29
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

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
        /*sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11*/
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    buildToolsVersion = "35.0.0"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    // google Services
    implementation("com.google.android.gms:play-services-base:18.7.0")
    implementation("com.google.android.gms:play-services-auth-api-phone:18.2.0")
    // Hilt-Dagger
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    // ROOM
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    // Lifecycle
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    // Fragment
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    // Navigation
    implementation("androidx.navigation:navigation-ui-ktx:2.8.9")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.6")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.code.gson:gson:2.10.1")
    //OkHTTP client
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")
    //Image Loading
    implementation ("io.coil-kt:coil:2.6.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    // Timber
    implementation ("com.jakewharton.timber:timber:5.0.1")
    // Calligraphy
    implementation ("io.github.inflationx:calligraphy3:3.1.1")
    implementation ("io.github.inflationx:viewpump:2.0.3")
    // Lottie
    implementation ("com.airbnb.android:lottie:6.1.0")
    // Shimmer
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("com.github.omtodkar:ShimmerRecyclerView:v0.4.1")
    // Other
    implementation ("com.github.MrNouri:DynamicSizes:1.0")
    implementation ("kr.co.prnd:readmore-textview:1.0.0")
    implementation ("com.github.GoodieBag:Pinview:v1.5")
    implementation ("me.relex:circleindicator:2.1.6")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.github.aliab:Persian-Date-Picker-Dialog:1.8.0")
    //implementation ("com.github.SimformSolutionsPvtLtd:SSImagePicker:2.1")
    implementation ("com.github.sparrow007:carouselrecyclerview:1.2.6")
}