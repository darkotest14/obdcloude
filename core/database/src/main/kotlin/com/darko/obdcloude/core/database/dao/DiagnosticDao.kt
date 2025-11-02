package com.darko.obdcloude.core.database.dao

import androidx.room.*
import com.darko.obdcloude.core.database.model.DiagnosticParameter
import com.darko.obdcloude.core.database.model.DiagnosticCommand
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosticDao {
    @Query("SELECT * FROM diagnostic_parameters WHERE ecuSystemId = :ecuSystemId")
    fun getParametersForEcuSystem(ecuSystemId: String): Flow<List<DiagnosticParameter>>
    
    @Query("SELECT * FROM diagnostic_commands WHERE ecuSystemId = :ecuSystemId")
    fun getCommandsForEcuSystem(ecuSystemId: String): Flow<List<DiagnosticCommand>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParameter(parameter: DiagnosticParameter)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(command: DiagnosticCommand)
    
    @Query("SELECT * FROM diagnostic_parameters WHERE pid = :pid")
    suspend fun getParameterByPid(pid: String): DiagnosticParameter?
    
    @Query("SELECT * FROM diagnostic_commands WHERE serviceId = :serviceId AND subFunction = :subFunction")
    suspend fun getCommandByService(serviceId: Int, subFunction: Int?): DiagnosticCommand?
    
    @Query("SELECT * FROM diagnostic_parameters WHERE ecuSystemId = :ecuSystemId AND code IN (:codes)")
    fun getParametersByCodes(ecuSystemId: String, codes: List<String>): Flow<List<DiagnosticParameter>>
    
    @Query("SELECT * FROM diagnostic_commands WHERE securityLevelRequired <= :securityLevel")
    fun getCommandsBySecurityLevel(securityLevel: Int): Flow<List<DiagnosticCommand>>
}