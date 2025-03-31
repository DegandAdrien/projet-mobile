package com.example.projetmobile.di


import android.content.Context
import androidx.room.Room
import com.example.projetmobile.data.AppDatabase
import com.example.projetmobile.data.LicensePlateDao

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module

@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "license_plate_database"
        ).build()
    }

    @Provides
    fun provideLicensePlateDao(database: AppDatabase): LicensePlateDao {
        return database.licensePlateDao()
    }

    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}

