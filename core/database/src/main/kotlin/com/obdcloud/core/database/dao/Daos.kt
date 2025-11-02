package com.obdcloud.core.database.dao

import androidx.room.*
import com.obdcloud.core.database.entity.VehicleEntity
import com.obdcloud.core.database.entity.ECUModuleEntity
import com.obdcloud.core.database.entity.DTCCodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles")
    fun getAllVehicles(): Flow<List<VehicleEntity>>
    
    @Query("SELECT * FROM vehicles WHERE vin = :vin")
    suspend fun getVehicleByVin(vin: String): VehicleEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)
    
    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)
}

@Dao
interface ECUModuleDao {
    @Query("SELECT * FROM ecu_modules WHERE vehicleVin = :vin")
    fun getModulesForVehicle(vin: String): Flow<List<ECUModuleEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: ECUModuleEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ECUModuleEntity>)
    
    @Delete
    suspend fun deleteModule(module: ECUModuleEntity)
}

@Dao
interface DTCDao {
    @Query("SELECT * FROM dtc_codes WHERE moduleId = :moduleId")
    fun getDTCsForModule(moduleId: String): Flow<List<DTCCodeEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDTC(dtc: DTCCodeEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDTCs(dtcs: List<DTCCodeEntity>)
    
    @Query("DELETE FROM dtc_codes WHERE moduleId = :moduleId")
    suspend fun clearDTCsForModule(moduleId: String)
}