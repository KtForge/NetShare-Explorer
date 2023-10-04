import org.gradle.api.JavaVersion

object Configuration {

    const val namespace = "com.msd.network.explorer"
    const val compileSdk = 34
    const val minSdk = 26
    const val targetSdk = 34
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    val javaVersion = JavaVersion.VERSION_17
}
