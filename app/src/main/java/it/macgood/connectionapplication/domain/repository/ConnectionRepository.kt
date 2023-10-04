package it.macgood.connectionapplication.domain.repository

import it.macgood.connectionapplication.domain.model.House
import it.macgood.connectionapplication.domain.model.Street
import it.macgood.core.Resource
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {

    suspend fun getAllHouses(streetId: Int): Flow<Resource<List<House>>>
    suspend fun getAllStreets(): Flow<Resource<List<Street>>>

}