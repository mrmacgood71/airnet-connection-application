package it.macgood.connectionapplication.presentation

sealed interface ConnectionSideEffect {
    data class ShowSnackbar(val text: String): ConnectionSideEffect
}