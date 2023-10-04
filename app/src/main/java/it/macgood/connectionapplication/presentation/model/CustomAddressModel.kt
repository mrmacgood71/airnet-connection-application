package it.macgood.connectionapplication.presentation.model

data class CustomAddressModel(
    val house: String = "",
    val corpus: String = "",
    val apartment: String = "",

) {
    fun isFilled() : Boolean {
        return house.isNotEmpty() && corpus.isNotEmpty() && apartment.isNotEmpty()
    }
}