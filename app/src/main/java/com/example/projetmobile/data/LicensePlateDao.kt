package com.example.projetmobile.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LicensePlateDao {
    @Query("SELECT * FROM license_plates ORDER BY timestamp DESC")
    fun getAllLicensePlates(): Flow<List<LicensePlateRecord>>

    @Insert
    suspend fun insertLicensePlate(licensePlate: LicensePlateRecord)

    @Delete
    suspend fun deleteLicensePlate(licensePlate: LicensePlateRecord)
}