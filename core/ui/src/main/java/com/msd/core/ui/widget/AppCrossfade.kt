package com.msd.core.ui.widget

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> AppCrossfade(
    modifier: Modifier = Modifier,
    targetState: T,
    content: @Composable (T) -> Unit
) {
    Crossfade(
        targetState = targetState,
        modifier = modifier,
        animationSpec = tween(0),
        label = "",
    ) { state -> content(state) }
}