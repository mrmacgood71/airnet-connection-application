package it.macgood.connectionapplication.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import it.macgood.connectionapplication.data.api.AirnetApi
import it.macgood.connectionapplication.data.dto.toHouse
import it.macgood.connectionapplication.data.dto.toStreet
import it.macgood.connectionapplication.domain.repository.ConnectionRepository
import it.macgood.connectionapplication.domain.model.House
import it.macgood.connectionapplication.domain.model.Street
import it.macgood.core.Resource
import it.macgood.core.handleException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ConnectionRepositoryImpl @Inject constructor(
    private val api: AirnetApi,
    private val context: Context,
) : ConnectionRepository {
    override suspend fun getAllHouses(streetId: Int): Flow<Resource<List<House>>> = flow {
        try {
            val houses = api.getAllHouses(streetId).map { it.toHouse() }
            emit(Resource.Success(houses))
        } catch (e: Exception) {
            emit(handleException(e, context = context))
        }

    }.flowOn(Dispatchers.IO)

    override suspend fun getAllStreets(): Flow<Resource<List<Street>>> = flow {
        try {
            val streets = api.getAllStreets().map { it.toStreet() }
            emit(Resource.Success(streets))
        } catch (e: Exception) {
            emit(handleException(e, context))
        }
    }.flowOn(Dispatchers.IO)
}