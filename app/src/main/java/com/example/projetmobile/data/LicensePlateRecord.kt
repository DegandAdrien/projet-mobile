package com.example.projetmobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "license_plates")
data class LicensePlateRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val plateNumber: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
) {
    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    fun getLocationString(): String {
        return "Lat: $latitude, Long: $longitude"
    }
}

