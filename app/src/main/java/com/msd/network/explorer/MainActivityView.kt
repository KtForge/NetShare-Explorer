package com.msd.network.explorer

import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.msd.core.ui.theme.NetworkStorageConfigurationTheme
import com.msd.feature.edit.presenter.EditPresenter
import com.msd.feature.edit.ui.EditView
import com.msd.feature.explorer.presenter.ExplorerPresenter
import com.msd.feature.explorer.ui.ExplorerView
import com.msd.feature.main.presenter.MainPresenter
import com.msd.feature.main.ui.MainView
import com.msd.core.navigation.Idle
import com.msd.core.navigation.Navigate
import com.msd.core.navigation.NavigateBack
import com.msd.core.navigation.NavigateUp
import com.msd.core.navigation.NavigationConstants
import com.msd.core.navigation.OpenFile
import com.msd.core.presentation.Presenter
import com.msd.core.presentation.State

@ExperimentalTransitionApi
@ExperimentalComposeUiApi
@Composable
fun MainActivityView(
    mainActivity: MainActivity,
    navController: NavHostController = rememberNavController(),
    editPresenter: @Composable (Int) -> EditPresenter,
    explorerPresenter: @Composable (Int, String) -> ExplorerPresenter
) {
    NetworkStorageConfigurationTheme {
        NavHost(
            navController = navController,
            startDestination = NavigationConstants.NetworkSettingsList
        ) {
            main(mainActivity, builder = this, navController)
            editNetworkConfiguration(mainActivity, builder = this, navController, editPresenter)
            explorer(mainActivity, builder = this, navController, explorerPresenter)
        }
    }
}

private fun main(
    mainActivity: MainActivity,
    builder: NavGraphBuilder,
    navController: NavHostController,
) {
    composable(
        mainActivity,
        builder,
        route = NavigationConstants.NetworkSettingsList,
        arguments = emptyList(),
        navController,
        viewModelProvider = { hiltViewModel<MainPresenter>() },
        content = { presenter -> MainView(presenter) },
    )
}

@ExperimentalTransitionApi
@ExperimentalComposeUiApi
private fun editNetworkConfiguration(
    mainActivity: MainActivity,
    builder: NavGraphBuilder,
    navController: NavHostController,
    editPresenter: @Composable (Int) -> EditPresenter,
) {
    composable(
        mainActivity,
        builder,
        route = NavigationConstants.EditNetworkConfiguration,
        arguments = listOf(
            navArgument(NavigationConstants.SmbConfigurationRouteIdArg) { type = NavType.IntType }
        ),
        navController,
        viewModelProvider = {
            with(navController.currentBackStackEntry?.arguments) {
                val smbConfigurationId =
                    this?.getInt(NavigationConstants.SmbConfigurationRouteIdArg) ?: -1
                editPresenter(smbConfigurationId)
            }
        },
        content = { presenter -> EditView(presenter) },
    )
}

@ExperimentalTransitionApi
@ExperimentalComposeUiApi
private fun explorer(
    mainActivity: MainActivity,
    builder: NavGraphBuilder,
    navController: NavHostController,
    explorerPresenter: @Composable (Int, String) -> ExplorerPresenter,
) {
    composable(
        mainActivity,
        builder,
        route = NavigationConstants.Explorer,
        arguments = listOf(
            navArgument(NavigationConstants.SmbConfigurationRouteIdArg) { type = NavType.IntType }
        ),
        navController,
        viewModelProvider = {
            with(navController.currentBackStackEntry?.arguments) {
                val smbConfigurationId =
                    this?.getInt(NavigationConstants.SmbConfigurationRouteIdArg) ?: -1
                val smbConfigurationName =
                    this?.getString(NavigationConstants.SmbConfigurationRouteNameArg).orEmpty()
                explorerPresenter(smbConfigurationId, smbConfigurationName)
            }
        },
        content = { presenter -> ExplorerView(presenter) },
    )
}

private fun <S : State, V : Presenter<S>> composable(
    mainActivity: MainActivity,
    builder: NavGraphBuilder,
    route: String,
    arguments: List<NamedNavArgument>,
    navController: NavHostController,
    viewModelProvider: @Composable () -> V,
    content: @Composable (V) -> Unit,
) {
    builder.composable(route, arguments) {
        val presenter = viewModelProvider()
        val navigationEvent by presenter.getNavigation().collectAsState(initial = Idle)

        LaunchedEffect(navigationEvent) {
            when (navigationEvent) {
                is Idle -> Unit
                is Navigate -> {
                    navController.navigate((navigationEvent as Navigate).routeId)
                }

                is NavigateBack -> {
                    navController.popBackStack()
                }

                is NavigateUp -> {
                    navController.navigateUp()
                }

                is OpenFile -> {
                    val uri = FileProvider.getUriForFile(
                        mainActivity,
                        mainActivity.applicationContext.packageName + ".provider",
                        (navigationEvent as OpenFile).file
                    )
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(
                        uri,
                        MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension((navigationEvent as OpenFile).file.extension)
                    )
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    val intentChooser = Intent.createChooser(intent, "Open file")
                    mainActivity.startActivity(intentChooser)
                }
            }
            presenter.cleanNavigation()
        }

        content(presenter)

        presenter.initialize()
    }
}
