package it.macgood.connectionapplication.domain.usecase

import it.macgood.connectionapplication.domain.repository.ConnectionRepository
import javax.inject.Inject

class GetStreetsUseCase @Inject constructor(
    private val repository: ConnectionRepository
) {
    suspend operator fun invoke() = repository.getAllStreets()
}