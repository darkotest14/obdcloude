package com.darko.obdcloude.core.database.util

import com.darko.obdcloude.core.database.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DatabaseSearchUtils @Inject constructor() {
    fun searchEnginesByDisplacement(
        engines: Flow<List<VehicleEngine>>,
        displacement: Int,
        tolerance: Int = 100
    ): Flow<List<VehicleEngine>> {
        return engines.map { list ->
            list.filter { engine ->
                val diff = (engine.displacement - displacement).absoluteValue
                diff <= tolerance
            }
        }
    }

    fun searchModelsByYear(
        models: Flow<List<VehicleModel>>,
        year: Int
    ): Flow<List<VehicleModel>> {
        return models.map { list ->
            list.filter { model ->
                year >= model.yearStart && (model.yearEnd == null || year <= model.yearEnd)
            }
        }
    }

    fun findCompatibleEcuSystems(
        ecuSystems: Flow<List<EcuSystem>>,
        requiredFeatures: List<String>
    ): Flow<List<EcuSystem>> {
        return ecuSystems.map { list ->
            list.filter { ecuSystem ->
                requiredFeatures.all { feature ->
                    ecuSystem.diagnosticFeatures.contains(feature)
                }
            }
        }
    }

    fun searchParametersByUnit(
        parameters: Flow<List<DiagnosticParameter>>,
        unit: String
    ): Flow<List<DiagnosticParameter>> {
        return parameters.map { list ->
            list.filter { parameter ->
                parameter.unit.equals(unit, ignoreCase = true)
            }
        }
    }

    fun searchCommandsBySecurityLevel(
        commands: Flow<List<DiagnosticCommand>>,
        maxSecurityLevel: Int
    ): Flow<List<DiagnosticCommand>> {
        return commands.map { list ->
            list.filter { command ->
                command.securityLevelRequired <= maxSecurityLevel
            }
        }
    }

    companion object {
        const val DEFAULT_DISPLACEMENT_TOLERANCE = 100 // cc
        const val DEFAULT_POWER_TOLERANCE = 5 // kW
    }
}