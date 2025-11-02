package com.obdcloud.core.database.di

import android.content.Context
import androidx.room.Room
import com.obdcloud.core.database.OBDCloudDatabase
import com.obdcloud.core.database.dao.VehicleDao
import com.obdcloud.core.database.dao.ECUModuleDao
import com.obdcloud.core.database.dao.DTCDao
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): OBDCloudDatabase = Room.databaseBuilder(
        context,
        OBDCloudDatabase::class.java,
        "obdcloud.db"
    ).build()
    
    @Provides
    fun provideVehicleDao(database: OBDCloudDatabase): VehicleDao = database.vehicleDao()
    
    @Provides
    fun provideECUModuleDao(database: OBDCloudDatabase): ECUModuleDao = database.ecuModuleDao()
    
    @Provides
    fun provideDTCDao(database: OBDCloudDatabase): DTCDao = database.dtcDao()
}