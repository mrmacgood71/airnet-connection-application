package it.macgood.connectionapplication.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import it.macgood.connectionapplication.R
import it.macgood.connectionapplication.domain.model.House
import it.macgood.connectionapplication.domain.model.Street
import it.macgood.connectionapplication.presentation.ConnectionScreenEvent
import it.macgood.connectionapplication.presentation.component.ui.theme.ConnectionBlue
import it.macgood.connectionapplication.presentation.component.ui.theme.helveticaFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedSelectionMenuOfStreets(
    streets: List<Street>,
    dispatch: (ConnectionScreenEvent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }
    var pickedStreetText by remember { mutableStateOf("") }
    val windowInfo = LocalWindowInfo.current
    val focusRequester = remember { FocusRequester() }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .focusRequester(focusRequester)
                .fillMaxWidth(),
            value = selectedOptionText,

            onValueChange = {
                selectedOptionText = it
                expanded = it.length >= 3
            },
            maxLines = 1,
            colors = DefaultConnectionMenuColors(),
            placeholder = { Text(text = stringResource(R.string.choose_street)) }
        )
        val filteringOptions =
            streets.filter { it.street.contains(selectedOptionText, ignoreCase = true) }
        if (filteringOptions.isNotEmpty() && selectedOptionText.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier
                    .height(
                        if ((filteringOptions.size * 56).dp > 248.dp) 248.dp
                        else (filteringOptions.size * 56).dp
                    )
                    .background(Color.White)
                    .fillMaxWidth(),

                expanded = expanded,
                properties = PopupProperties(),
                onDismissRequest = { expanded = false }
            ) {
                filteringOptions.forEach { selected ->
                    DropdownMenuItem(
                        text = { Text(text = selected.street, fontFamily = helveticaFontFamily) },
                        colors = MenuDefaults.itemColors(),
                        onClick = {
                            selectedOptionText = selected.street
                            expanded = false
                            dispatch(
                                ConnectionScreenEvent.StreetIsPicked(
                                    street = selected.street,
                                    streetId = selected.streetId
                                )
                            )
                            pickedStreetText = selected.street
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        LaunchedEffect(key1 = selectedOptionText) {
            if (selectedOptionText != pickedStreetText) {
                dispatch(ConnectionScreenEvent.StreetIsNotPicked(selectedOptionText))
            }
        }

        LaunchedEffect(windowInfo) {
            snapshotFlow { windowInfo.isWindowFocused }.collect { isWindowFocused ->
                if (isWindowFocused) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedSelectionMenuOfHouses(
    streetId: String,
    houses: List<House>,
    dispatch: (ConnectionScreenEvent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = selectedOptionText,
            readOnly = true,
            onValueChange = {},

            label = { Text(text = stringResource(R.string.choose_house)) },
            maxLines = 1,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = DefaultConnectionMenuColors()
        )

        DropdownMenu(
            modifier = Modifier
                .height(
                    if ((houses.size * 56).dp > 248.dp) 248.dp
                    else (houses.size * 56).dp
                )
                .background(Color.White)
                .fillMaxWidth(),
            expanded = expanded,
            properties = PopupProperties(),
            onDismissRequest = { expanded = false }
        ) {
            var sorted = houses.sortHouses()
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.choose_house), fontFamily = helveticaFontFamily) },
                onClick = {
                    selectedOptionText = "Выберите дом"
                    expanded = false
                    dispatch(ConnectionScreenEvent.StreetIsPicked("", streetId))
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
            )

            sorted.forEach { selected ->
                DropdownMenuItem(
                    text = { Text(text = selected.house, fontFamily = helveticaFontFamily) },
                    onClick = {
                        selectedOptionText = selected.house
                        expanded = false
                        dispatch(ConnectionScreenEvent.HouseIsPicked(selected.houseId))
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultConnectionMenuColors() = ExposedDropdownMenuDefaults.textFieldColors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    unfocusedIndicatorColor = ConnectionBlue,
    focusedIndicatorColor = ConnectionBlue
)

fun List<House>.sortHouses(): List<House> {

    val nums = mutableListOf<Pair<House, Int>>()
    for (item in this) {
        var digit = ""
        for (c in item.house) {
            if (c.isDigit()) {
                digit += c
            } else break
        }
        nums.add(item to digit.toInt())
    }

    return nums.sortedBy { it.second }.map { it.first };
}