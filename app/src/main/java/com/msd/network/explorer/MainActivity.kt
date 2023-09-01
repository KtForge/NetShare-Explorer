package com.msd.network.explorer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.msd.navigation.Idle
import com.msd.navigation.Navigate
import com.msd.navigation.NavigateBack
import com.msd.navigation.NavigateUp
import com.msd.navigation.NavigationConstants.EditNetworkConfiguration
import com.msd.navigation.NavigationConstants.Explorer
import com.msd.navigation.NavigationConstants.NetworkSettingsList
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteIdArg
import com.msd.navigation.NavigationConstants.SmbConfigurationRouteNameArg
import com.msd.navigation.OpenFile
import com.msd.presentation.Presenter
import com.msd.presentation.State
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun editViewModelFactory(): EditPresenter.Factory
        fun explorerViewModelFactory(): ExplorerPresenter.Factory
    }

    @ExperimentalTransitionApi
    @ExperimentalComposeUiApi
    @Composable
    fun editPresenter(smbConfigurationId: Int): EditPresenter {
        val factory = EntryPointAccessors.fromActivity(
            LocalContext.current as Activity,
            ViewModelFactoryProvider::class.java
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
            ViewModelFactoryProvider::class.java
        ).explorerViewModelFactory()

        return viewModel(
            factory = ExplorerPresenter.provideFactory(
                factory,
                smbConfigurationId,
                smbConfigurationName
            )
        )
    }

    private var presenter: ViewModel? = null

    @ExperimentalTransitionApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetworkStorageConfigurationTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = NetworkSettingsList
                ) {
                    main(builder = this, navController)
                    editNetworkConfiguration(builder = this, navController)
                    explorer(builder = this, navController)
                }
            }
        }
    }

    private fun main(builder: NavGraphBuilder, navController: NavHostController) {
        composable(
            builder,
            route = NetworkSettingsList,
            arguments = emptyList(),
            navController,
            viewModelProvider = { hiltViewModel<MainPresenter>() },
            content = { presenter -> MainView(presenter) }
        )
    }

    @ExperimentalTransitionApi
    @ExperimentalComposeUiApi
    private fun editNetworkConfiguration(
        builder: NavGraphBuilder,
        navController: NavHostController
    ) {
        composable(
            builder,
            route = EditNetworkConfiguration,
            arguments = listOf(
                navArgument(SmbConfigurationRouteIdArg) { type = NavType.IntType }
            ),
            navController,
            viewModelProvider = {
                with(navController.currentBackStackEntry?.arguments) {
                    val smbConfigurationId = this?.getInt(SmbConfigurationRouteIdArg) ?: -1
                    editPresenter(smbConfigurationId = smbConfigurationId)
                }
            },
            content = { presenter -> EditView(presenter) }
        )
    }

    @ExperimentalTransitionApi
    @ExperimentalComposeUiApi
    private fun explorer(
        builder: NavGraphBuilder,
        navController: NavHostController
    ) {
        composable(
            builder,
            route = Explorer,
            arguments = listOf(
                navArgument(SmbConfigurationRouteIdArg) { type = NavType.IntType }
            ),
            navController,
            viewModelProvider = {
                with(navController.currentBackStackEntry?.arguments) {
                    val smbConfigurationId = this?.getInt(SmbConfigurationRouteIdArg) ?: -1
                    val smbConfigurationName =
                        this?.getString(SmbConfigurationRouteNameArg).orEmpty()
                    explorerPresenter(smbConfigurationId, smbConfigurationName)
                }
            },
            content = { presenter -> ExplorerView(presenter) }
        )
    }

    private fun <S : State, V : Presenter<S>> composable(
        builder: NavGraphBuilder,
        route: String,
        arguments: List<NamedNavArgument>,
        navController: NavHostController,
        viewModelProvider: @Composable () -> V,
        content: @Composable (V) -> Unit
    ) {
        builder.composable(route, arguments) {
            val presenter = viewModelProvider()
            this.presenter = presenter
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
                            this@MainActivity,
                            applicationContext.packageName + ".provider",
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
                        startActivity(intentChooser)
                    }
                }
                presenter.cleanNavigation()
            }

            content(presenter)

            presenter.initialize()
        }
    }

    override fun onStart() {
        super.onStart()
        cacheDir.deleteRecursively()
        (presenter as? Presenter<State>)?.onStart()
    }

    override fun onResume() {
        super.onResume()
        (presenter as? Presenter<State>)?.onResume()
    }
}
