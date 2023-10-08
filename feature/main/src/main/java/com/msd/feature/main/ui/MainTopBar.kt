package com.msd.feature.main.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.msd.core.ui.widget.AppTopBar
import com.msd.feature.main.R

@Composable
fun MainTopBar() {
    AppTopBar(
        titleContent = {
            Text(
                text = stringResource(id = R.string.list_title),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        navigationContent = {}
    )
}
