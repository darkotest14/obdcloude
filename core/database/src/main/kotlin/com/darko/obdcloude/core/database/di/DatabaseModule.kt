package com.darko.obdcloude.core.database.di

import android.content.Context
import androidx.room.Room
import com.darko.obdcloude.core.database.VehicleDatabase
import com.darko.obdcloude.core.database.dao.VehicleDao
import com.darko.obdcloude.core.database.dao.DiagnosticDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideVehicleDatabase(
        @ApplicationContext context: Context
    ): VehicleDatabase {
        return Room.databaseBuilder(
            context,
            VehicleDatabase::class.java,
            "vehicle_database"
        ).build()
    }
    
    @Provides
    fun provideVehicleDao(database: VehicleDatabase): VehicleDao {
        return database.vehicleDao()
    }
    
    @Provides
    fun provideDiagnosticDao(database: VehicleDatabase): DiagnosticDao {
        return database.diagnosticDao()
    }
}