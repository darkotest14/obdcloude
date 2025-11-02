package com.darko.obdcloude.core.database.dao

import androidx.room.*
import com.darko.obdcloude.core.database.model.VehicleManufacturer
import com.darko.obdcloude.core.database.model.VehicleModel
import com.darko.obdcloude.core.database.model.VehicleEngine
import com.darko.obdcloude.core.database.model.EcuSystem
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle_manufacturers")
    fun getAllManufacturers(): Flow<List<VehicleManufacturer>>
    
    @Query("SELECT * FROM vehicle_models WHERE manufacturerId = :manufacturerId")
    fun getModelsForManufacturer(manufacturerId: String): Flow<List<VehicleModel>>
    
    @Query("SELECT * FROM vehicle_engines WHERE modelId = :modelId")
    fun getEnginesForModel(modelId: String): Flow<List<VehicleEngine>>
    
    @Query("SELECT * FROM ecu_systems WHERE engineId = :engineId")
    fun getEcuSystemsForEngine(engineId: String): Flow<List<EcuSystem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManufacturer(manufacturer: VehicleManufacturer)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: VehicleModel)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEngine(engine: VehicleEngine)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEcuSystem(ecuSystem: EcuSystem)
    
    @Query("SELECT * FROM vehicle_models WHERE platform = :platform")
    fun getModelsForPlatform(platform: String): Flow<List<VehicleModel>>
    
    @Query("SELECT * FROM ecu_systems WHERE type = :type")
    fun getEcuSystemsByType(type: String): Flow<List<EcuSystem>>
    
    @Transaction
    @Query("SELECT * FROM vehicle_manufacturers WHERE id IN " +
           "(SELECT manufacturerId FROM vehicle_models WHERE id IN " +
           "(SELECT modelId FROM vehicle_engines WHERE ecuType = :ecuType))")
    fun getManufacturersWithEcuType(ecuType: String): Flow<List<VehicleManufacturer>>
}