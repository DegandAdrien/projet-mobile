package com.example.projetmobile.data
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LicensePlateRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase(): RoomDatabase() {
    abstract fun licensePlateDao(): LicensePlateDao
}
