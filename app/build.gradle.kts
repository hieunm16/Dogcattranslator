import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    jacoco
//    maven-publish
}
val apikeyPropertiesFile = rootProject.file("apikey.properties")
val apikeyProperties = Properties()
apikeyProperties.load(FileInputStream(apikeyPropertiesFile))

jacoco {
    toolVersion = "0.8.1"
}

android {
    namespace = "com.wa.dog.cat.sound.prank"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wa.dog.cat.sound.prank"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        val formattedDate = SimpleDateFormat("MM.dd.yyyy").format(Date())
        base.archivesBaseName = "AppDogAndCatTranslatorPlank${versionName}(${versionCode})_${formattedDate}"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
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
    bundle {
        language {
            enableSplit = false
        }
    }

    lintOptions {
        isCheckReleaseBuilds = false
    }

    kapt {
        correctErrorTypes = true
    }

    flavorDimensions.add("default")
    productFlavors {
        create("develop") {
            buildConfigField("String", "BASE_URL", apikeyProperties["BASE_URL_DEV"].toString())
            buildConfigField(
                "String",
                "ACCESS_TOKEN",
                apikeyProperties["ACCESS_TOKEN_DEV"].toString()
            )
        }

        create("production") {
            buildConfigField("String", "BASE_URL", apikeyProperties["BASE_URL_LIVE"].toString())
            buildConfigField(
                "String",
                "ACCESS_TOKEN",
                apikeyProperties["ACCESS_TOKEN_LIVE"].toString()
            )
        }
    }
}

dependencies {

    implementation("androidx.autofill:autofill:1.1.0")
    implementation("androidx.work:work-runtime:2.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Matterial
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    //Utils support
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.tbuonomo:dotsindicator:5.0")
    implementation("com.airbnb.android:lottie:6.1.0")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    //Room Database
    implementation("androidx.room:room-common:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    implementation("androidx.room:room-runtime:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    androidTestImplementation("androidx.room:room-testing:2.6.0")

    //Dimens
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.intuit.sdp:sdp-android:1.0.6")

    //ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    //multidex
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:31.2.3"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-config-ktx:21.4.1")
    implementation("com.google.firebase:firebase-database:20.3.0")

    //Remote config
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    //Ads
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    implementation("com.google.ads.mediation:facebook:6.16.0.0")
    implementation("com.google.ads.mediation:vungle:7.0.0.1")
    implementation("com.google.ads.mediation:ironsource:7.5.2.0")
    implementation("com.google.ads.mediation:applovin:11.11.3.0")
    implementation("com.google.ads.mediation:mintegral:16.5.51.0")
    implementation("com.google.ads.mediation:pangle:5.5.0.9.0")
    implementation("com.unity3d.ads:unity-ads:4.9.1")
    implementation("com.google.ads.mediation:unity:4.9.2.0")
    implementation("com.google.guava:guava:27.0.1-android")

    //Adjust
    implementation("com.adjust.sdk:adjust-android:4.33.5")
    implementation("com.android.installreferrer:installreferrer:2.2")
    // Add the following if you are using the Adjust SDK inside web views on your app
    implementation("com.adjust.sdk:adjust-android-webbridge:4.33.5")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.google.android.gms:play-services-appset:16.0.2")

    implementation("com.google.android.ump:user-messaging-platform:2.2.0")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.46.1")
    kapt("com.google.dagger:hilt-android-compiler:2.44")


}