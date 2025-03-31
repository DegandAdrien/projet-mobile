package com.example.projetmobile.viewmodel

import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetmobile.data.LicensePlateRecord
import com.example.projetmobile.repository.LicensePlateRepository
import com.example.projetmobile.repository.LocationRepository
import com.example.projetmobile.repository.TextRecognitionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val licensePlateRepository: LicensePlateRepository,
    private val locationRepository: LocationRepository,
    private val textRecognitionRepository: TextRecognitionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _licensePlates = MutableStateFlow<List<LicensePlateRecord>>(emptyList())
    val licensePlates: StateFlow<List<LicensePlateRecord>> = _licensePlates.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted.asStateFlow()

    private val _processingImage = MutableStateFlow(false)
    val processingImage: StateFlow<Boolean> = _processingImage.asStateFlow()

    private val _lastDetectedText = MutableStateFlow<String?>(null)
    val lastDetectedText: StateFlow<String?> = _lastDetectedText.asStateFlow()

    init {
        loadLicensePlates()
    }

    fun onPermissionsGranted() {
        _permissionsGranted.value = true
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            locationRepository.getLocationUpdates().collect { location ->
                _currentLocation.value = location
            }
        }
    }

    fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _processingImage.value = true
            try {
                val detectedText = textRecognitionRepository.recognizeText(bitmap)
                _lastDetectedText.value = detectedText
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            } finally {
                _processingImage.value = false
            }
        }
    }

    fun saveLicensePlate(plateNumber: String) {
        viewModelScope.launch {
            val location = _currentLocation.value
            if (location != null) {
                val record = LicensePlateRecord(
                    id = 0, // Auto-generated
                    plateNumber = plateNumber,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = Date().time
                )
                licensePlateRepository.insertLicensePlate(record)
                loadLicensePlates()
                _lastDetectedText.value = null
            } else {
                _uiState.value = UiState.Error("Location not available")
            }
        }
    }

    private fun loadLicensePlates() {
        viewModelScope.launch {
            try {
                licensePlateRepository.getAllLicensePlates().collect { plates ->
                    _licensePlates.value = plates
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteLicensePlate(record: LicensePlateRecord) {
        viewModelScope.launch {
            licensePlateRepository.deleteLicensePlate(record)
        }
    }

    sealed class UiState {
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }
}

