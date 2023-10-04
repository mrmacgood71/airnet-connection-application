package it.macgood.connectionapplication.domain.usecase

import it.macgood.connectionapplication.domain.repository.ConnectionRepository
import javax.inject.Inject

class GetHousesUseCase @Inject constructor(
    private val repository: ConnectionRepository
) {

    suspend operator fun invoke(streetId: Int) = repository.getAllHouses(streetId)

}