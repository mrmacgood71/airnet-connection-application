package it.macgood.connectionapplication.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import it.macgood.connectionapplication.R
import it.macgood.connectionapplication.presentation.component.ui.theme.ConnectionBlue
import it.macgood.connectionapplication.presentation.component.ui.theme.ConnectionLightGray
import it.macgood.connectionapplication.presentation.component.ui.theme.helveticaFontFamily

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ConnectionTopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.connection_application_text),
                fontFamily = helveticaFontFamily
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ConnectionLightGray),
        navigationIcon = {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = ConnectionBlue
                )
            }
        }
    )
}