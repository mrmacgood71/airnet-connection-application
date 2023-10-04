package it.macgood.connectionapplication.data.api

import it.macgood.connectionapplication.data.dto.HouseDto
import it.macgood.connectionapplication.data.dto.StreetDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AirnetApi {

    @GET("houses")
    @Headers("Accept: text/html")
    suspend fun getAllHouses(
        @Query("street_id") streetId: Int
    ): List<HouseDto>

    @GET("allStreets/")
    @Headers("Accept: text/html")
    suspend fun getAllStreets(): List<StreetDto>

    companion object {
        const val BASE_URL = "https://stat-api.airnet.ru/v2/utils/get/"
    }
}