package com.darko.obdcloude.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.darko.obdcloude.core.database.model.*

data class VehicleWithModels(
    @Embedded val manufacturer: VehicleManufacturer,
    @Relation(
        parentColumn = "id",
        entityColumn = "manufacturerId"
    )
    val models: List<VehicleModel>
)

data class ModelWithEngines(
    @Embedded val model: VehicleModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "modelId"
    )
    val engines: List<VehicleEngine>
)

data class EngineWithEcuSystems(
    @Embedded val engine: VehicleEngine,
    @Relation(
        parentColumn = "id",
        entityColumn = "engineId"
    )
    val ecuSystems: List<EcuSystem>
)

data class EcuSystemWithDiagnostics(
    @Embedded val ecuSystem: EcuSystem,
    @Relation(
        parentColumn = "id",
        entityColumn = "ecuSystemId"
    )
    val parameters: List<DiagnosticParameter>,
    @Relation(
        parentColumn = "id",
        entityColumn = "ecuSystemId"
    )
    val commands: List<DiagnosticCommand>
)