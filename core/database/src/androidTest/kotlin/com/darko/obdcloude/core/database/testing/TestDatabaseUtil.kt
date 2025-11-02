package com.darko.obdcloude.core.database.testing

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.darko.obdcloude.core.database.VehicleDatabase
import com.darko.obdcloude.core.database.model.*
import kotlinx.coroutines.runBlocking

object TestDatabaseUtil {
    fun createTestDatabase(): VehicleDatabase {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VehicleDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    fun generateTestData(database: VehicleDatabase) = runBlocking {
        val manufacturer = VehicleManufacturer(
            id = "test_manufacturer",
            name = "Test Manufacturer",
            code = "TEST",
            ecuProtocols = listOf("UDS", "KWP2000")
        )

        val model = VehicleModel(
            id = "test_model",
            manufacturerId = manufacturer.id,
            name = "Test Model",
            code = "TM1",
            yearStart = 2023,
            yearEnd = null,
            platform = "TEST_PLATFORM",
            generation = "1"
        )

        val engine = VehicleEngine(
            id = "test_engine",
            modelId = model.id,
            code = "TEST_1.6",
            name = "Test Engine 1.6L",
            displacement = 1600,
            fuelType = "PETROL",
            power = 110,
            ecuType = "SID201",
            ecuVersion = "1.0",
            protocolIds = listOf("UDS")
        )

        val ecuSystem = EcuSystem(
            id = "test_ecu",
            engineId = engine.id,
            name = "Test ECU",
            code = "TEST_ECU",
            type = EcuType.ENGINE_CONTROL,
            supplier = "Test Supplier",
            version = "1.0",
            protocol = "UDS",
            securityLevel = 3,
            diagnosticFeatures = listOf("parameters", "actuators")
        )

        database.vehicleDao().apply {
            insertManufacturer(manufacturer)
            insertModel(model)
            insertEngine(engine)
            insertEcuSystem(ecuSystem)
        }
    }
}