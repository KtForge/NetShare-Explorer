package com.msd.network.explorer.test

import android.app.Activity
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.msd.feature.edit.presenter.EditPresenter
import com.msd.feature.explorer.presenter.ExplorerPresenter
import com.msd.network.explorer.MainActivity
import com.msd.network.explorer.MainActivityTest
import com.msd.network.explorer.MainActivityView
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TestTest {

    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<MainActivityTest>()

    private lateinit var navController: TestNavHostController

    @OptIn(ExperimentalTransitionApi::class, ExperimentalComposeUiApi::class)
    @Before
    fun setup() {
        hiltTestRule.inject()
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }

            MainActivityView(
                activity = composeTestRule.activity,
                navController = navController,
                editPresenter = { id ->
                    editPresenter(smbConfigurationId = id)
                },
                explorerPresenter = { id, name ->
                    explorerPresenter(smbConfigurationId = id, smbConfigurationName = name)
                }
            )
        }
    }

    @Test
    fun testTest() {
        Thread.sleep(3000)
    }

    @ExperimentalTransitionApi
    @ExperimentalComposeUiApi
    @Composable
    fun editPresenter(smbConfigurationId: Int): EditPresenter {
        val factory = EntryPointAccessors.fromActivity(
            LocalContext.current as Activity,
            MainActivity.ViewModelFactoryProvider::class.java
        ).editViewModelFactory()

        return viewModel(
            factory = EditPresenter.provideFactory(
                factory,
                smbConfigurationId
            )
        )
    }

    @ExperimentalTransitionApi
    @ExperimentalComposeUiApi
    @Composable
    fun explorerPresenter(
        smbConfigurationId: Int,
        smbConfigurationName: String
    ): ExplorerPresenter {
        val factory = EntryPointAccessors.fromActivity(
            LocalContext.current as Activity,
            MainActivity.ViewModelFactoryProvider::class.java
        ).explorerViewModelFactory()

        return viewModel(
            factory = ExplorerPresenter.provideFactory(
                factory,
                smbConfigurationId,
                smbConfigurationName
            )
        )
    }
}