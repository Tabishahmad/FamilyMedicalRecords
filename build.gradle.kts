//buildscript {
//    dependencies {
//        classpath("com.google.gms:google-services:4.4.1")
//    }
//}
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}
allprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name.contains("kotlin-stdlib")) {
                useVersion("1.9.24")
                because("Force Kotlin stdlib to match Kotlin plugin version")
            }
        }
    }
}