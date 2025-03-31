package com.example.projetmobile.repository

import com.example.projetmobile.data.LicensePlateDao
import com.example.projetmobile.data.LicensePlateRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LicensePlateRepository {
    fun getAllLicensePlates(): Flow<List<LicensePlateRecord>>
    suspend fun insertLicensePlate(licensePlate: LicensePlateRecord)
    suspend fun deleteLicensePlate(licensePlate: LicensePlateRecord)
}

class LicensePlateRepositoryImpl @Inject constructor(
    private val licensePlateDao: LicensePlateDao
) : LicensePlateRepository {

    override fun getAllLicensePlates(): Flow<List<LicensePlateRecord>> {
        return licensePlateDao.getAllLicensePlates()
    }

    override suspend fun insertLicensePlate(licensePlate: LicensePlateRecord) {
        licensePlateDao.insertLicensePlate(licensePlate)
    }

    override suspend fun deleteLicensePlate(licensePlate: LicensePlateRecord) {
        licensePlateDao.deleteLicensePlate(licensePlate)
    }
}