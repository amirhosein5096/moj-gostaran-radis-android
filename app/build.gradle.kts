plugins {
    id("com.android.application")
}

android {
    namespace = "com.radis.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.radis.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.2.0"
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.activity:activity-ktx:1.10.0")
}
