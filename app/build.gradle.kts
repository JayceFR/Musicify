plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.musicify"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.musicify"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //Extra stuff added
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("jp.wasabeef:recyclerview-animators:4.0.2")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.google.android.exoplayer:extension-mediasession:2.19.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}