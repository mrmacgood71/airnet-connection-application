package it.macgood.connectionapplication.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import it.macgood.connectionapplication.R
import it.macgood.connectionapplication.presentation.component.AddressRow
import it.macgood.connectionapplication.presentation.component.ConnectionTopBar
import it.macgood.connectionapplication.presentation.component.ExposedSelectionMenuOfHouses
import it.macgood.connectionapplication.presentation.component.ExposedSelectionMenuOfStreets
import it.macgood.connectionapplication.presentation.model.CustomAddressModel
import it.macgood.connectionapplication.presentation.component.ui.theme.ConnectionBlue
import it.macgood.connectionapplication.presentation.component.ui.theme.ConnectionDarkGray
import it.macgood.connectionapplication.presentation.component.ui.theme.ConnectionLightGray
import it.macgood.connectionapplication.presentation.component.ui.theme.helveticaFontFamily
import kotlinx.coroutines.launch

@Composable
fun ConnectionScreen(
    connectionViewModel: ConnectionViewModel = hiltViewModel()
) {
    val state = connectionViewModel.state.collectAsState().value
    val event = connectionViewModel.connectionEvent.collectAsState().value
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        for (effect in connectionViewModel.sideEffects) {
            when(effect) {
                is ConnectionSideEffect.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = effect.text,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ConnectionTopBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ConnectionLightGray)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                state.loading -> {
                    CircularProgressIndicator()
                }

                state.error.isNotEmpty() -> {
                    Text(text = stringResource(R.string.error, state.error))
                }

                else -> {
                    SuccessConnectionScreen(
                        state = state,
                        dispatch = { connectionViewModel.onEvent(it) },
                        event = event
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessConnectionScreen(
    state: ConnectionScreenState,
    dispatch: (ConnectionScreenEvent) -> Unit,
    event: ConnectionScreenEvent
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ExposedSelectionMenuOfStreets(
            streets = state.streets,
            dispatch = { dispatch(it) }
        )
        AnimatedVisibility(visible = state.houseIsVisible) {
            ExposedSelectionMenuOfHouses(
                streetId = state.streetId,
                houses = state.houses,
                dispatch = { dispatch(it) }
            )
        }
        AddressRow(
            event = event,
            state = state,
            dispatch = { dispatch(it) }
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ConnectionBlue,
                disabledContainerColor = ConnectionDarkGray
            ),
            enabled = state.sendingIsEnabled,
            onClick = {
                dispatch(ConnectionScreenEvent.SendApplication)
            }
        ) {
            Text(
                text = stringResource(R.string.send),
                color = Color.White,
                fontFamily = helveticaFontFamily
            )
        }
    }
}

