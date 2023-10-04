package it.macgood.connectionapplication.presentation

import it.macgood.connectionapplication.domain.model.House
import it.macgood.connectionapplication.domain.model.Street

data class ConnectionScreenState(
    val streets: List<Street> = emptyList(),
    val houses: List<House> = emptyList(),
    val error: String = "",
    val loading: Boolean = true,
    val houseIsVisible: Boolean = false,
    val sendingIsEnabled: Boolean = false,
    val streetId: String = "",
    val houseId: String = "",
    val street: String? = "",
    val house: String = "",
    val apartment: String = "",
    val corpus: String = ""
)