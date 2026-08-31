plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "br.com.fiap.comidaviva"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "br.com.fiap.comidaviva"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Assinatura do APK de release.
    // Keystore acadêmico versionado junto ao projeto de propósito, para que a
    // entrega possa ser recompilada por qualquer avaliador. NÃO usar em produção.
    signingConfigs {
        create("release") {
            storeFile = rootProject.file("comidaviva.jks")
            storePassword = "comidaviva2026"
            keyAlias = "comidaviva"
            keyPassword = "comidaviva2026"
        }
    }

    buildTypes {
        release {
            // Sem isto o Gradle gera "app-release-unsigned.apk", que não instala.
            signingConfig = signingConfigs.getByName("release")

            // Liga o R8. Indispensável aqui: a biblioteca material-icons-extended
            // traz milhares de ícones e, sem otimização, o APK passa de 45 MB.
            // Com o R8 apenas os ícones realmente referenciados são empacotados.
            optimization {
                enable = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.geometry)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //Navigation Compose dependence
    implementation(libs.androidx.navigation.compose)
    // Dependência para a biblioteca estendida de ícones do Compose
    implementation("androidx.compose.material:material-icons-extended")
}