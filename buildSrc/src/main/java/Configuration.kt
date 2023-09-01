import org.gradle.api.JavaVersion

object Configuration {

    const val namespace = "com.msd.network.explorer"
    const val compileSdk = 34
    const val minSdk = 24
    const val targetSdk = 34
    const val versionCode = 1
    const val versionName = "1.0"
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    // const val testInstrumentationRunner = "io.cucumber.android.runner.CucumberAndroidJUnitRunner"
    val javaVersion = JavaVersion.VERSION_17
}
