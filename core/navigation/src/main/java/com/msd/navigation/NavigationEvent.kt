package com.msd.navigation

import java.io.File

sealed interface NavigationEvent

object Idle : NavigationEvent
data class Navigate(val routeId: String) : NavigationEvent
object NavigateBack : NavigationEvent
object NavigateUp : NavigationEvent
data class OpenFile(val file: File) : NavigationEvent