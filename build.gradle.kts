// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    id("com.android.library") version "8.0.0" apply false
    // The google-services plugin is required to parse the google-services.json file
    id("com.google.gms.google-services") version "4.3.15" apply false
}