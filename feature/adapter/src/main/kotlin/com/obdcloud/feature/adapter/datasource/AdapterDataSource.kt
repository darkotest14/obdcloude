package com.obdcloud.feature.adapter.datasource

import com.obdcloud.core.domain.model.OBDAdapter
import kotlinx.coroutines.flow.Flow

interface AdapterDataSource {
    fun discover(): Flow<List<OBDAdapter>>
    suspend fun connect(adapter: OBDAdapter): Result<OBDAdapter>
    suspend fun disconnect(adapter: OBDAdapter)
    suspend fun sendCommand(command: String): Result<String>
}