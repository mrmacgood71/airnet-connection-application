package it.macgood.connectionapplication.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.macgood.connectionapplication.data.api.AirnetApi
import it.macgood.connectionapplication.domain.usecase.GetHousesUseCase
import it.macgood.connectionapplication.domain.usecase.GetStreetsUseCase
import it.macgood.connectionapplication.presentation.model.CustomAddressModel
import it.macgood.core.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val getHousesUseCase: GetHousesUseCase,
    private val getStreetsUseCase: GetStreetsUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow(ConnectionScreenState())
    val state = _screenState.asStateFlow()

    private val _connectionEvent = MutableStateFlow<ConnectionScreenEvent>(ConnectionScreenEvent.StreetIsNotPicked(null))
    val connectionEvent = _connectionEvent.asStateFlow()

    val sideEffects = Channel<ConnectionSideEffect>(Channel.UNLIMITED)

    init {
        viewModelScope.launch {
            getStreetsUseCase().collect { streets ->
                when(streets) {
                    is Resource.Error -> {
                        _screenState.update {
                            it.copy(error = streets.message)
                        }
                    }
                    is Resource.Success -> {
                        _screenState.update {
                            it.copy(streets = streets.data, loading = false)
                        }
                    }
                }

            }
        }
    }

    fun onEvent(event: ConnectionScreenEvent) {
        when(event) {
            is ConnectionScreenEvent.StreetIsNotPicked -> {
                _connectionEvent.update { ConnectionScreenEvent.StreetIsNotPicked(event.street) }
                _screenState.update {
                    it.copy(
                        streetId = "",
                        street = event.street,
                        houseIsVisible = false,
                        sendingIsEnabled = false
                    )
                }
            }
            is ConnectionScreenEvent.HouseIsPicked -> {
                _screenState.update { it.copy(houseId = event.house) }
                _connectionEvent.update { ConnectionScreenEvent.HouseIsPicked(event.house) }
            }
            ConnectionScreenEvent.HouseNotFound -> {
                _connectionEvent.update { ConnectionScreenEvent.HouseNotFound }
            }

            is ConnectionScreenEvent.StreetIsPicked -> {
                viewModelScope.launch {
                    getHousesUseCase(event.streetId.toInt()).collect { houses ->
                        when(houses) {
                            is Resource.Error -> {
                                _screenState.update {
                                    it.copy(
                                        streetId = event.streetId,
                                        houseId = "",
                                        error = houses.message,
                                        houseIsVisible = true,
                                        sendingIsEnabled = false
                                    )
                                }
                            }
                            is Resource.Success -> {
                                _screenState.update {
                                    it.copy(
                                        streetId = event.streetId,
                                        houseId = "",
                                        houses = houses.data,
                                        houseIsVisible = true,
                                        sendingIsEnabled = false
                                    )
                                }
                            }
                        }
                    }
                    _connectionEvent.update {
                        ConnectionScreenEvent.StreetIsPicked(
                            street = event.street,
                            streetId = event.streetId
                        )
                    }
                }
            }

            is ConnectionScreenEvent.FillingInCompletion<*> -> {
                _screenState.update {
                    if (event.data is String) {
                         return@update it.copy(
                            sendingIsEnabled = event.completion,
                            apartment = event.data
                        )
                    } else if (event.data is CustomAddressModel) {
                        return@update it.copy(
                            sendingIsEnabled = event.completion,
                            house = event.data.house,
                            apartment = event.data.apartment,
                            corpus = event.data.corpus
                        )
                    }
                    it.copy(sendingIsEnabled = event.completion)
                }
            }

            ConnectionScreenEvent.SendApplication -> {
                when {
                    _screenState.value.streetId.isNotEmpty() && _screenState.value.houseId.isNotEmpty() -> {
                        val response = "${AirnetApi.BASE_URL}send?" +
                                "street_id=${_screenState.value.streetId}" +
                                "&house_id=${_screenState.value.houseId}" +
                                "&apartment=${_screenState.value.apartment}"
                        dispatch(ConnectionSideEffect.ShowSnackbar(response))
                    }
                    _screenState.value.streetId.isNotEmpty() && _screenState.value.houseId.isEmpty() -> {
                        val response = "${AirnetApi.BASE_URL}send?" +
                                "street_id=${_screenState.value.streetId}" +
                                "&house=${_screenState.value.house}" +
                                "&corpus=${_screenState.value.corpus}" +
                                "&apartment=${_screenState.value.apartment}"
                        dispatch(ConnectionSideEffect.ShowSnackbar(response))
                    }
                    else -> {
                        val response = "${AirnetApi.BASE_URL}send?" +
                                "street=${_screenState.value.street}" +
                                "&house=${_screenState.value.house}" +
                                "&corpus=${_screenState.value.corpus}" +
                                "&apartment=${_screenState.value.apartment}"
                        dispatch(ConnectionSideEffect.ShowSnackbar(response))
                    }
                }
            }
        }
    }

    private fun dispatch(effect: ConnectionSideEffect) {
        viewModelScope.launch {
            sideEffects.send(effect)
        }
    }
}