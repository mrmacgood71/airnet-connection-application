package it.macgood.connectionapplication.presentation

sealed class ConnectionScreenEvent {
    data class HouseIsPicked(val house: String) : ConnectionScreenEvent()

    data class StreetIsNotPicked(val street: String?) : ConnectionScreenEvent()

    object HouseNotFound : ConnectionScreenEvent()

    data class FillingInCompletion<T>(val data: T, val completion: Boolean) : ConnectionScreenEvent()

    data class StreetIsPicked(val street: String?, val streetId: String) : ConnectionScreenEvent()

    object SendApplication : ConnectionScreenEvent()
}