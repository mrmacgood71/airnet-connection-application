package it.macgood.connectionapplication.data.dto

import com.google.gson.annotations.SerializedName
import it.macgood.connectionapplication.domain.model.House

data class HouseDto(
    @SerializedName("house_id")
    val houseId: String,
    val house: String
)

fun HouseDto.toHouse() = House(house = house, houseId = houseId)
fun House.toHouseDto() = HouseDto(house = house, houseId = house)