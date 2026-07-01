package com.example.tutoruam_proyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tutoruam_proyecto.ui.model.Rating
import com.example.tutoruam_proyecto.ui.model.RatingRequest
import com.example.tutoruam_proyecto.ui.repository.RatingRepository
import com.example.tutoruam_proyecto.ui.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RatingViewModel(
    private val repository: RatingRepository
) : ViewModel() {

    private val _ratingsState = MutableStateFlow<ApiResult<List<Rating>>?>(null)
    val ratingsState: StateFlow<ApiResult<List<Rating>>?> = _ratingsState.asStateFlow()

    private val _averageState = MutableStateFlow<ApiResult<Double>?>(null)
    val averageState: StateFlow<ApiResult<Double>?> = _averageState.asStateFlow()

    private val _actionState = MutableStateFlow<ApiResult<Rating>?>(null)
    val actionState: StateFlow<ApiResult<Rating>?> = _actionState.asStateFlow()

    fun loadRatings(tutorId: Long) {
        viewModelScope.launch {
            _ratingsState.value = ApiResult.Loading
            _ratingsState.value = repository.getRatingsByTutor(tutorId)
        }
    }

    fun loadAverage(tutorId: Long) {
        viewModelScope.launch {
            _averageState.value = ApiResult.Loading
            _averageState.value = repository.getAverageRating(tutorId)
        }
    }

    fun createRating(request: RatingRequest) {
        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            _actionState.value = repository.createRating(request)
        }
    }

    fun clearActionState() {
        _actionState.value = null
    }
}

class RatingViewModelFactory(
    private val repository: RatingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
            return RatingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
