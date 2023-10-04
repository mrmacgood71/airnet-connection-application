package it.macgood.connectionapplication.data.dto

import com.google.gson.annotations.SerializedName
import it.macgood.connectionapplication.domain.model.Street

data class StreetDto(
    val street: String,
    @SerializedName("street_id")
    val streetId: String
)

fun StreetDto.toStreet() = Street(street = street, streetId = streetId)
fun Street.toStreetDto() = StreetDto(street = street, streetId = streetId)