package it.macgood.connectionapplication.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import it.macgood.connectionapplication.R
import it.macgood.connectionapplication.presentation.ConnectionScreenEvent
import it.macgood.connectionapplication.presentation.ConnectionScreenState
import it.macgood.connectionapplication.presentation.model.CustomAddressModel
import it.macgood.connectionapplication.presentation.component.ui.theme.ConnectionDarkGray

@Composable
fun AddressRow(
    event: ConnectionScreenEvent,
    state: ConnectionScreenState,
    dispatch: (ConnectionScreenEvent) -> Unit
) {
    when (event) {
        is ConnectionScreenEvent.StreetIsNotPicked -> {
            AnimatedVisibility(visible = true) {
                ThreeTextFieldsRow(
                    previousAddress = CustomAddressModel(
                        state.house,
                        state.corpus,
                        state.apartment
                    ),
                    dispatch = { dispatch(it) }
                )
            }
        }

        is ConnectionScreenEvent.HouseIsPicked -> {
            AnimatedVisibility(visible = true) {
                OneTextFieldRow(
                    previousApartment = state.apartment,
                    dispatch = { dispatch(it) }
                )
            }
        }

        is ConnectionScreenEvent.StreetIsPicked -> {
                ThreeTextFieldsRow(
                    previousAddress = CustomAddressModel(
                        state.house,
                        state.corpus,
                        state.apartment
                    ),
                    dispatch = { dispatch(it) }
                )
        }

        else -> {}
    }
}

@Composable
private fun defaultConnectionAddressColors() = TextFieldDefaults.colors(
    unfocusedContainerColor = Color.White,
    focusedContainerColor = Color.White,
    focusedIndicatorColor = Color.White,
    unfocusedIndicatorColor = Color.White,
    unfocusedLabelColor = ConnectionDarkGray,
    unfocusedPlaceholderColor = ConnectionDarkGray
)

@Composable
fun OneTextFieldRow(
    previousApartment: String,
    dispatch: (ConnectionScreenEvent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var apartment by remember { mutableStateOf(previousApartment) }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ConnectionDarkGray, RoundedCornerShape(8.dp)),
            value = apartment,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = defaultConnectionAddressColors(),
            onValueChange = {
                if (it.isDigitsOnly()) {
                    apartment = it
                    if (it.isNotEmpty()) {
                        dispatch(ConnectionScreenEvent.FillingInCompletion(it, true))
                    } else {
                        dispatch(ConnectionScreenEvent.FillingInCompletion(it, false))
                    }
                }
            },
            placeholder = { Text(text = stringResource(id = R.string.apartment)) }
        )
    }
}

@Composable
fun ThreeTextFieldsRow(
    previousAddress: CustomAddressModel,
    dispatch: (ConnectionScreenEvent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val address = remember { mutableStateOf(previousAddress) }

        TextField(
            modifier = Modifier
                .width(96.dp)
                .border(1.dp, ConnectionDarkGray, RoundedCornerShape(8.dp)),
            value = address.value.house,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = defaultConnectionAddressColors(),
            onValueChange = {
                address.value = address.value.copy(house = it)
                if (address.value.isFilled()) {
                    dispatch(ConnectionScreenEvent.FillingInCompletion(address.value, true))
                } else {
                    dispatch(ConnectionScreenEvent.FillingInCompletion(address.value, false))
                }
            },
            placeholder = { Text(text = stringResource(R.string.house)) }
        )
        TextField(
            modifier = Modifier
                .width(96.dp)
                .border(1.dp, ConnectionDarkGray, RoundedCornerShape(8.dp)),
            value = address.value.corpus,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                address.value = address.value.copy(corpus = it)
                if (address.value.isFilled()) {
                    dispatch(ConnectionScreenEvent.FillingInCompletion(address.value, true))
                } else {
                    dispatch(ConnectionScreenEvent.FillingInCompletion(address.value, false))
                }
            },
            colors = defaultConnectionAddressColors(),
            placeholder = { Text(text = stringResource(R.string.corpus)) }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ConnectionDarkGray, RoundedCornerShape(8.dp)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = address.value.apartment,
            onValueChange = {
                address.value = address.value.copy(apartment = it)
                if (address.value.isFilled()) {
                    dispatch(ConnectionScreenEvent.FillingInCompletion(address.value, true))
                } else {
                    dispatch(ConnectionScreenEvent.FillingInCompletion(address.value, false))
                }
            },
            colors = defaultConnectionAddressColors(),
            placeholder = { Text(text = stringResource(R.string.apartment)) }
        )
    }
}
