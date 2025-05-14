import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    
}


val keystorePropertiesFile = file("app/keystore.properties").takeIf { it.exists() } 
    ?: rootProject.file("keystore.properties")

val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { load(it) }
    } else {
        error("No se encontró keystore.properties en ninguna ubicación válida")
    }
}

android {
    namespace = "com.arr.simple"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.arr.simple"
        minSdk = 23
        targetSdk = 34
        versionCode = 250509
        versionName = "2.1.5"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    signingConfigs {
    fun getRequiredProperty(key: String): String {
            return keystoreProperties.getProperty(key)?.trim() 
                ?: error("Propiedad faltante: '$key' en keystore.properties")
        }
        create("release") {
            keyAlias = keystoreProperties["KEY_ALIAS"] as String
            keyPassword = keystoreProperties["KEY_PASSWORD"] as String
            storeFile = file(keystoreProperties["STORE_FILE"] as String)
            storePassword = keystoreProperties["STORE_PASSWORD"] as String
        }
        
        getByName("debug") {
            keyAlias = keystoreProperties["KEY_ALIAS"] as String
            keyPassword = keystoreProperties["KEY_PASSWORD"] as String
            storeFile = file(keystoreProperties["STORE_FILE"] as String)
            storePassword = keystoreProperties["STORE_PASSWORD"] as String
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")

        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        
    }
    
}

dependencies {


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.work:work-runtime:2.9.1")
    
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.13.0-alpha10")
    
    
    implementation("androidx.navigation:navigation-ui:2.5.3")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("com.github.yuriy-budiyev:code-scanner:2.3.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.caverock:androidsvg:1.3")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    
    implementation(project(":api-nauta"))
    implementation("io.reactivex.rxjava3:rxjava:3.1.5")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation("com.google.code.gson:gson:2.11.0")
    
    implementation(project(":preference"))
    implementation(project(":apklis-api"))
    
    
    // qr code 
    implementation("com.google.zxing:core:3.3.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0") {
        isTransitive = false 
    }
}
