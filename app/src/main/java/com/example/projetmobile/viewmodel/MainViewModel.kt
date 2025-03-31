package com.example.projetmobile.viewmodel

import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.example.projetmobile.data.LicensePlateRecord
import kotlinx.coroutines.launch


class MainViewModel: ViewModel() {

    private val _licensePlates = MutableStateFlow<List<LicensePlateRecord>>(emptyList())
    val licensePlates: StateFlow<List<LicensePlateRecord>> = _licensePlates.asStateFlow()

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted.asStateFlow()

    private val _processingImage = MutableStateFlow(false)
    val processingImage: StateFlow<Boolean> = _processingImage.asStateFlow()

    private val _lastDetectedText = MutableStateFlow<String?>(null)
    val lastDetectedText: StateFlow<String?> = _lastDetectedText.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    fun onPermissionsGranted() {
        _permissionsGranted.value = true
    }

    fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _processingImage.value = true
        }
    }

    fun saveLicensePlate(plateNumber: String) {
        viewModelScope.launch {
            val location = _currentLocation.value

        }
    }


}

