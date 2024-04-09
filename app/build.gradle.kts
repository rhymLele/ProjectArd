plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.projectard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projectard"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    //room for database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.activity:activity:1.8.0")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    //scalable size unit
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.intuit.ssp:ssp-android:1.1.1")

    //rounded image
    implementation ("com.makeramen:roundedimageview:2.3.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}