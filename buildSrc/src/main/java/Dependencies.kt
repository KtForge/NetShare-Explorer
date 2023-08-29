object Dependencies {

    val kotlinBom by lazy { "org.jetbrains.kotlin:kotlin-bom:${Versions.kotlinBom}" }
    val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
    val composeActivity by lazy { "androidx.activity:activity-compose:${Versions.composeActivity}" }
    val composeNavigation by lazy { "androidx.navigation:navigation-compose:${Versions.composeNavigation}" }
    val viewModelLifecycleKtx by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModelLifecycleKtx}" }
    val lifecycleRuntimeKtx by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntimeKtx}" }

    val coroutinesCore by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesCore}" }

    val composeBom by lazy { "androidx.compose:compose-bom:${Versions.composeBom}" }
    val composeRuntime by lazy { "androidx.compose.runtime:runtime" }
    val composeUi by lazy { "androidx.compose.ui:ui" }
    val composeUiGraphics by lazy { "androidx.compose.ui:ui-graphics" }
    val composeUiToolingPreview by lazy { "androidx.compose.ui:ui-tooling-preview" }
    val composeMaterial3 by lazy { "androidx.compose.material3:material3" }
    val composeMaterialIcons by lazy { "androidx.compose.material:material-icons-extended" }

    val daggerHiltAndroid by lazy { "com.google.dagger:hilt-android:${Versions.daggerHilt}" }
    val daggerHiltAndroidCompiler by lazy { "com.google.dagger:hilt-android-compiler:${Versions.daggerHilt}" }
    val daggerHiltNavigation by lazy { "androidx.hilt:hilt-navigation-compose:${Versions.daggerHiltNavigation}" }
    val inject by lazy { "javax.inject:javax.inject:${Versions.inject}" }

    val jUnit by lazy { "junit:junit:${Versions.jUnit}" }
    val mockito by lazy { "org.mockito:mockito-core:${Versions.mockitoKotlin}"}
    val mockitoKotlin by lazy { "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}" }
    val mockitoInline by lazy { "org.mockito:mockito-inline:${Versions.mockitoKotlin}" }
    val coroutinesTest by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}" }

    val jUnitKtx by lazy { "androidx.test.ext:junit-ktx:${Versions.jUnitKtx}" }
    val espressoCore by lazy { "androidx.test.espresso:espresso-core:${Versions.espressoCore}" }
    val uiTestJUnit4 by lazy { "androidx.compose.ui:ui-test-junit4" }

    val uiTooling by lazy { "androidx.compose.ui:ui-tooling" }
    val uiTestManifest by lazy { "androidx.compose.ui:ui-test-manifest" }

    // Dependencies specific to this app
    val smbj by lazy { "com.hierynomus:smbj:${Versions.smbj}" }
    val roomRuntime by lazy { "androidx.room:room-runtime:${Versions.room}" }
    val roomCompiler by lazy { "androidx.room:room-compiler:${Versions.room}" }
    val roomKtx by lazy { "androidx.room:room-ktx:${Versions.room}" }
}
