plugins {
    id("com.android.library")
}

android {
    compileSdk = 34
    namespace = "cu.arr.etecsa.api"

    defaultConfig {
        minSdk = 21
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    
    
    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") 
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    
    // moshi
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.moshi:moshi-adapters:1.15.1")
    
    // okHttp3
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // svg 
    implementation("com.caverock:androidsvg:1.3")
    
    // decode token
    implementation("com.auth0.android:jwtdecode:2.0.2")
    
    // jsoup analizar html
    implementation("org.jsoup:jsoup:1.15.4")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}